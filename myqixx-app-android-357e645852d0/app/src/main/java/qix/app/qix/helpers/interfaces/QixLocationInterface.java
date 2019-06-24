package qix.app.qix.helpers.interfaces;

import android.location.Location;

public interface QixLocationInterface {
    void onLocationUpdate(Location userLocation, boolean isFirstTime);
}
