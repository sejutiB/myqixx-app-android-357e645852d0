package qix.app.qix.helpers;

import qix.app.qix.R;

public class Configuration {

    /**
     * Preference Shourtcuts
     */

    public static String getIdToken(){
        return Helpers.getPreference(R.string.preference_id_token_key);
    }

    public static boolean isLogged(){
        return Helpers.getBooleanPreference(R.string.preference_is_logged_key);
    }

    public static String getAccessToken(){
        return Helpers.getPreference(R.string.preference_access_token_key);
    }

    public static boolean isAlreadyLogged(){
        return Helpers.getBooleanPreference(R.string.preference_already_logged_key);
    }

    public static String getRefreshToken(){
        return Helpers.getPreference(R.string.preference_refresh_token_key);
    }

    public static void setUserId(String userId){
        Helpers.setPreference(R.string.preference_user_id, userId);
    }

    public static String getUserId(){
        return Helpers.getPreference(R.string.preference_user_id);
    }

    public static void setUserCountry(String countryCode){
        Helpers.setPreference(R.string.preference_user_country, countryCode);
    }

    public static String getUserCountry(){
        return Helpers.getPreference(R.string.preference_user_country);
    }

    public static String getUsername(){
        return Helpers.getPreference(R.string.preference_username_key);
    }

    public static String getPassword(){
        return Helpers.getPreference(R.string.preference_password_key);
    }


}
