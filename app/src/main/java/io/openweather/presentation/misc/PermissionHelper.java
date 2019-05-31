package io.openweather.presentation.misc;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class PermissionHelper {

    private static final int REQUEST_PERMISSIONS_CODE = 34;

    public void requestPermissions(Activity activity, String... permissions) {
        ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSIONS_CODE);
    }

    /**
     * Checks all given permissions have been granted.
     *
     * @param grantResults results
     * @return returns true if all permissions have been granted.
     */
    public boolean verifyPermissions(int requestCode, int... grantResults) {
        if (requestCode != REQUEST_PERMISSIONS_CODE) return false;
        if (grantResults.length == 0) {
            return false;
        }
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true if the Activity or Fragment has access to all given permissions.
     *
     * @param context     context
     * @param permissions permission list
     * @return returns true if the Activity or Fragment has access to all given permissions.
     */
    public boolean hasSelfPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (!permissionExists(context, permission)) {
                return false;
            }
        }
        return true;
    }


    private boolean permissionExists(Context context, String permission) {
        int permissionState = ActivityCompat.checkSelfPermission(
                context,
                permission
        );
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

}
