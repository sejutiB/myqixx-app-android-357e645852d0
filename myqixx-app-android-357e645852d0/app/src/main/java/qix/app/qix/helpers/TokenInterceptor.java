package qix.app.qix.helpers;

import android.app.Activity;
import android.util.Log;

import java.io.IOException;

import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import qix.app.qix.CoordinatorActivity;
import qix.app.qix.R;
import qix.app.qix.helpers.interfaces.QixEndpointInterface;
import qix.app.qix.models.RefreshTokenRequest;
import qix.app.qix.models.RefreshTokenResponse;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TokenInterceptor implements Interceptor {
    private AsyncRequest.TokenManager tokenManager;
    private Activity activity;

    private static final String TAG = "TokenInterceptor";
    private int tent = 0;

    TokenInterceptor(Activity activity, AsyncRequest.TokenManager manager) {
        this.tokenManager = manager;
        this.activity = activity;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        if(Configuration.isLogged() || Configuration.isAlreadyLogged()){

            if(Helpers.getPreference(R.string.preference_id_token_expiration_key) == null || Helpers.getPreference(R.string.preference_refresh_token_expiration_key) == null){
                reauthanticate();
                tent++;
                return response;
            }

            if(hasToken(request)){
                long idTokenExpirationTime = Integer.parseInt(Helpers.getPreference(R.string.preference_id_token_expiration_key));
                long refreshTokenExpirationTime = Integer.parseInt(Helpers.getPreference(R.string.preference_refresh_token_expiration_key));
                long currentTime = System.currentTimeMillis() / 1000;

                if(refreshTokenExpirationTime <= currentTime){
                    Log.d(TAG, "Login id needed!");
                    reauthanticate();
                    tent++;
                    return response;
                }else if(idTokenExpirationTime <= currentTime){
                    Log.d(TAG, "ID token has expired, need to refresh the tokens");

                    Retrofit r = new Retrofit.Builder()
                            .baseUrl(Constants.QIX_BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    QixEndpointInterface api = r.create(QixEndpointInterface.class);

                    RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest();
                    refreshTokenRequest.setRefreshToken(tokenManager.getRefreshToken());

                    retrofit2.Response<RefreshTokenResponse> refreshTokenResponseAsync = api.refreshToken(refreshTokenRequest).execute();
                    RefreshTokenResponse refreshTokenResponse = refreshTokenResponseAsync.body();

                    if(refreshTokenResponseAsync.isSuccessful()){
                        assert refreshTokenResponse != null;
                        Helpers.writePreferences(refreshTokenResponse);

                        // create a new request and modify it accordingly using the new token
                        Request newRequest = request.newBuilder().addHeader("Authorization", refreshTokenResponse.getIdToken()).build();
                        Log.d(TAG, "Token refreshed : "+refreshTokenResponse.getIdToken());
                        tent++;
                        return chain.proceed(newRequest);
                    }else{
                        Log.d(TAG, "Can't get the token, login needed");
                        reauthanticate();
                        tent++;
                        return response;
                    }

                }
            }
        }

        if(hasToken(request)){
            Log.d(TAG, "Have a valid Token! " + tent + "\n"+request.url());
        }else{
            Log.d(TAG, "No token needed! \n"+request.url());
        }

        tent++;

        return response;
    }

    private void reauthanticate(){
        Helpers.deleteAllPreferences();
        Helpers.setPreference(R.string.preference_already_logged_key, true);
        //Helpers.startNewActivityAndKillOthers(CoordinatorActivity.class);
    }

    private boolean hasToken(Request r){
        return r.header("Authorization") != null;
    }
}
