package com.im.daeseong.processview;

import androidx.appcompat.app.AppCompatActivity;//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<String> runService = CProcessInfo.getRunningServicesName(MainActivity.this);
        for (String packageName : runService) {
            Log.e(TAG, "runService:" + packageName);
        }

        List<String> runTop = CProcessInfo.getRunningTopPackageName(MainActivity.this);
        for (String packageName : runTop) {
            Log.e(TAG, "runTop:" + packageName);
        }

        List<String> currentTask = CProcessInfo.getCurrentTaskPackageName(MainActivity.this);
        for (String packageName : currentTask) {
            Log.e(TAG, "CurrentTask:" + packageName);
        }

        List<String> allPackage = CProcessInfo.getAllPackageNames(MainActivity.this);
        for (String packageName : allPackage) {
            Log.e(TAG, "allPackage:" + packageName);
        }
    }
}
