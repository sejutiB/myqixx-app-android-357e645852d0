package qix.app.qix;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.cifrasoft.services.SoundCode;
import com.cifrasoft.services.SoundCodeListener;
import com.cifrasoft.services.SoundCodeSettings;

import java.util.HashMap;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import qix.app.qix.fragments.ListeningFragment;
import qix.app.qix.fragments.ShakeResultFragment;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.models.ShakeResponse;
import qix.app.qix.models.TransactionResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AudioScanningActivity extends AppCompatActivity implements SoundCodeListener {

    static final String TAG = "AudioScanningActivity";
    static final double[] coordinates = new double[2];
    private PowerManager.WakeLock mWakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("Listening...");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setContentView(R.layout.activity_audio_scan);

        mWakeLock = ((PowerManager) Objects.requireNonNull(getSystemService(Context.POWER_SERVICE))).newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass().getName());

        mWakeLock.acquire(120 * 1000L /*1 minutes*/);

        Helpers.switchFragment(this, new ListeningFragment(), R.id.scan_fragment);

        String userLocation = getIntent().getStringExtra("USER_LOCATION");

        assert (userLocation != null);
        coordinates[0] = Double.parseDouble(userLocation.split(",")[0]);
        coordinates[1] = Double.parseDouble(userLocation.split(",")[1]);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, 1);
            return;
        }

        new Handler().postDelayed(() -> {
            SoundCodeSettings scs = new SoundCodeSettings();
            scs.counterLength = 0;
            SoundCode.instance(getBaseContext()).prepare(scs, AudioScanningActivity.this, true);
        }, 500);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SoundCodeSettings scs = new SoundCodeSettings();
                scs.counterLength = 0;
                SoundCode.instance(this).prepare(scs, AudioScanningActivity.this, true);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            SoundCode.release();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetectedId(long[] result) {
        mWakeLock.release();
        SoundCode.release();

        Log.d(TAG, "RESULT " + result[2]);

        if (result[2] == 1) {

            Helpers.presentToast("ID: " + result[1], Toast.LENGTH_SHORT);

            Log.d(TAG, "SOUNDCODE DETECTED" +
                    "\nCONTENT ID  " + "[" + result[1] + "]" +
                    "\nCOUNTER        " + "[" + result[2] + "]" +
                    "\nTIMESTAMP   " + "[" + (float) ((result[3]) / 100) / 10 + "] sec.");

            HashMap<String, String> data = new HashMap<>();
            data.put("watermark", result[1] + "");
            data.put("latitude", coordinates[0] + "");
            data.put("longitude", coordinates[1] + "");

            Log.d(TAG, "Sending data: " + data.toString());

            if (BuildConfig.BUILD_VARIANT.equals("dev"))
                this.startShake(data);
            else
                this.startShakeAsync(data);

            /*AsyncRequest.shake(this, data, new Callback<ShakeResponse>() {
                @Override
                public void onResponse(@NonNull Call<ShakeResponse> call, @NonNull Response<ShakeResponse> response) {
                    if (response.isSuccessful()) {

                        ShakeResultFragment shakeFragment = new ShakeResultFragment();
                        Bundle bundle = new Bundle();
                        ShakeResponse result = response.body();

                        assert result != null;
                        if (result.hasWin()) {
                            Log.d(TAG, "You win!");

                            String imageUrl = result.getImageLink();

                            bundle.putBoolean("success", true);
                            bundle.putString("image", imageUrl);

                            String desc = result.getUserMessage();
                            bundle.putString("description", desc);
                            bundle.putInt("code", 200);

                        } else {
                            bundle.putBoolean("success", false);
                            bundle.putString("text", "Non hai vinto, riprova più tardi!");
                            bundle.putInt("code", 201);
                        }

                        shakeFragment.setArguments(bundle);
                        Helpers.switchFragment(AudioScanningActivity.this, shakeFragment, R.id.scan_fragment);
                    } else {
                        ShakeResultFragment shakeFragment = new ShakeResultFragment();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("success", false);
                        bundle.putString("image", null);
                        bundle.putInt("code", response.code());

                        switch (response.code()) {
                            case 404:
                                bundle.putString("text", getResources().getString(R.string.qixshake_error_404));
                                break;
                            case 406:
                                bundle.putString("text", getResources().getString(R.string.qixshake_error_406));
                                break;
                            case 410:
                                bundle.putString("text", getResources().getString(R.string.qixshake_error_410));
                                break;
                            case 420:
                                bundle.putString("text", getResources().getString(R.string.qixshake_error_420));
                                break;
                            case 500:
                                bundle.putString("text", getResources().getString(R.string.qixshake_error_500));
                                break;
                            default:
                                bundle.putString("text", getResources().getString(R.string.error_message_unknown));
                                break;
                        }

                        shakeFragment.setArguments(bundle);
                        Helpers.switchFragment(AudioScanningActivity.this, shakeFragment, R.id.scan_fragment);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ShakeResponse> call, @NonNull Throwable t) {
                    Log.d(TAG, "Errore " + t.getLocalizedMessage());
                }
            });*/
        }
    }

    private void startShakeAsync(HashMap<String, String> data) {
        AsyncRequest.shakeAsync(this, data, new Callback<TransactionResponse>() {
            @Override
            public void onResponse(@NonNull Call<TransactionResponse> call, @NonNull Response<TransactionResponse> response) {
                TransactionResponse result = response.body();

                if (response.isSuccessful()) {
                    assert result != null;
                    Log.d("TRANSACTION ID", result.getTransactionId());
                    startShakeTransaction(result.getTransactionId(), 0);
                } else {
                    // assert result != null;
                    ShakeResultFragment shakeFragment = new ShakeResultFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("success", false);
                    bundle.putString("image", null);
                    bundle.putInt("code", response.code());

                    shakeFragment.setArguments(bundle);
                    Helpers.switchFragment(AudioScanningActivity.this, shakeFragment, R.id.scan_fragment);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TransactionResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Errore 1" + t.getLocalizedMessage());
                ShakeResultFragment shakeFragment = new ShakeResultFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("success", false);
                bundle.putString("image", null);
                bundle.putInt("code", 500);

                shakeFragment.setArguments(bundle);
                Helpers.switchFragment(AudioScanningActivity.this, shakeFragment, R.id.scan_fragment);
            }
        });
    }

    private void startShake(HashMap<String, String> data) {
        AsyncRequest.shake(this, data, new Callback<ShakeResponse>() {
            @Override
            public void onResponse(@NonNull Call<ShakeResponse> call, @NonNull Response<ShakeResponse> response) {
                if (response.isSuccessful()) {
                    ShakeResponse result = response.body();
                    assert result != null;
                    if (!result.isSuccessfull()) {
                        ShakeResultFragment shakeFragment = new ShakeResultFragment();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("success", false);
                        bundle.putString("image", null);
                        bundle.putInt("code", 500);
                        shakeFragment.setArguments(bundle);
                        Helpers.switchFragment(AudioScanningActivity.this, shakeFragment, R.id.scan_fragment);
                    } else {
                        ShakeResultFragment shakeFragment = new ShakeResultFragment();
                        Bundle bundle = new Bundle();
                        if (result.hasWin()) {
                            Log.d(TAG, "You win!");
                            String imageUrl = result.getImageLink();
                            bundle.putBoolean("success", true);
                            bundle.putString("image", imageUrl);
                            String desc = result.getUserMessage();
                            bundle.putString("description", desc);
                            bundle.putInt("code", 200);
                        } else {
                            bundle.putBoolean("success", false);
                            bundle.putString("text", "Non hai vinto, riprova più tardi!");
                            bundle.putInt("code", 201);
                        }
                        shakeFragment.setArguments(bundle);
                        Helpers.switchFragment(AudioScanningActivity.this, shakeFragment, R.id.scan_fragment);
                    }
                } else {
                    ShakeResultFragment shakeFragment = new ShakeResultFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("success", false);
                    bundle.putString("image", null);
                    bundle.putInt("code", response.code());
                    shakeFragment.setArguments(bundle);
                    Helpers.switchFragment(AudioScanningActivity.this, shakeFragment, R.id.scan_fragment);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ShakeResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Errore " + t.getLocalizedMessage());
            }
        });
    }

    private void startShakeTransaction(final String transactionId, final int times) {
        AsyncRequest.shakeAsyncResult(this, transactionId, new Callback<ShakeResponse>() {
            @Override
            public void onResponse(@NonNull Call<ShakeResponse> call, @NonNull Response<ShakeResponse> response) {
                if (response.isSuccessful()) {
                    ShakeResponse result = response.body();
                    assert result != null;
                    if (!result.isSuccessfull()) {
                        if (times < 20) {
                            Log.d("Tentivo", (times + 1) + " di 20");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startShakeTransaction(transactionId, times + 1);
                                }
                            }, 4000);
                        } else {
                            ShakeResultFragment shakeFragment = new ShakeResultFragment();
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("success", false);
                            bundle.putString("image", null);
                            bundle.putInt("code", 500);

                            shakeFragment.setArguments(bundle);
                            Helpers.switchFragment(AudioScanningActivity.this, shakeFragment, R.id.scan_fragment);
                        }
                    } else {
                        ShakeResultFragment shakeFragment = new ShakeResultFragment();
                        Bundle bundle = new Bundle();

                        if (result.hasWin()) {
                            Log.d(TAG, "You win!");

                            String imageUrl = result.getImageLink();

                            bundle.putBoolean("success", true);
                            bundle.putString("image", imageUrl);

                            String desc = result.getUserMessage();
                            bundle.putString("description", desc);
                            bundle.putInt("code", 200);

                        } else {
                            bundle.putBoolean("success", false);
                            bundle.putString("text", "You didn't win, try again later!");
                            bundle.putInt("code", 201);
                        }

                        shakeFragment.setArguments(bundle);
                        Helpers.switchFragment(AudioScanningActivity.this, shakeFragment, R.id.scan_fragment);
                    }
                } else {
                    ShakeResultFragment shakeFragment = new ShakeResultFragment();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("success", false);
                    bundle.putString("image", null);
                    bundle.putInt("code", response.code());

                    shakeFragment.setArguments(bundle);
                    Helpers.switchFragment(AudioScanningActivity.this, shakeFragment, R.id.scan_fragment);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ShakeResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "Errore 2" + t.getLocalizedMessage());
                ShakeResultFragment shakeFragment = new ShakeResultFragment();
                Bundle bundle = new Bundle();
                bundle.putBoolean("success", false);
                bundle.putString("image", null);
                bundle.putInt("code", 500);

                shakeFragment.setArguments(bundle);
                Helpers.switchFragment(AudioScanningActivity.this, shakeFragment, R.id.scan_fragment);
            }
        });
    }

    @Override
    protected void onPause() {
        if (isFinishing()) {
            SoundCode.release();
        }
        super.onPause();
    }

    @Override
    public void onAudioInitFailed() {
        runOnUiThread(() -> Helpers.presentToast("Audio error", Toast.LENGTH_SHORT));
        Log.d(TAG, "Errore AUDIO sconosciuto");
    }
}
