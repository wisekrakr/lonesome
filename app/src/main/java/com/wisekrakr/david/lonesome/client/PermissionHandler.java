package com.wisekrakr.david.lonesome.client;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

public class PermissionHandler {
    public static final String TAG = "PermissionHandler";

    /**
     * Checks if  permission is enabled or not
     * @param context current  fragment activity
     * @param manifestPermission (String) what kind of permission
     * @param packageManager (int) PackageManager constant
     * @return
     */
    public static boolean checkForPermission(FragmentActivity context, String manifestPermission, int packageManager){
        boolean result = ContextCompat.checkSelfPermission(context, manifestPermission) == (packageManager);

        return result;
    }

    /**
     * Request runtime permission
     * @param activity current activity or fragment
     * @param permissions Array
     * @param requestCode (int) static constant
     */
    public static void requestPermission(FragmentActivity activity, String[] permissions, int requestCode){
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }
}
