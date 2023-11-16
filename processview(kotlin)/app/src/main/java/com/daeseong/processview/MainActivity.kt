package com.daeseong.processview

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val runService = CProcessInfo.getRunningServicesName(this)
        for (i in runService!!.indices) {
            Log.e("tag", "runService:" + runService[i])
        }

        val runTop = CProcessInfo.getRunningTopPackageName(this)
        for (i in runTop!!.indices) {
            Log.e("tag", "runTop:" + runTop[i])
        }

        val currentTask = CProcessInfo.getCurrentTaskPackageName(this)
        for (i in currentTask!!.indices) {
            Log.e("tag", "CurrentTask:" + currentTask[i])
        }

        val allPackage = CProcessInfo.getAllPackageNames(this)
        for (i in allPackage!!.indices) {
            Log.e("tag", "allPackage:" + allPackage[i])
        }
    }
}
