package qix.app.qix.helpers.interfaces;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import qix.app.qix.models.AdyenCheckResponse;
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
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface QixEndpointInterface {

    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest credentials);

    @POST("password/verification_email")
    Call<VerificationResponse> resendVerifyLink(@Body SendVerificationLink email);

    @POST("refreshtoken")
    Call<RefreshTokenResponse> refreshToken(@Body RefreshTokenRequest refreshTokenRequest);

    @GET("wallet/balance")
    Call<BalanceResponse> getBalance(@Header("Authorization") String idToken);

    @POST("service/shake")
    Call<ShakeResponse> shake(@Header("Authorization") String idToken, @Body ShakeRequest shakeRequest);

    @POST("service/shake/async")
    Call<TransactionResponse> shakeAsync(@Header("Authorization") String idToken, @Body ShakeRequest shakeRequest);

    @GET("service/shake/async/{transactionId}")
    Call<ShakeResponse> shakeAsyncResult(@Header("Authorization") String idToken, @Path("transactionId") String transactionId);

    @POST("payments")
    Call<SuccessResponse> startTransaction(@Header("Authorization") String idToken, @Body TransactionRequest transactionRequest);

    @POST("payments/qrcode")
    Call<SuccessResponse> startQuickTransaction(@Header("Authorization") String idToken, @Body QuickTransactionRequest transactionRequest);

    @POST("payments/instore")
    Call<SuccessResponse> startInstoreTransaction(@Header("Authorization") String idToken, @Body TransactionRequest transactionRequest);

    @POST("payments/marketplace")
    Call<SuccessResponse> startPaypalTransaction(@Header("Authorization") String idToken, @Body PaypalTransactionRequest transactionRequest);

    @GET("exchangerate")
    Call<ExchangeRateResponse> getExchangeRate(@Header("Authorization") String idToken, @Query("fromCurrency") String fromCurrency, @Query("toCurrency") String toCurrency);

    @GET("payments/{transactionId}")
    Call<TransactionStatusResponse> getTransactionStatus(@Header("Authorization") String idToken, @Path("transactionId") String transactionId);

    @GET("transactions")
    Call<TransactionArrayResponse> getAllTransactions(@Header("Authorization") String idToken, @Query("from") String fromIsoDate, @Query("to") String toIsoDate, @Query("lastEvaluatedKey") String lastEvaluatedKey);

    @POST("payments/{transactionId}")
    Call<MessageResponse> sendGatewayCheck(@Header("Authorization") String idToken, @Path("transactionId") String transactionId, @Body GatewayCheckRequest adyenCheckRequest);

    @GET("me")
    Call<ProfileResponse> getProfileData(@Header("Authorization") String idToken);

    @POST("me")
    Call<MessageResponse> updateProfileData(@Header("Authorization") String idToken, @Body UpdateProfileRequest updateProfileRequest);

    //@Multipart
    @PUT("me/profilepicture")
    Call<SuccessResponse> uploadUserImage(@Header("Authorization") String idToken, @Header("Content-Type") String contentType, @Body RequestBody file);

    @POST("buy")
    Call<TransactionResponse> buy(@Header("Authorization") String idToken, @Body GatewayCheckRequest adyenCheckRequest);

    @GET("buy/{transactionId}")
    Call<StatusResponse> getBuyStatus(@Header("Authorization") String idToken, @Path("transactionId") String transactionId);

    @GET("pos")
    Call<List<PartnerResponse>> getPartners(@Header("Authorization") String idToken, @Query("latitude") Double latitude, @Query("longitude") Double longitude, @Query("radius") Integer radius);

    @GET("poscategories")
    Call<List<CategoryResponse>> getPartnersCategories(@Header("Authorization") String idToken);

    @POST("signup")
    Call<MessageResponse> signup(@Body SignupRequest signupRequest);

    @GET("signup/email")
    Call<EmailVerifiyResponse> verifyEmail(@Query("email") String email);

    @GET("offers")
    Call<List<OfferResponse>> getOffers(@Header("Authorization") String idToke, @Query("latitude") Double latitude, @Query("longitude") Double longitude, @Query("radius") Integer radius);

    @POST("password")
    Call<MessageResponse> getRecoveryPasswordCode(@Body RecoveryPasswordRequest recoveryPasswordRequest);

    @PUT("password")
    Call<MessageResponse> changePassword(@Body ChangePasswordRequest changePasswordRequest);

    @POST("sign_in")
    Call<KaligoResponse> loginWithKaligo(@Query("login") String token);

    @POST("checkouts")
    Call<CheckoutResponse> getCheckoutId(
            @Query("authentication.userId") String userId,
            @Query("authentication.password") String password,
            @Query("authentication.entityId") String entityId,
            @Query("amount") String amount,
            @Query("currency") String currency,
            @Query("paymentType") String paymentType);
}
