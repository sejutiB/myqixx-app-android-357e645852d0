package qix.app.qix.helpers;

import android.app.Activity;

import com.google.gson.Gson;
import com.shopify.buy3.GraphClient;
import com.shopify.buy3.HttpCachePolicy;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import okhttp3.RequestBody;
import qix.app.qix.helpers.interfaces.QixEndpointInterface;
import qix.app.qix.models.BalanceResponse;
import qix.app.qix.models.CategoryResponse;
import qix.app.qix.models.ChangePasswordRequest;
import qix.app.qix.models.CheckoutResponse;
import qix.app.qix.models.EmailVerifiyResponse;
import qix.app.qix.models.ExchangeRateResponse;
import qix.app.qix.models.GatewayCheckRequest;
import qix.app.qix.models.KaligoResponse;
import qix.app.qix.models.LoginResponse;
import qix.app.qix.models.LoginRequest;

import qix.app.qix.models.MessageResponse;
import qix.app.qix.models.OfferResponse;
import qix.app.qix.models.PartnerResponse;
import qix.app.qix.models.PaypalTransactionRequest;
import qix.app.qix.models.ProfileResponse;
import qix.app.qix.models.QuickTransactionRequest;
import qix.app.qix.models.RecoveryPasswordRequest;
import qix.app.qix.models.RefreshTokenRequest;
import qix.app.qix.models.RefreshTokenResponse;
import qix.app.qix.models.SendVerificationLink;
import qix.app.qix.models.ShakeRequest;
import qix.app.qix.models.ShakeResponse;
import qix.app.qix.models.SignupRequest;
import qix.app.qix.models.StatusResponse;
import qix.app.qix.models.SuccessResponse;
import qix.app.qix.models.TransactionArrayResponse;
import qix.app.qix.models.TransactionRequest;
import qix.app.qix.models.TransactionResponse;
import qix.app.qix.models.TransactionStatusResponse;
import qix.app.qix.models.UpdateProfileRequest;
import qix.app.qix.models.VerificationResponse;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Classe per di richieste HTTP
 */
public class AsyncRequest {

    private static OkHttpClient.Builder mHttpClient = null;

    private static Retrofit mRetrofit = null;

    public static GraphClient getClient(Activity activity){
        return GraphClient.builder(activity)
                .shopDomain(Constants.getShopInfo().shopDomain)
                .accessToken(Constants.getShopInfo().apiKey)
                .httpClient(mHttpClient.build())
                .httpCache(new File(activity.getCacheDir(), "/http"), 10 * 1024 * 1024) // 10mb for http cache
                .defaultHttpCachePolicy(HttpCachePolicy.CACHE_FIRST.expireAfter(5, TimeUnit.MINUTES)) // cached response valid by default for 5 minutes
                .build();
    }

    public AsyncRequest() {
        // Enforce Singleton
    }

    private static Retrofit.Builder mBuilder = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create());

    private static QixEndpointInterface getApi(Activity activity) {
        return getApi(activity, true);
    }

    private static QixEndpointInterface getApi(Activity activity, boolean hasToken) {
        return getApi(activity, hasToken, Constants.QIX_BASE_URL);
    }

    private static QixEndpointInterface getApi(Activity activity, boolean hasToken, String url) {

        if (mHttpClient == null) {
            mHttpClient = new OkHttpClient.Builder();
        }

        mHttpClient.addInterceptor(new TokenInterceptor(activity, getTokenManager(hasToken)));

        OkHttpClient client = mHttpClient.build();

        if ((mRetrofit == null) || (!mRetrofit.baseUrl().toString().equals(url))) {
            mRetrofit = mBuilder.baseUrl(url)
                    .client(client)
                    .build();
        }
        return mRetrofit.create(QixEndpointInterface.class);
    }


    protected static class TokenManager {
        private boolean hasToken;
        private String idToken;
        private String refreshToken;
        private boolean isLogged;

        public boolean  hasToken() {
            return hasToken;
        }

        public String getIdToken() {
            return idToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public boolean isLogged() {
            return isLogged;
        }

        void setHasToken(boolean hasToken) {
            this.hasToken = hasToken;
        }

         void setIdToken(String idToken) {
            this.idToken = idToken;
        }

         void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

         void setIsLogged(boolean logged) {
            isLogged = logged;
        }
    }

    private static TokenManager getTokenManager(final boolean useToken){
        TokenManager t = new TokenManager();
        t.setHasToken(useToken);
        t.setIdToken(Configuration.getIdToken());
        t.setRefreshToken(Configuration.getRefreshToken());
        t.setIsLogged(Configuration.isLogged());

        return t;
    }

    /** PUBLIC METHODS **/

    public static void login(Activity activity, String username, String password, Callback<LoginResponse> cb) {
        LoginRequest credentials = new LoginRequest();
        credentials.setUsername(username);
        credentials.setPassword(password);
        getApi(activity, false).login(credentials).enqueue(cb);
    }

    public static void resendVerifyLink(Activity activity, String username, Callback<VerificationResponse> cb) {
        SendVerificationLink email = new SendVerificationLink();
        email.setUsername(username);
        getApi(activity, false).resendVerifyLink(email).enqueue(cb);
    }

    public static void getBalace(Activity activity, Callback<BalanceResponse> cb){
        getApi(activity).getBalance(getIdToken()).enqueue(cb);
    }

    public static void refreshToken(Activity activity, String refreshToken, Callback<RefreshTokenResponse> cb){
        RefreshTokenRequest r = new RefreshTokenRequest();
        r.setRefreshToken(refreshToken);
        getApi(activity, false).refreshToken(r).enqueue(cb);
    }

    public static void startTransaction(Activity activity, Constants.PaymentType paymentType, HashMap<String, Object> data, Callback<SuccessResponse> cb){
        TransactionRequest transactionRequest = new TransactionRequest(data);
        switch (paymentType){
            case IN_APP_PAYMENT:
                getApi(activity).startTransaction(getIdToken(), transactionRequest).enqueue(cb);
                break;
            case IN_STORE_PAYMENT:
                getApi(activity).startInstoreTransaction(getIdToken(), transactionRequest).enqueue(cb);
                break;
        }
    }

    public static void startQuickTransaction(Activity activity, HashMap<String, String> data, Callback<SuccessResponse> cb){
        QuickTransactionRequest transactionRequest = new QuickTransactionRequest(data);
        getApi(activity).startQuickTransaction(getIdToken(), transactionRequest).enqueue(cb);
    }

    public static void startPaypalTransaction(Activity activity, String orderId, Callback<SuccessResponse> cb){
        PaypalTransactionRequest transactionRequest = new PaypalTransactionRequest(orderId);
        getApi(activity).startPaypalTransaction(getIdToken(), transactionRequest).enqueue(cb);
    }

    public static void getExchangeRate(Activity activity, String from, String to, Callback<ExchangeRateResponse> cb){
        getApi(activity).getExchangeRate(getIdToken(), from, to).enqueue(cb);
    }

    public static void getPartners(Activity activity, Double latitude, Double longitude, Integer radius, Callback<List<PartnerResponse>> cb){
        getApi(activity).getPartners(getIdToken(), latitude, longitude, radius).enqueue(cb);
    }

    public static void getPartnersCategories(Activity activity, Callback<List<CategoryResponse>> cb){
        getApi(activity).getPartnersCategories(getIdToken()).enqueue(cb);
    }

    public static void getBuyStatus(Activity activity, String transactionId, Callback<StatusResponse> cb){
        getApi(activity).getBuyStatus(getIdToken(), transactionId).enqueue(cb);
    }

    public static void buy(Activity activity, String checkoutId, Callback<TransactionResponse> cb){
        GatewayCheckRequest adyenCheckRequest = new GatewayCheckRequest();
        adyenCheckRequest.setGatewayCheck(checkoutId);
        getApi(activity).buy(getIdToken(), adyenCheckRequest).enqueue(cb);
    }

    public static void getProfileData(Activity activity, Callback<ProfileResponse> cb){
        getApi(activity).getProfileData(getIdToken()).enqueue(cb);
    }

    public static void updateProfileData(Activity activity, UpdateProfileRequest requestData, Callback<MessageResponse> cb){
        getApi(activity).updateProfileData(getIdToken(), requestData).enqueue(cb);
    }

    public static void uploadUserImage(Activity activity, String ext, RequestBody body, Callback<SuccessResponse> cb){
        getApi(activity).uploadUserImage(getIdToken(), "image/" + ext, body).enqueue(cb);
    }

    public static void sendGatewayCheck(Activity activity, String transactionId, String checkoutId, Callback<MessageResponse> cb){
        GatewayCheckRequest gatewayCheckRequest = new GatewayCheckRequest();
        gatewayCheckRequest.setGatewayCheck(checkoutId);
        getApi(activity).sendGatewayCheck(getIdToken(), transactionId, gatewayCheckRequest).enqueue(cb);
    }

    public static void getAllTransactions(Activity activity, String from, String to, String lastEvaluatedKey, Callback<TransactionArrayResponse> cb){
        getApi(activity).getAllTransactions(getIdToken(), from, to, lastEvaluatedKey).enqueue(cb);
    }

    public static void getTransactionStatus(Activity activity, String transactionId, Callback<TransactionStatusResponse> cb){
        getApi(activity).getTransactionStatus(getIdToken(), transactionId).enqueue(cb);
    }

    public static void shake(Activity activity, HashMap<String, String> data, Callback<ShakeResponse> cb){
        ShakeRequest shakeRequest = new ShakeRequest(data);
        getApi(activity).shake(getIdToken(), shakeRequest).enqueue(cb);
    }

    public static void shakeAsync(Activity activity, HashMap<String, String> data, Callback<TransactionResponse> cb){
        ShakeRequest shakeRequest = new ShakeRequest(data);
        getApi(activity).shakeAsync(getIdToken(), shakeRequest).enqueue(cb);
    }

    public static void shakeAsyncResult(Activity activity, String transactionId, Callback<ShakeResponse> cb){
        getApi(activity).shakeAsyncResult(getIdToken(), transactionId).enqueue(cb);
    }

    public static  void getOffers(Activity activity, Double latitude, Double longitude, Integer radius, Callback<List<OfferResponse>> cb){
        getApi(activity).getOffers(getIdToken(), latitude, longitude, radius).enqueue(cb);
    }

    public static void loginWithKaligo(Activity activity, HashMap<String, Object> data, Callback<KaligoResponse> cb){
        Gson gson = new Gson();
        String jsonString = gson.toJson(data);
        String base64 = CryptoHandler.encrypt(jsonString);
        getApi(activity, false, Constants.KALIGO_SIGN_IN_URL).loginWithKaligo(base64).enqueue(cb);
    }

    public static void getCheckoutId(Activity activity, String amount, String currency, Callback<CheckoutResponse> cb){
        getApi(activity, false, Constants.PAYDOO_BASE_URL).getCheckoutId(Constants.PAYDOO_USER_ID, Constants.PAYDOO_PASSWORD, Constants.PAYDOO_ENTITY_ID, amount, currency,"DB").enqueue(cb);
    }

    public static  void signUp(Activity activity, HashMap<String, String> data, Callback<MessageResponse> cb){
        SignupRequest signupRequest = new SignupRequest(data);
        getApi(activity, false).signup(signupRequest).enqueue(cb);
    }

    public static void verifyEmail(Activity activity, String email, Callback<EmailVerifiyResponse> cb){
        getApi(activity, false).verifyEmail(email).enqueue(cb);
    }

    public static void getRecoveryPasswordCode(Activity activity, String email, Callback<MessageResponse> cb){
        RecoveryPasswordRequest r = new RecoveryPasswordRequest(email);
        getApi(activity, false).getRecoveryPasswordCode(r).enqueue(cb);
    }

    public static void changePassword(Activity activity, String email, String password, String code, Callback<MessageResponse> cb){
        ChangePasswordRequest r = new ChangePasswordRequest();
        r.setConfirmationCode(code);
        r.setEmail(email);
        r.setPassword(password);
        getApi(activity, false).changePassword(r).enqueue(cb);
    }

    private static String getIdToken(){
        return Configuration.getIdToken();
    }

}


