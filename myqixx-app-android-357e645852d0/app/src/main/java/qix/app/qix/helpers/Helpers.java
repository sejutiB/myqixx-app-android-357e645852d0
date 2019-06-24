package qix.app.qix.helpers;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import qix.app.qix.LoginActivity;
import qix.app.qix.MainApplication;
import qix.app.qix.R;
import qix.app.qix.models.BalanceResponse;
import qix.app.qix.models.LoginResponse;
import qix.app.qix.models.RefreshTokenResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

/**
 * La classe Helpers contiene funzioni richiamabili globalmente,
 * utili per diversi scopi
 */
public class Helpers {
    private static final String TAG = "Helpers";

    /**
     * setPreference: Setta una coppia chiave/valore nelle shared preferences
     * @param key int         REQUIRED identificativo della stringa chiave della risorsa.
     * @param defaultLanguage String
     */
    public static String getPreference(Context context, @StringRes Integer key, String defaultLanguage) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String keyString = Resources.getSystem().getString(key);
        return preferences.getString(keyString, defaultLanguage);
    }


    /**
     * getPreference: Ritorna una stringa per una chiave, salvata nelle shared preferences
     * @param key int REQUIRED identificativo della stringa chiave della risorsa.
     */
    public static void setPreference(@StringRes Integer key, String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(MainApplication.getAppContext().getString(key), value);
        editor.apply();
    }

    /**
     * getPreference: Ritorna una stringa per una chiave, salvata nelle shared preferences
     * @param key int REQUIRED identificativo della stringa chiave della risorsa.
     * @param value boolean REQUIRED Valore da assegnare alla chiave
     */
    public static void setPreference(@StringRes Integer key, boolean value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainApplication.getAppContext());
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(MainApplication.getAppContext().getString(key), value);
        editor.apply();
    }

    /**
     * getPreference: Ritorna una stringa per una chiave, salvata nelle shared preferences
     * @param key int REQUIRED identificativo della stringa chiave della risorsa.
     * @return String Ritorna l'oggetto richiesto o null
     */
    public static String getPreference( @StringRes int key){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainApplication.getAppContext());
        return sharedPref.getString(MainApplication.getAppContext().getString(key), null);
    }

    /**
     * getPreference: Ritorna una stringa per una chiave, salvata nelle shared preferences
     * @param key int REQUIRED identificativo della stringa chiave della risorsa.
     * @return boolean Ritorna l'oggetto richiesto o null
     */
    public static boolean getBooleanPreference(@StringRes int key){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainApplication.getAppContext());
        return sharedPref.getBoolean(MainApplication.getAppContext().getString(key), false);
    }


    /**
     * Data un data in formato string e il suo formato, ritorna true se la persona nata in
     * questa data ha più di 18 anni
     * @param birthDateString String
     * @return boolean
     */
    public static boolean isAdult(String birthDateString, String format){
        if(birthDateString == null)
            return false;

        Date d = null;

        try {
            d = getDateFrom(birthDateString, format);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(d == null)
           return false;

        Long current = System.currentTimeMillis();
        Long birthMillis = d.getTime();
        Long years = (current - birthMillis) / 1000 / 60 / 60 / 24 / 365;

        return years >= Constants.QIXSIGNUP_MIN_AGE_REQUIRED;
    }

    /**
     * deletePreference: Cancella una coppia chiave/valore dalle shared preferences
     * @param key int REQUIRED identificativo della stringa chiave della risorsa
     */
    private static void deletePreference(@StringRes int key){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainApplication.getAppContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(MainApplication.getAppContext().getString(key));
        editor.apply();
    }

    /**
     * EFFECT: Fa apparire un toast
     * @param message String    REQUIRED Messaggio da mostrare
     * @param length int        REQUIRED Durata del messaggio (Toast.LENGTH_LONG, Toast.LENGTH_SHORT)
     */
    public static void presentToast(String message, int length){
        Toast toast = Toast.makeText(MainApplication.getAppContext(), message, length);
        toast.show();
    }

    /**
     * EFFECT: Fa apparire un toast
     * @param message int    REQUIRED Messaggio da mostrare
     * @param length int        REQUIRED Durata del messaggio (Toast.LENGTH_LONG, Toast.LENGTH_SHORT)
     */
    public static void presentToast(@StringRes int message, int length){
        Toast toast = Toast.makeText(MainApplication.getAppContext(), MainApplication.getAppContext().getString(message), length);
        toast.show();
    }

    /**
     * EFFECT: Scrive / Sovrascrive tutte le shared preferences di gestione login e Api
     * @param result LoginResponse REQUIRED Valore ritornato dalle API
     */
    public static void writePreferences(LoginResponse result){
         long currentTime = System.currentTimeMillis() / 1000;
         long expirationTimestamp = (result.getExpiresIn() == null ? 3600 :  result.getExpiresIn()) + currentTime;

        setPreference(R.string.preference_is_logged_key, true);
        setPreference(R.string.preference_refresh_token_key, result.getRefreshToken());
        setPreference(R.string.preference_refresh_token_expiration_key,  String.valueOf(currentTime + 24 * 60 * 60));

        setPreference(R.string.preference_access_token_key, result.getAccessToken());
        setPreference(R.string.preference_id_token_key, result.getIdToken());
        setPreference(R.string.preference_token_duration_key, result.getExpiresIn().toString());
        setPreference(R.string.preference_id_token_expiration_key,  String.valueOf(expirationTimestamp));


        Log.d(TAG, "LOGGED IN" +
                "\nNEW DATA"+
                "\nAccessToken: " + getPreference(R.string.preference_access_token_key) +
                "\nrefreshToken: " + getPreference(R.string.preference_refresh_token_key) +
                "\ntokenId: "+ getPreference(R.string.preference_id_token_key) +
                "\nidTokenExpireTime: " + getPreference(R.string.preference_id_token_expiration_key) +
                "\nrefreshTokenExpireTime: " + getPreference(R.string.preference_refresh_token_expiration_key));

    }

    /**
     * EFFECT: Scrive / Sovrascrive tutte le shared preferences di gestione login e Api
     * @param result JSONObject REQUIRED Valore ritornato dalle API
     */
    public static void writePreferences(RefreshTokenResponse result){
        long currentTime = System.currentTimeMillis() / 1000;
        long expirationTimestamp = (result.getExpiresIn() == null ? 3600 :  result.getExpiresIn()) + currentTime;

        setPreference(R.string.preference_access_token_key, result.getAccessToken());
        setPreference(R.string.preference_id_token_key, result.getIdToken());
        setPreference(R.string.preference_token_duration_key, result.getExpiresIn().toString());
        setPreference(R.string.preference_id_token_expiration_key,  String.valueOf(expirationTimestamp));

        Log.d(TAG, "TOKEN REFRESHED" +
                "\nNEW DATA"+
                "\nAccessToken -> " + getPreference(R.string.preference_access_token_key) +
                "\nrefreshToken -> " + getPreference(R.string.preference_refresh_token_key) +
                "\ntokenId -> "+ getPreference(R.string.preference_id_token_key) +
                "\nidTokenExpireTime -> " + getPreference(R.string.preference_id_token_expiration_key) +
                "\nrefreshTokenExpireTime -> " + getPreference(R.string.preference_refresh_token_expiration_key));

    }

    /**
     * Crea un Intent per l'activity di Login
     */
    public static void startLoginActivity(){
        startNewActivity(LoginActivity.class, false, false, null);
    }

    /**
     * Crea un Intent per l'activity di Login che ritorna un valore
     */
    public static void startLoginActivityForResult(Activity activity){
        Intent i = new Intent(MainApplication.getAppContext(), LoginActivity.class);
        activity.startActivityForResult(i, Constants.LOGIN_REQUEST_CODE);
    }

    /**
     * Lancia una nuova Activity
     * @param openClass Acivity da istanziare
     */
    public static void startNewActivityAndKillOthers(Class<?> openClass){
        startNewActivity(openClass, true,false, null);
    }

    public static void startNewActivity(Class<?> openClass, boolean stopAll, boolean hasExtra, HashMap extra){
        Intent intent = new Intent(MainApplication.getAppContext(), openClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if(stopAll){
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        if(hasExtra){
            for (Object o : extra.entrySet()) {
                Map.Entry pair = (Map.Entry) o;
                if(pair.getValue() instanceof String){
                    intent.putExtra(pair.getKey().toString(),  pair.getValue().toString());
                }else if(pair.getValue() instanceof Boolean){
                    intent.putExtra(pair.getKey().toString(),  ((Boolean) pair.getValue()).booleanValue());
                }else{
                    // TODO: DEFAULT RAPPRESENTATION
                    intent.putExtra(pair.getKey().toString(),  pair.getValue().toString());
                }
            }
        }
        MainApplication.getAppContext().startActivity(intent);
    }


    /**
     * Lancia una nuova Activity
     * @param openClass Acivity da istanziare
     */
    public static void startNewActivity(Class<?> openClass){
        startNewActivity(openClass, false, false, null);
    }

    /**
     * Lancia una nuova Activity
     * @param openClass Acivity da istanziare
     * @param hasExtra Boolean
     * @param extra HashMap extras
     */
    public static void startNewActivity(Activity activity, Class<?> openClass, boolean hasExtra, HashMap extra){
        Intent intent = new Intent(activity.getApplicationContext(), openClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(hasExtra){
            for (Object o : extra.entrySet()) {
                Map.Entry pair = (Map.Entry) o;
                intent.putExtra(pair.getKey().toString(),  pair.getValue().toString());
            }
        }
        activity.startActivity(intent);
    }

    /**
     * Controlla se è necessario rieffettuare il login: se l'utente
     * ha già effettuato il login almeno una volta gli verrà richiesto il codice / password / segno (Android > Lollipop)
     * @param activity AppCompatActivity parent
     */
    public static void checkIfLoginIsNeeded(Activity activity){
        if(shouldRequestCode()){
            requestUnlockScreen(activity, Constants.LOCK_REQUEST_CODE);
        }else if(!qix.app.qix.helpers.Configuration.isLogged()){
            startLoginActivityForResult(activity);
        }
    }

    public static boolean shouldRequestCode(){
        return getBooleanPreference(R.string.preference_already_logged_key) && !qix.app.qix.helpers.Configuration.isLogged();
    }

    /**
     * Presenta l'activity di richiesta del codice/pattern/fingerprint dell'uetente.
     * @param activity AppCompatActivity Parent activity
     * @param request int LOCK_REQUEST_CODE
     */
    public static void requestUnlockScreen(Activity activity, int request){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            KeyguardManager keyguardManager = (KeyguardManager) MainApplication.getAppContext().getSystemService(Context.KEYGUARD_SERVICE);
            assert keyguardManager != null;

            if (keyguardManager.isKeyguardSecure()) {
                Intent i = keyguardManager.createConfirmDeviceCredentialIntent("Unlock", "Confirm your screen lock PIN, Pattern or Password ");
                if(i != null){
                    activity.startActivityForResult(i, request);
                }else{
                    Timber.d("Intent null");
                    //startLoginActivityForResult(activity);
                }
            }else{
                Timber.d("No, protection");
                //startLoginActivityForResult(activity);
                //presentToast("Secure lock screen isn't set up.\nGo to 'Settings -> Security -> Screen lock' to set up a lock screen", Toast.LENGTH_SHORT );
            }
        }else{
            Timber.d("Dispositivo superato.");
            //startLoginActivityForResult(activity);
        }
    }

    /**
     * Returns true if mock location enabled, false if not enabled.
     * @param context Context
     * @return boolean
     */
    public static boolean isMockSettingsON(Context context) {
        return !Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
    }

    /**
     * Check if user permit access to mock location app
     * @param context Context
     * @return boolean
     */
    public static boolean areThereMockPermissionApps(Context context) {
        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for(String r : requestedPermissions){
                        if(r.equals("android.permission.ACCESS_MOCK_LOCATION") && !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Got exception " , e.getMessage());
            }
        }

        return count > 0;
    }

    /**
     * Ritorna un url con un parametro signature per l'utilizzo delle api google maps
     * @param context Context
     * @param latitude Double
     * @param longitude Double
     * @return String
     * @throws NoSuchAlgorithmException Exception
     * @throws InvalidKeyException Exception
     * @throws MalformedURLException Exception
     */
    public static String getSignedMapUrl(Context context, Double latitude, Double longitude) throws NoSuchAlgorithmException, InvalidKeyException, MalformedURLException {
        @SuppressLint("DefaultLocale") String url = String.format(Constants.MAP_STATIC_IMAGE_URL_FORMAT_SIGNED, latitude, longitude, context.getString(R.string.google_maps_api_key));

        return Constants.MAP_STATIC_BASE_URL + signRequest(context, url);
    }

    /**
     * Firma un url per google
     * @param context Context
     * @param urlString String
     * @return String
     * @throws NoSuchAlgorithmException Exception
     * @throws InvalidKeyException Exception
     * @throws MalformedURLException Exception
     */
    private static String signRequest(Context context, String urlString) throws NoSuchAlgorithmException, InvalidKeyException, MalformedURLException {

        URL url = new URL(urlString);

        String secret = context.getString(R.string.google_maps_signature_secret);

        byte[] key = Base64.decode(secret, Base64.URL_SAFE);

        String resource = url.getPath() + "?" + url.getQuery();
        SecretKeySpec sha1Key = new SecretKeySpec(key, "HmacSHA1");

        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(sha1Key);

        byte[] sigBytes = mac.doFinal(resource.getBytes());
        String signature =  Base64.encodeToString(sigBytes, Base64.URL_SAFE);

        return resource + "&signature=" + signature;
    }

    /**
     * Elimina tutte le shared preferences associate al login
     */
    public static void deleteAllPreferences(){
        deletePreference(R.string.preference_is_logged_key);
        deletePreference(R.string.preference_refresh_token_key);
        deletePreference(R.string.preference_access_token_key);
        deletePreference(R.string.preference_id_token_key);
        deletePreference(R.string.preference_token_duration_key);
        deletePreference(R.string.preference_id_token_expiration_key);
        deletePreference(R.string.preference_refresh_token_expiration_key);
    }

    public static void destroyAllPreferences(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainApplication.getAppContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }

    /**
     Setta il fragment passato nel layout passato
     * @param fragment Fragment             REQUIRED Il Fragment da mostrare
     * @param layoutId int                  REQUIRED La stringa ID del layout
     */
    public static void switchFragment(AppCompatActivity activity, Fragment fragment, @IdRes int layoutId) {
        FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(layoutId, fragment);
        fragmentTransaction.commit();
    }


    /**
     * Elimina le shared preferences e presenta l'activity di login
     */
    public static void logout(){
        deletePreference(R.string.preference_already_logged_key);
        deleteAllPreferences();
        //*startLoginActivity();
    }

    /**
     * Imposta un nuova lingua
     * @param language String REQUIRED Codice della lingua da impostare
     */
    public static void setLocale(Context context, String language) {
        setPreference(R.string.preference_current_locale_key, language);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, language);
        }

        updateResourcesLegacy(context, language);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private static void updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);

        context.createConfigurationContext(configuration);
    }

    private static void updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    /**
     * Inserisce un testo e un'immagine nel layout di un button custom
     * @param view View
     * @param layoutId Integer
     * @param imageResource Integer
     * @param backgroundResource Integer
     */
    public static void setCustomButtonData(View view, Integer layoutId, Integer imageResource, Integer backgroundResource){
        ConstraintLayout layout = view.findViewById(layoutId);
        ImageView buttonImage = layout.findViewById(R.id.buttonBackgroundImage);
        View viewbackground = layout.findViewById(R.id.buttonBackView);
        viewbackground.setBackgroundResource(backgroundResource);
        buttonImage.setImageResource(imageResource);
    }

    /**
     * Crea un alert personalizzato con 2 bottoni
     * @param message Messaggio
     * @param positiveButton Titolo bottone conferma
     * @param negativeButton Titolo bottone negativo
     * @param isCancelable Boolean cancellabile
     * @param positive Listener bottone positivo
     * @param negative Listener bottone negativo
     */
    public static void presentSimpleDialog(Activity activity, String title, String message, String positiveButton, String negativeButton, boolean isCancelable, DialogInterface.OnClickListener positive, DialogInterface.OnClickListener negative){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setTitle(title)
                .setCancelable(isCancelable)
                .setPositiveButton(positiveButton, positive)
                .setNegativeButton(negativeButton, negative);
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Crea un alert personalizzato con 2 bottoni
     * @param message Messaggio
     * @param positiveButton Titolo bottone conferma
     * @param negativeButton Titolo bottone negativo
     * @param isCancelable Boolean Cancellabile
     * @param listener Listener bottone positivo e negativo
     */
    public static void presentSimpleDialog(Activity activity, String title,  String message, String positiveButton, String negativeButton, boolean isCancelable, DialogInterface.OnClickListener listener){
        presentSimpleDialog(activity, title, message, positiveButton, negativeButton, isCancelable, listener, listener);
    }

    /**
     * Ritorna un numero con solo 2 Decimanli
     * @param n Double
     * @return Double
     */
    public static Double getTwoDecimalNumber(Double n){
        return getNDecimalNumber(n, 2);
    }


    /**
     * Ritona una stringa rappresentante un double formattato alle migliaia, dato un double
     * @param n Double
     * @return String
     */
    public static String getTwoDecimalNumberFormattedString(Double n){
        DecimalFormat decim = new DecimalFormat("#,###.##");
        return decim.format(getTwoDecimalNumber(n));
    }

    /**
     * Ritona una stringa rappresentante un intero formattato alle migliaia, dato un double
     * @param n Double
     * @return String
     */
    public static String getIntNumberFormattedString(Double n){
        DecimalFormat decim = new DecimalFormat("#,###");
        return decim.format(getTwoDecimalNumber(n));
    }

    /**
     * Ritona una stringa rappresentante un intero formattato alle migliaia, dato un intero
     * @param n INteger
     * @return String
     */
    public static String getIntNumberFormattedString(Integer n){
        DecimalFormat decim = new DecimalFormat("#,###");
        return decim.format(n);
    }

    /**
     * Ritorna un numero con solo 3 Decimanli
     * @param n Double
     * @return Double
     */
    public static Double getThreeDecimalNumber(Double n){
        return getNDecimalNumber(n, 3);
    }

    /**
     * Ritrna un numero con solo "decimals" numeri decimali
     * @param n Double
     * @param decimals int
     * @return Double
     */
    @NonNull
    private static Double getNDecimalNumber(Double n, int decimals){
        return Math.round(n * Math.pow(10, decimals)) / Math.pow(10, decimals);
    }

    /**
     * Rtiorna una stringa in formato ISO
     * @param date Date
     * @return String
     */
    public static String getIsoStringFrom(Date date){
        return date != null ? getStringFrom(date, "yyyy-MM-dd'T'HH:mm'Z'") : null;
    }

    /**
     * Ritorna una string nel formato richiesto
     * @param date Date
     * @param format String
     * @return String
     */
    @SuppressLint("SimpleDateFormat")
    public static String getStringFrom(Date date, String format){
        DateFormat df = new SimpleDateFormat(format); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(TimeZone.getDefault());
        return df.format(date);
    }

    /**
     * Ritorna una data String in formato ISO
     * @param date String
     * @return Date
     * @throws ParseException Exception
     */
    public static Date getDateFromIso(String date) throws ParseException{
        return  getDateFrom(date, "yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
    }

    public static Integer map(Float x, Float in_min, Float in_max, Integer out_min, Integer out_max){
        return ((Float)((x - in_min) * (out_max.floatValue() - out_min.floatValue()) / (in_max - in_min) + out_min.floatValue())).intValue();
    }

    /**
     * Ritorna una oggetto Date data una string e un formato
     * @param date String
     * @param format String
     * @return Date
     * @throws ParseException Exception
     */
    @SuppressLint("SimpleDateFormat")
    public static Date getDateFrom(String date, String format) throws ParseException {
        DateFormat formatter = new SimpleDateFormat(format);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return  formatter.parse(date);
    }

    /**
     * Aggiorna i valori delle textView per getBalance
     * @param activity AppCompatActivity
     * @param balance TextView
     */
    public static void updateBalance(Activity activity, final TextView balance){
        updateBalance(activity, balance, null);
    }

    /**
     * Aggiorna i valori delle textView per getBalance
     * @param activity AppCompatActivity
     * @param balance TextView
     * @param format String
     */
    public static void updateBalance(final Activity activity, final TextView balance, final String format){
        final String noValue = activity.getString(R.string.default_no_value_unit);

        AsyncRequest.getBalace(activity, new Callback<BalanceResponse>() {
            @Override
            public void onResponse(@NonNull Call<BalanceResponse> call, @NonNull Response<BalanceResponse> response) {

                if(response.isSuccessful()){

                    BalanceResponse result = response.body();

                    assert result != null;
                    String qix = getIntNumberFormattedString(result.getBalance());
                    //String cur = useCurrency ? result.getQixCurrency() : defaultCurrency;

                    setPreference(R.string.preference_user_qix_currency, result.getQixCurrency());

                    if(format != null){
                        balance.setText(String.format(format, qix));
                    }else{
                        balance.setText(getIntNumberFormattedString(result.getBalance()));
                    }
                }else{
                    balance.setText(activity.getString(R.string.default_no_value_unit));
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<BalanceResponse> call, @NonNull Throwable t) {
                balance.setText(noValue);
            }
        });
    }

    /**
     * Aggiunge l'unità di misura alla distanza
     * @param n Integer
     * @return String
     */
    public static String getFormattedDistance(Integer n){
        return n < 1000 ? n + " m" : (n / 1000) + " Km";
    }
}
