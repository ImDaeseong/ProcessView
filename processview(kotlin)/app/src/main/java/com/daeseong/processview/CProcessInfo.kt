package com.daeseong.processview

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.os.Process
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


object CProcessInfo {

    //실행중인 서비스 목록
    fun GetRunningServicesName(context: Context): List<String>? {
        try {
            val ServiceInfo: MutableList<String> = ArrayList()
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (srvInfo in activityManager.getRunningServices(Short.MAX_VALUE.toInt())) {
                if (srvInfo.service.packageName == "com.daeseong.processview") {
                    continue
                }
                ServiceInfo.add(srvInfo.service.packageName)
            }
            return ServiceInfo
        } catch (e: Exception) {
        }
        return null
    }

    //최상위 액티비티
    fun GetRunningTopPackageName(context: Context): List<String>? {
        try {
            val TopPackageInfo: MutableList<String> = ArrayList()
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (ActivityInfo in activityManager.getRunningTasks(Short.MAX_VALUE.toInt())) {
                if (ActivityInfo.topActivity!!.packageName == "com.daeseong.processview") {
                    continue
                }
                TopPackageInfo.add(ActivityInfo.topActivity!!.packageName)
            }
            return TopPackageInfo
        } catch (e: java.lang.Exception) {
        }
        return null
    }

    //최근 실행한 Task 목록
    fun GetCurrentTaskPackageName(context: Context): List<String>? {
        try {
            val CurrentPackageInfo: MutableList<String> = ArrayList()
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (AppInfo in activityManager.getRecentTasks(Short.MAX_VALUE.toInt(),ActivityManager.RECENT_IGNORE_UNAVAILABLE or ActivityManager.RECENT_WITH_EXCLUDED )) {
                if (AppInfo.baseIntent.component!!.packageName == "com.daeseong.processview") {
                    continue
                }
                CurrentPackageInfo.add(AppInfo.baseIntent.component!!.packageName)
            }
            return CurrentPackageInfo
        } catch (e: java.lang.Exception) {
        }
        return null
    }

    //전체앱 목록
    fun GetAllPackageNames(context: Context): List<String>? {
        try {
            val AllPackageInfo: MutableList<String> = ArrayList()
            val packageManager = context.packageManager
            for (applicationInfo in packageManager.getInstalledApplications(PackageManager.GET_META_DATA)) {
                if (applicationInfo.packageName == "com.daeseong.processview") {
                    continue
                }
                AllPackageInfo.add(applicationInfo.packageName)
            }
            return AllPackageInfo
        } catch (e: java.lang.Exception) {
        }
        return null
    }

    fun CopyApk(context: Context, sPackageName: String?): Boolean {
        try {
            val packageManager = context.packageManager
            val pkgInfo = packageManager.getPackageInfo(sPackageName, 0)
            val sPublicSourceDir = pkgInfo.applicationInfo.publicSourceDir
            val sFileName = String.format("%s.apk", sPackageName)
            var sPath = String.format("%s/apkList", Environment.getExternalStorageDirectory().absolutePath)
            if (!DirectoryExists(sPath)) CreateDirectory(sPath)
            sPath = String.format("%s/%s", sPath, sFileName)
            val fullFilename = sPath
            CopyFile(sPublicSourceDir, fullFilename)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return false
    }

    fun KillApp(context: Context, sPackageName: String?): Boolean {
        try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.killBackgroundProcesses(sPackageName)
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(sPackageName, PackageManager.GET_META_DATA)
            Process.sendSignal(applicationInfo.uid, Process.SIGNAL_KILL)
            return true
        } catch (e: java.lang.Exception) {
        }
        return false
    }

    fun Install(context: Context, sAppPath: String?): Boolean {
        try {
            val file = File(sAppPath)
            if (file.exists()) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
            return true
        } catch (e: java.lang.Exception) {
        }
        return false
    }

    fun UnInstall(context: Context, sPackageName: String): Boolean {
        try {
            val intent = Intent(Intent.ACTION_DELETE)
            val packageURI = Uri.parse("package:$sPackageName")
            intent.data = packageURI
            context.startActivity(intent)
            return true
        } catch (e: java.lang.Exception) {
        }
        return false
    }

    fun StartApp(context: Context, sPackageName: String?): Boolean {
        try {
            val packageManager = context.packageManager
            val intent = packageManager.getLaunchIntentForPackage(sPackageName!!)
            if (intent != null) context.startActivity(intent)
            return true
        } catch (e: java.lang.Exception) {
        }
        return false
    }

    fun GetPackageLabel(context: Context, sPackageName: String?): String? {
        try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(sPackageName, PackageManager.GET_META_DATA)
            return applicationInfo.loadLabel(packageManager).toString()
        } catch (e: java.lang.Exception) {
        }
        return ""
    }

    fun GetPackageIcon(context: Context, sPackageName: String?): Drawable? {
        try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(sPackageName, PackageManager.GET_META_DATA)
            return applicationInfo.loadIcon(packageManager)
        } catch (e: java.lang.Exception) {
        }
        return null
    }

    fun GetPackageInfo(context: Context, sPackageName: String?): PackageItem? {
        try {
            val packageManager = context.packageManager
            val pkgInfo = packageManager.getPackageInfo(sPackageName, 0) ?: return null
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val First = Date(pkgInfo.firstInstallTime)
            val Last = Date(pkgInfo.lastUpdateTime)
            val sInstallTime = format.format(First)
            val sUpdateTime = format.format(Last)
            val sPermission = pkgInfo.applicationInfo.permission
            val sPublicSourceDir = pkgInfo.applicationInfo.publicSourceDir
            val sFilesize = GetFileSize(sPublicSourceDir)
            return PackageItem(pkgInfo.packageName, sInstallTime, sUpdateTime, pkgInfo.versionName, sFilesize!!, sPermission, sPublicSourceDir)
        } catch (e: java.lang.Exception) {
        }
        return null
    }

    fun GetPackageCache(sPackageName: String?, bClear: Boolean): String?
    {
        val sAbsolutePath = Environment.getExternalStorageDirectory().absolutePath
        val sPath = String.format("%s/Android/data/%s/cache", sAbsolutePath, sPackageName)

        //cache file clear
        if (bClear) ClearCacheFiles(sPath)
        return GetCacheFileSize(sPath)
    }

    private fun ClearCacheFiles(sPath: String) {
        try {
            val directory = File(sPath)
            if (directory.isDirectory) {
                val files = directory.listFiles()
                for (f in files) {
                    f.delete()
                }
            }
        } catch (e: java.lang.Exception) {
        }
    }

    private fun GetCacheFileSize(sPath: String): String? {
        try {
            var sFilesize: String? = ""
            var lFileSize: Long = 0
            val directory = File(sPath)
            if (directory.isDirectory) {
                val files = directory.listFiles()
                for (f in files) {
                    lFileSize += f.length()
                }
            }
            sFilesize = byte2FitMemorySize(lFileSize)
            return sFilesize
        } catch (e: java.lang.Exception) {
        }
        return "0kb"
    }

    private fun GetFileSize(sPath: String): String? {
        try {
            val file = File(sPath)
            val len = file.length()
            return byte2FitMemorySize(len)
        } catch (e: java.lang.Exception) {
        }
        return ""
    }

    @SuppressLint("DefaultLocale")
    private fun byte2FitMemorySize(byteNum: Long): String? {
        return when {
            byteNum < 0 -> {
                "shouldn't be less than zero!"
            }
            byteNum < 1024 -> {
                String.format("%.3fB", byteNum.toDouble())
            }
            byteNum < 1048576 -> {
                String.format("%.3fKB", byteNum.toDouble() / 1024)
            }
            byteNum < 1073741824 -> {
                String.format("%.3fMB", byteNum.toDouble() / 1048576)
            }
            else -> {
                String.format("%.3fGB", byteNum.toDouble() / 1073741824)
            }
        }
    }

    private fun CreateDirectory(directoryPath: String) {
        val file = File(directoryPath)
        if (!file.exists()) {
            file.mkdirs()
        }
    }

    private fun DirectoryExists(directoryPath: String): Boolean {
        val file = File(directoryPath)
        return file.isDirectory
    }

    private fun CopyFile(sourcePath: String, destinationPath: String) {
        var inChannel: FileChannel? = null
        var outChannel: FileChannel? = null

        try {
            val sFile = File(sourcePath)
            val dFile = File(destinationPath)
            inChannel = FileInputStream(sFile).channel
            outChannel = FileOutputStream(dFile).channel
            inChannel.transferTo(0, inChannel.size(), outChannel)
        } catch (e: IOException) {
        } finally {
            try {
                inChannel?.close()
                outChannel?.close()
            } catch (e: IOException) {
            }
        }
    }

    private fun DeleteFile(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
    }

    private fun FileExists(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists()
    }
}