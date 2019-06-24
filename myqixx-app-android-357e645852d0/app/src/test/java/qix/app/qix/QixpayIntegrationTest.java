package qix.app.qix;


import android.content.Intent;
import android.support.test.rule.ActivityTestRule;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import java.util.Random;

import qix.app.qix.models.PartnerResponse;

public class QixpayIntegrationTest {

    private PartnerResponse mPartnerResponse;

    private Double getRandomDouble(){
        Random r = new Random();
        return r.nextDouble();
    }

    @Rule
    public ActivityTestRule<QixpayActivity> activityRule = new ActivityTestRule<QixpayActivity>(
            QixpayActivity.class,
            true,
            false);

    @Before
    public void createPartnerData(){

        Gson gson = new GsonBuilder().create();
        String jsonString = "{" +
                "\"id\": \"fd6f2efd-27a3-4827-9c77-9dd98d4888cb\", " +
                "\"name\": \"Belorussky station\", " +
                "\"address\": \"Tverskaya Zastava Square, 7, Mosca, Russia\", " +
                "\"latitude\": 55.7765945," +
                "\"longitude\": 37.58148410000001," +
                "\"category_id\": \"8da53339-b272-11e8-8a27-83d161290622\", " +
                "\"rate_accrual\": 30, " +
                "\"rate_redemption\": 20, " +
                "\"currency\": \"RUB\", " +
                "\"qix_currency\": \"QIXRUB\"," +
                "\"distance\": 132070," +
                "\"image_url\": \"https://s3.eu-west-1.amazonaws.com/urbana-qix-cms-pictures%2Fthumbnails%2Fpartner_pos/fd6f2efd-27a3-4827-9c77-9dd98d4888cb?AWSAccessKeyId=ASIAS2ANYRXMPPN7WYJF&Expires=1540369059&Signature=ezKLDM0zMqzmurPG67n7e1IkjsE%3D&x-amz-security-token=FQoGZXIvYXdzEPP%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaDC3T0ymGhZx3HdAl9yLsAeIUBq2vsXcRYGUyr18GHNW4bal%2FsyLZ%2BkWjB2SpyqCSB0rcJF4fQU1b1FD2NwlG%2BYqCMN43BSoY17dMIanku2GYk1guO2l1Tc9OKpCmpF%2BLP6YOoyjtiBn8BZQvF92QWkHPiAym3M2qGr6iWMfIWNrLrleFvgv4%2Fs49yEA1KnyUX4SUUS8LjfQ3oPzeQIxThm%2F5GXluk0LvqvBO1zfngKeN3WQGSktmSHq1mH5a8IQh8U4O793KLfYul1lf0K2Gu07L5Nm4QqPl%2F%2BjX4csHTSv1MiTVtkKVbhpGTqMq%2FB8mFcHzaCh9mHEbI7mDKNGUv94F\" " +
            "}";
            mPartnerResponse = gson.fromJson(jsonString, PartnerResponse.class);
    }

    @Test
    public void qixpay_startActivity(){
        Intent intent = new Intent();
        intent.putExtra("partner", mPartnerResponse);

        activityRule.launchActivity(intent);

    }

    /*@Test
    public void qixpay_insertDataAndChangePage(){
        onView(withId(R.id.qixpayAmountTextfield)).
    }*/

}
