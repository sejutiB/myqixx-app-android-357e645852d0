package qix.app.qix.helpers;

import java.util.LinkedHashSet;
import java.util.Set;

import qix.app.qix.BuildConfig;

public class Constants {

    public enum PageIndexes{
        HOME_PAGE_INDEX(0),
        QIXWORLD_PAGE_INDEX(1),
        QIXCHANGE_PAGE_INDEX(2),
        MYQIX_PAGE_INDEX(3),
        QIXPAY_PAGE_INDEX(4);

        private final int value;
        PageIndexes(int value) { this.value = value; }
        public int getValue() { return value; }
    }

    public enum TransactionType{
        PAY_OR_BUY,
        PAY_IN_STORE,
        PAY,
        BUY,
        SHAKE,
        TRAVEL,
        MARKETPLACE,
        WAIT_FOR_APPROVAL
    }

    public enum PaymentType{
        IN_APP_PAYMENT,
        IN_STORE_PAYMENT
    }

    public static class TransactionData{
        public String title;
        public String value;

        public TransactionData(String t, String v){
            title = t;
            value = v;
        }
    }

    public static class ShopInfo {
        public String shopDomain;
        public String apiKey;
        public String currency;

        public ShopInfo(String d, String a, String c){
           this.shopDomain = d;
           this.apiKey = a;
           this.currency = c;
        }
    }


    /* The payment brands for Ready-to-Use UI and Payment Button */
    public static final Set<String> PAYMENT_BRANDS;

    //public static final String SHOP_DOMAIN = "qix-shop.myshopify.com";
    //public static final String SHOPIFY_API_KEY = "c2400a459a0342739b5909224fc50bc5";



    static {
        PAYMENT_BRANDS = new LinkedHashSet<>();

        PAYMENT_BRANDS.add("VISA");
        PAYMENT_BRANDS.add("MASTER");
        PAYMENT_BRANDS.add("PAYPAL");
    }

    public static final Integer MAX_RADIUS = BuildConfig.MAX_RADIUS;

    static final String MAP_STATIC_BASE_URL = "https://maps.googleapis.com";

    static final String MAP_STATIC_IMAGE_URL_FORMAT_SIGNED = MAP_STATIC_BASE_URL + "/maps/api/staticmap?markers=%.5f,%.5f&zoom=13&size=300x300&sensor=false&key=%s";

    public static final String QIX_BASE_URL = BuildConfig.BASE_URL;

    public static final String KALIGO_SIGN_IN_URL = BuildConfig.KALIGO_BASE_URL;
    static final String KALIGO_SECRET_KEY = BuildConfig.KALIGO_SECRET;

    public static final String PAYDOO_BASE_URL = BuildConfig.PAYDOO_ENDPOINT;
    public static final String PAYDOO_USER_ID = BuildConfig.PAYDOO_USER_ID;
    public static final String PAYDOO_PASSWORD = BuildConfig.PAYDOO_PASSWORD;
    public static final String PAYDOO_ENTITY_ID = BuildConfig.PAYDOO_ENTITY_ID;


    public static final String ZENDESK_ENDPOINT = BuildConfig.ZENDESK_ENDPOINT;
    public static final String ZENDESK_APP_ID = BuildConfig.ZENDESK_APP_ID;;
    public static final String ZENDESK_CLIENT_ID = BuildConfig.ZENDESK_CLIENT_ID;;


    static final int QIXSIGNUP_MIN_AGE_REQUIRED = 18;

    /* Qixpay Flow pages indexes*/
    public static final int QIXPAY_DETAILS_PAGE_INDEX = 0;
    public static final int QIXPAY_SELECT_PAGE_INDEX = 1;
    public static final int QIXPAY_CHART_PAGE_INDEX = 2;
    public static final int QIXPAY_REVIEW_PAGE_INDEX = 3;
    public static final int QIXPAY_RESULT_PAGE_INDEX = 4;


    /* Qix signup Flow pages indexes*/
    public static final int QIXSIGNUP_FIRST_PAGE_INDEX = 0;
    public static final int QIXSIGNUP_SECOND_PAGE_INDEX = 1;
    public static final int QIXSIGNUP_THIRD_PAGE_INDEX = 2;
    public static final int QIXSIGNUP_FOURTH_PAGE_INDEX = 3;

    /* Qix recovery password Flow pages indexes*/
    public static final int QIXRECOVERY_FIRST_PAGE_INDEX = 0;
    public static final int QIXRECOVERY_SECOND_PAGE_INDEX = 1;
    public static final int QIXRECOVERY_THIRD_PAGE_INDEX = 2;

    /* Qix intro pages indexes*/
    public static final int QIXINTRO_FIRST_PAGE_INDEX = 0;
    public static final int QIXINTRO_SECOND_PAGE_INDEX = 1;
    public static final int QIXINTRO_THIRD_PAGE_INDEX = 2;
    public static final int QIXINTRO_FOURTH_PAGE_INDEX = 3;

    /* Qix shaketutorial pages indexes*/
    public static final int QIXSHAKE_FIRST_PAGE_INDEX = 0;
    public static final int QIXSHAKE_SECOND_PAGE_INDEX = 1;
    public static final int QIXSHAKE_THIRD_PAGE_INDEX = 2;
    public static final int QIXSHAKE_FOURTH_PAGE_INDEX = 3;

    /* QIX buy flow indexes */
    public static final int QIXBUY_FIRST_PAGE_INDEX = 0;
    public static final int QIXBUY_RESULT_PAGE_INDEX = 1;

    /* Activity for result Codes */
    public static final int LOCK_REQUEST_CODE = 221;
    public static final int PAY_REQUEST_CODE = 222;
    public static final int SHAKE_ACTIVITY_CODE = 223;
    public static final int LOGIN_REQUEST_CODE = 224;
    public static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 225;
    public static final int INTRO_ACTIVITY_CODE = 226;
    public static final int RC_BARCODE_CAPTURE = 9001;

    public static ShopInfo getShopInfo(){

        if(Configuration.getUserCountry() != null){
            switch(Configuration.getUserCountry()){
                case "MX":
                    return new ShopInfo("qix-shop-mexico.myshopify.com", "85ed1d8a7658537e09cf4f7a8a31e2bf", "MXN");
                case "ZA":
                    return new ShopInfo("qix-shop-safrica.myshopify.com", "d77d9c34bb37411e8f6da1ca0db44e07", "ZAR");
                case "NL":
                    return new ShopInfo("qix-shop-netherlands.myshopify.com", "a0990b8351b2e64600312e6bfe1466d9", "EUR");
                default:
                    return new ShopInfo("qix-shop.myshopify.com", "c2400a459a0342739b5909224fc50bc5", "EUR");
            }
        }else{
            return new ShopInfo("qix-shop.myshopify.com", "c2400a459a0342739b5909224fc50bc5", "EUR");
        }


    }


}
