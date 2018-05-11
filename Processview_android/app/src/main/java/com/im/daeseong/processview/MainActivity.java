package com.im.daeseong.processview;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<String> runService = CProcessInfo.GetRunningServicesName(MainActivity.this);
        for(int i=0; i < runService.size(); i++){
            //Log.e("tag", "runService:"+ runService.get(i) );
        }

        List<String> runTop = CProcessInfo.GetRunningTopPackageName(MainActivity.this);
        for(int i=0; i < runTop.size(); i++){
            //Log.e("tag", "runTop:"+ runTop.get(i) );
        }

        List<String> CurrentTask = CProcessInfo.GetCurrentTaskPackageName(MainActivity.this);
        for(int i=0; i < CurrentTask.size(); i++){
            //Log.e("tag", "CurrentTask:"+ CurrentTask.get(i) );
        }

        List<String> allPackage = CProcessInfo.GetAllPackageNames(MainActivity.this);
        for(int i=0; i < allPackage.size(); i++){
            //Log.e("tag", "allPackage:"+ allPackage.get(i) );
        }

    }
}
