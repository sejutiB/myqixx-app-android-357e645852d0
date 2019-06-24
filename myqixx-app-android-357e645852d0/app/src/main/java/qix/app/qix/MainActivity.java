package qix.app.qix;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SearchView;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import qix.app.qix.fragments.HomeFragment;
import qix.app.qix.fragments.QixTravelFragment;
import qix.app.qix.fragments.myqix.MyQixListFragment;
import qix.app.qix.fragments.qixmarketplace.QixMarketplaceFragment;
import qix.app.qix.fragments.qixpay.QixpayListFragment;
import qix.app.qix.helpers.AsyncRequest;
import qix.app.qix.helpers.Constants;
import qix.app.qix.helpers.Configuration;
import qix.app.qix.helpers.Helpers;
import qix.app.qix.helpers.adapters.QixFragmentPagerAdapter;

import qix.app.qix.helpers.custom_views.QixViewPager;
import qix.app.qix.models.ProfileResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends ShakeDetectorActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = "MainActivity";

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private static Location currentLocation;
    private LocationManager locationManager;
    private QixViewPager pager;
    private static int currentItem = 0;
    private QixFragmentPagerAdapter adapter;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentlocationnetwork, currentlocationgps, currentlocationpassive;


    private BottomNavigationView navigation;

    private static final String STATE_RESOURCE_PATH = "STATE_RESOURCE_PATH";

    protected String resourcePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setTitle("QIX");

        //*fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        logUser();

        String lang = Helpers.getPreference(R.string.preference_current_locale_key);

        if (lang != null) {
            Helpers.setLocale(getApplicationContext(), lang);
        } else {
            Helpers.setLocale(getApplicationContext(), "en");
        }

        if (savedInstanceState != null) {
            resourcePath = savedInstanceState.getString(STATE_RESOURCE_PATH);
        }

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Token firebase: " + token);

        /* Creo le istanze dei fragment */
        ArrayList<Fragment> pages = new ArrayList<>();
        pages.add(new HomeFragment());
        pages.add(new QixTravelFragment());
        pages.add(new QixMarketplaceFragment());
        pages.add(new MyQixListFragment());
        pages.add(new QixpayListFragment());

        navigation = findViewById(R.id.bottomNavigationView);

        navigation.setSelectedItemId(R.id.navigation_qix_home);

        navigation.setOnNavigationItemSelectedListener(item -> {
            setTitle(item.getTitle());
            Integer id = null;
            switch (item.getItemId()) {
                case R.id.navigation_qix_home:
                    id = 0;
                    break;
                case R.id.navigation_qix_travel:
                    id = 1;
                    break;
                case R.id.navigation_qix_marketplace:
                    id = 2;
                    break;
                case R.id.navigation_my_qix:
                    id = 3;
                    break;
            }

            pager.setCurrentItem(id);
            return true;
        });

        adapter = new QixFragmentPagerAdapter(getSupportFragmentManager(), pages);

        pager = findViewById(R.id.view_pager);
        pager.disableSwipe(true);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("page", "onPageSelected: " + position);

                currentItem = position;

                //navigation.changeCurrentItem(position == 2 ? -1 : position > 2 ? position - 1 : position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        pager.setOffscreenPageLimit(4);
        pager.setAdapter(adapter);

        Log.d(TAG, "\nAccessToken: " + Helpers.getPreference(R.string.preference_access_token_key) +
                "\nrefreshToken: " + Configuration.getRefreshToken() +
                "\ntokenId: " + Configuration.getIdToken() +
                "\nidTokenExpireTime: " + Helpers.getPreference(R.string.preference_id_token_expiration_key) +
                "\nrefreshTokenExpireTime: " + Helpers.getPreference(R.string.preference_refresh_token_expiration_key));


        new Handler().postDelayed(this::getUserLocation, 100);


        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        assert mSensorManager != null;
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // Listener per lo shake
        setOnShakeListener(count -> {
            if (currentLocation != null) {
                Intent intent = new Intent(getApplicationContext(), AudioScanningActivity.class);
                intent.putExtra("USER_LOCATION", currentLocation.getLatitude() + "," + currentLocation.getLongitude());
                startActivityForResult(intent, Constants.SHAKE_ACTIVITY_CODE);
            } else {
                Helpers.presentToast(getResources().getString(R.string.localizationMessage), Toast.LENGTH_SHORT);
            }
        });

        if(!Helpers.getBooleanPreference(R.string.preference_is_first_run_key)){
            Intent activityIntent = new Intent(this, ShakeTutorial.class);
            startActivity(activityIntent);
            Helpers.setPreference(R.string.preference_is_first_run_key, true);
        }

    }

    private void logUser() {
        AsyncRequest.getProfileData(this, new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    ProfileResponse result = response.body();
                    assert result != null;
                    Crashlytics.setUserIdentifier(result.getSub());
                    Crashlytics.setUserEmail(Configuration.getUsername());
                    Crashlytics.setUserName(Configuration.getUsername());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {

            }
        });
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(STATE_RESOURCE_PATH, resourcePath);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Constants.PAY_REQUEST_CODE:
                break;
            case Constants.SHAKE_ACTIVITY_CODE:
                break;

        }

    }


    public void getUserLocation() {
        Log.i(TAG, "Getting user location");
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // User not accept location sharing.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }

        if (Helpers.isMockSettingsON(getApplicationContext()) && Helpers.areThereMockPermissionApps(getApplicationContext())) {
            Log.d(TAG, "Probably is mock location!");
        }

        Log.d(TAG, "GET LOCATION");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        assert locationManager != null;

        Criteria criteria = new Criteria();
        criteria.setAccuracy(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ? Criteria.ACCURACY_COARSE : Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        String locationProvider = locationManager.getBestProvider(criteria, true);
        Log.d(TAG, "LocationProvider: " + locationProvider);
        /*fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            currentLocation = location;
                            adapter.onLocationUpdate(location, false);
                            Log.i("in fused", "Location: " + location.toString());

                        }
                    }
                });*/
        // currentLocation = locationManager.getLastKnownLocation(locationProvider);
        try {
            currentlocationnetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
             currentlocationpassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
             currentlocationgps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (currentlocationgps != null) currentLocation = currentlocationgps;
        else if (currentlocationnetwork != null) currentLocation = currentlocationnetwork;
        else if (currentlocationpassive != null) currentLocation = currentlocationpassive;


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "Location: " + location.toString());

                if (location.isFromMockProvider()) {
                    Log.d(TAG, "FAKE GPS");
                    locationManager.removeUpdates(this);
                }

                adapter.onLocationUpdate(location, currentLocation == null);
                currentLocation = location;
                Log.d("LocationUpdate", currentLocation.getLatitude() + "");
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        });


        if (currentLocation != null) {
            adapter.onLocationUpdate(currentLocation, true);
          //  Toast.makeText(this, currentLocation.getLatitude() + " g " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
        } else {

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "LocationN: " + location.toString());

                    if (location.isFromMockProvider()) {
                        locationManager.removeUpdates(this);
                    }

                    adapter.onLocationUpdate(location, currentLocation == null);
                    currentLocation = location;
                    Log.d("LocationUpdateN", currentLocation.getLatitude() + "");
                  //  Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + " N " + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {
                }

                @Override
                public void onProviderEnabled(String s) {
                }

                @Override
                public void onProviderDisabled(String s) {
                }
            });
            adapter.onLocationUpdate(currentLocation, true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserLocation();
                } else {
                    Helpers.presentToast("Need location permission", Toast.LENGTH_SHORT);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(this);
        super.onPause();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }

    public static int getCurrentItemIndex() {
        return currentItem;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    public static Location getCurrentLocation() {
        return currentLocation;
    }

    @Override
    public void onBackPressed() {
        if (pager.getCurrentItem() != Constants.PageIndexes.HOME_PAGE_INDEX.getValue()) {
            pager.setCurrentItem(Constants.PageIndexes.HOME_PAGE_INDEX.getValue());
            navigation.setSelectedItemId(R.id.navigation_qix_home);
        } else {
            super.onBackPressed();
        }
    }
}
