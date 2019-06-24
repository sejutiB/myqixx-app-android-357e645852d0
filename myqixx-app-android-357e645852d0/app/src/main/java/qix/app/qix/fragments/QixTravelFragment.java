package qix.app.qix.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import qix.app.qix.R;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.CryptoHandler;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.models.ProfileResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QixTravelFragment extends Fragment {

    WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_webview, container, false);


        webView = view.findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);

        this.setUpWebView();

        return view;
    }


    private void setUpWebView(){

        AsyncRequest.getProfileData(getActivity(), new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if(response.isSuccessful()){
                    ProfileResponse data = response.body();
                    String userId = data.getSub();

                    long time = System.currentTimeMillis();

                    String m = "{\"user_id\":\"" + userId + "\",\"timestamp\":"+time+"}";
                    Log.d("Messagio: ", m);

                    String token = CryptoHandler.encrypt(m);
                    Log.d("Token: ", token);

                    String encodedToken = null;

                    try {
                        encodedToken = URLEncoder.encode(token, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    String url = Constants.KALIGO_SIGN_IN_URL + encodedToken;

                    Log.d("URL", url);

                    webView.setWebViewClient(new WebViewClient());
                    webView.loadUrl(url);
                }else{
                    Helpers.presentToast("Cannot retrieve data", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                Helpers.presentToast(R.string.no_connectio_error_message, Toast.LENGTH_SHORT);
            }
        });
    }

}
