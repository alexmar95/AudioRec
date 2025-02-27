package com.example.audiorec;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

abstract public class AbstractPermissionActivity
        extends Activity {
    abstract protected String[] getDesiredPermissions();
    abstract protected void onPermissionDenied();
    abstract protected void onReady(Bundle state);

    private static final int REQUEST_PERMISSION=61125;
    private static final String STATE_IN_PERMISSION="inPermission";
    private boolean isInPermission=false;
    private Bundle state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.state=savedInstanceState;

        if (state!=null) {
            isInPermission=state.getBoolean(STATE_IN_PERMISSION, false);
        }

        if (hasAllPermissions(getDesiredPermissions())) {
            onReady(state);
        }
        else if (!isInPermission) {
            isInPermission=true;

            ActivityCompat
                    .requestPermissions(this,
                            netPermissions(getDesiredPermissions()),
                            REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        isInPermission=false;

        if (requestCode==REQUEST_PERMISSION) {
            if (hasAllPermissions(getDesiredPermissions())) {
                onReady(state);
            }
            else {
                onPermissionDenied();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(STATE_IN_PERMISSION, isInPermission);
    }

    private boolean hasAllPermissions(String[] perms) {
        for (String perm : perms) {
            if (!hasPermission(perm)) {
                return(false);
            }
        }

        return(true);
    }

    protected boolean hasPermission(String perm) {
        return(ContextCompat.checkSelfPermission(this, perm)==
                PackageManager.PERMISSION_GRANTED);
    }

    private String[] netPermissions(String[] wanted) {
        ArrayList<String> result=new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return(result.toArray(new String[result.size()]));
    }
}

