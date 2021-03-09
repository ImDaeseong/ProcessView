package com.daeseong.processview

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.daeseong.processview.CProcessInfo.GetAllPackageNames
import com.daeseong.processview.CProcessInfo.GetCurrentTaskPackageName
import com.daeseong.processview.CProcessInfo.GetPackageCache
import com.daeseong.processview.CProcessInfo.GetPackageIcon
import com.daeseong.processview.CProcessInfo.GetPackageInfo
import com.daeseong.processview.CProcessInfo.GetPackageLabel
import com.daeseong.processview.CProcessInfo.GetRunningServicesName
import com.daeseong.processview.CProcessInfo.GetRunningTopPackageName
import com.daeseong.processview.CProcessInfo.KillApp
import com.daeseong.processview.CProcessInfo.StartApp

class MainActivity : AppCompatActivity() {

    private val tag: String = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val runService = GetRunningServicesName(this)
        for (i in runService!!.indices) {
            Log.e(tag, "runService:"+ runService[i])
        }

        val runTop = GetRunningTopPackageName(this)
        for (i in runTop!!.indices) {
            Log.e(tag, "runTop:"+ runTop[i])
        }

        val CurrentTask = GetCurrentTaskPackageName(this)
        for (i in CurrentTask!!.indices) {
            Log.e(tag, "CurrentTask:"+ CurrentTask[i])
        }

        val allPackage = GetAllPackageNames(this)
        for (i in allPackage!!.indices) {
            Log.e(tag, "allPackage:"+ allPackage[i])
            Log.e(tag, "GetPackageLabel:"+ GetPackageLabel(this, allPackage[i]))
            Log.e(tag, "GetPackageIcon:"+ GetPackageIcon(this, allPackage[i]))
            Log.e(tag, "GetPackageInfo:"+ GetPackageInfo(this, allPackage[i]))
            GetPackageCache(allPackage[i], false)
        }

        val bkill = KillApp(this, "com.google.android.youtube")
        if(bkill){
            Log.e(tag, "KillApp:$bkill")
            StartApp(this, "com.google.android.youtube")
        }
    }
}
