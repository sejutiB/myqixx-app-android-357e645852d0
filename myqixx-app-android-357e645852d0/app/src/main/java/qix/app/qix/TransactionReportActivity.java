package qix.app.qix;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.helpers.adapters.TransactionReportAdapter;
import qix.app.qix.models.Transaction;
import qix.app.qix.models.TransactionArrayResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionReportActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private TransactionReportAdapter adapter;

    @BindView(R.id.transactionSwipeLayout)
    SwipeRefreshLayout pullToRefresh;

    @BindView(R.id.startDateTransaction)
    EditText dateRange;

    @BindView(R.id.applyTransactionButton)
    Button applyButton;

    @BindView(R.id.transactionListView)
    ListView listView;

    @BindView(R.id.notFoundTransactionText)
    TextView noElementsText;

    @BindView(R.id.qixNumText)
    TextView qixPoints;

    private ProgressBar footerProgressBar;

    private List<Transaction> transactions = new ArrayList<>();
    private Date startDate;
    private Date endDate;
    private String lastEvaluatedKey = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Transactions");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_transactions_report);

        ButterKnife.bind(this);

        listView.setEmptyView(noElementsText);
        adapter = new TransactionReportAdapter(this,null);
        listView.setAdapter(adapter);
        setListViewFooter(listView);

        footerProgressBar.setTag(false);

        pullToRefresh.setEnabled(true);
        pullToRefresh.setOnRefreshListener(this);

        getTransactions(false);

        qixPoints.setText("--");

        Helpers.updateBalance(this, qixPoints);

        // Disattivo l'editText
        dateRange.setKeyListener(null);

        final SmoothDateRangePickerFragment smoothDateRangePickerFragment = SmoothDateRangePickerFragment.newInstance(new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
            @Override
            public void onDateRangeSet(SmoothDateRangePickerFragment view,
                                       int yearStart, int monthStart,
                                       int dayStart, int yearEnd,
                                       int monthEnd, int dayEnd) {

                startDate = new GregorianCalendar(yearStart, monthStart, dayStart, 0, 0).getTime();
                endDate = new GregorianCalendar(yearEnd, monthEnd, dayEnd, 0, 0).getTime();

                Log.d("aaa", startDate.toString());
                dateRange.setText(String.format("%s - %s", Helpers.getStringFrom(startDate, "dd/MM/yyyy"), Helpers.getStringFrom(endDate, "dd/MM/yyyy")));
                dateRange.clearFocus();
            }
        });

        smoothDateRangePickerFragment.setOnCancelListener(dialogInterface -> dateRange.clearFocus());

        dateRange.setOnFocusChangeListener((view, hasFocus) -> {
            if(hasFocus){
                smoothDateRangePickerFragment.show(getFragmentManager(), "smoothDateRangePicker");
            }
        });

        applyButton.setOnClickListener(view -> {
            if(!dateRange.getText().toString().isEmpty()){
                transactions.clear();
                getAllTransaction(startDate, endDate, null);
            }else{
                Helpers.presentToast("Select a date range", Toast.LENGTH_SHORT);
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(transactions != null && transactions.size() > 0)
                {
                    if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount != 0)
                    {
                        if(footerProgressBar.getVisibility() == View.GONE)
                        {
                            footerProgressBar.setVisibility(View.VISIBLE);
                            getTransactions(true);
                        }
                    }
                }
            }
        });

        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(getApplicationContext(), TransactionDetailsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra("transaction", transactions.get(i));

            startActivity(intent);
        });

    }

    private void setListViewFooter(ListView listView){
        View view = LayoutInflater.from(this).inflate(R.layout.layout_transaction_listview_footer, null);
        footerProgressBar = view.findViewById(R.id.footerProgressBar);
        footerProgressBar.setVisibility(View.GONE);
        listView.addFooterView(footerProgressBar);
    }

    private void getTransactions(boolean nextPage){
        if(!nextPage || canGetNextPage()){
            getAllTransaction(startDate, endDate, nextPage ? lastEvaluatedKey : null);
        }
    }

    private boolean canGetNextPage(){

        if(lastEvaluatedKey != null && lastEvaluatedKey.isEmpty()){
            footerProgressBar.setVisibility(View.GONE);
            return false;
        }

        return true;
    }

    private void getAllTransaction(Date from, Date to, final String lastKey){
        if(!pullToRefresh.isRefreshing())
            pullToRefresh.setRefreshing(true);

        /* Caso in cui lastKey è una stringa vuota, ossia siamo all'ultima pagina */
        if(lastKey != null && lastKey.isEmpty()) {
            footerProgressBar.setVisibility(View.GONE);
            pullToRefresh.setRefreshing(false);
            return;
        }


        String start = Helpers.getIsoStringFrom(from);
        String end = Helpers.getIsoStringFrom(to);

        AsyncRequest.getAllTransactions(this, start, end, lastKey, new Callback<TransactionArrayResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransactionArrayResponse> call, @NonNull Response<TransactionArrayResponse> response) {
                if(response.isSuccessful()){
                    TransactionArrayResponse result = response.body();

                    assert result != null;

                    if(lastEvaluatedKey != null){
                        transactions.addAll(result.getTransactions());
                        //adapter.setTransactionData(transactions);
                    }else{
                        transactions = result.getTransactions();
                        //adapter.setTransactionData(transactions);
                    }

                    adapter.setTransactionData(transactions);

                    lastEvaluatedKey = result.getLastEvaluatedKey();

                    adapter.notifyDataSetChanged();

                }else{
                    Helpers.presentToast("No transactions!", Toast.LENGTH_SHORT);
                }

                if(lastKey != null)
                    footerProgressBar.setVisibility(View.GONE);

                pullToRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(@NonNull Call<TransactionArrayResponse> call, @NonNull Throwable t) {
                pullToRefresh.setRefreshing(false);
               Helpers.presentToast("Error "+t.getLocalizedMessage(), Toast.LENGTH_SHORT);
                if(lastKey != null)
                    footerProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        lastEvaluatedKey = null;
        getTransactions(false);
    }
}
