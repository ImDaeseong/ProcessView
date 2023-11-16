package com.daeseong.processview

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*

object CProcessInfo {

    // 실행 중인 서비스 목록
    fun getRunningServicesName(context: Context): List<String>? {
        try {
            val serviceInfo = mutableListOf<String>()
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

            for (srvInfo in activityManager.getRunningServices(Short.MAX_VALUE.toInt())) {
                if (srvInfo.service.packageName == "com.im.daeseong.processview") {
                    continue
                }
                serviceInfo.add(srvInfo.service.packageName)
            }
            return serviceInfo

        } catch (e: Exception) {
        }

        return null
    }

    // 최상위 액티비티
    fun getRunningTopPackageName(context: Context): List<String>? {
        try {
            val topPackageInfo = mutableListOf<String>()
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

            for (activityInfo in activityManager.getRunningTasks(Short.MAX_VALUE.toInt())) {
                if (activityInfo.topActivity!!.packageName == "com.im.daeseong.processview") {
                    continue
                }
                topPackageInfo.add(activityInfo.topActivity!!.packageName)
            }
            return topPackageInfo

        } catch (e: Exception) {
        }

        return null
    }

    // 최근 실행한 Task 목록
    fun getCurrentTaskPackageName(context: Context): List<String>? {
        try {
            val currentPackageInfo = mutableListOf<String>()
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

            for (appInfo in activityManager.getRecentTasks(Short.MAX_VALUE.toInt(),ActivityManager.RECENT_IGNORE_UNAVAILABLE or ActivityManager.RECENT_WITH_EXCLUDED)) {
                if (appInfo.baseIntent.component!!.packageName == "com.im.daeseong.processview") {
                    continue
                }
                currentPackageInfo.add(appInfo.baseIntent.component!!.packageName)
            }
            return currentPackageInfo

        } catch (e: Exception) {
        }

        return null
    }

    // 전체 앱 목록
    fun getAllPackageNames(context: Context): List<String>? {
        try {
            val allPackageInfo = mutableListOf<String>()
            val packageManager = context.packageManager

            for (applicationInfo in packageManager.getInstalledApplications(PackageManager.GET_META_DATA)) {
                if (applicationInfo.packageName == "com.im.daeseong.processview") {
                    continue
                }
                allPackageInfo.add(applicationInfo.packageName)
            }
            return allPackageInfo

        } catch (e: Exception) {
        }
        return null
    }

    fun copyApk(context: Context, sPackageName: String): Boolean {
        try {
            val packageManager = context.packageManager
            val pkgInfo = packageManager.getPackageInfo(sPackageName, 0)

            val sPublicSourceDir = pkgInfo.applicationInfo.publicSourceDir
            val sFileName = String.format("%s.apk", sPackageName)

            val sPath = String.format("%s/apkList", Environment.getExternalStorageDirectory().absolutePath)
            if (!directoryExists(sPath)) createDirectory(sPath)

            val fullFilename = String.format("%s/%s", sPath, sFileName)
            copyFile(sPublicSourceDir, fullFilename)

            return true

        } catch (e: PackageManager.NameNotFoundException) {
        }
        return false
    }

    fun killApp(context: Context, sPackageName: String): Boolean {
        try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.killBackgroundProcesses(sPackageName)

            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(sPackageName, PackageManager.GET_META_DATA)
            android.os.Process.sendSignal(applicationInfo.uid, android.os.Process.SIGNAL_KILL)

            return true
        } catch (e: Exception) {
        }
        return false
    }

    fun install(context: Context, sAppPath: String): Boolean {
        try {
            val file = File(sAppPath)
            if (file.exists()) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
            return true

        } catch (e: Exception) {
        }
        return false
    }

    fun uninstall(context: Context, sPackageName: String): Boolean {
        try {
            val intent = Intent(Intent.ACTION_DELETE)
            val packageURI = Uri.parse("package:$sPackageName")
            intent.data = packageURI
            context.startActivity(intent)
            return true

        } catch (e: Exception) {
        }
        return false
    }

    fun startApp(context: Context, sPackageName: String): Boolean {
        try {
            val packageManager = context.packageManager
            val intent = packageManager.getLaunchIntentForPackage(sPackageName)
            if (intent != null)
                context.startActivity(intent)
            return true
        } catch (e: Exception) {
        }
        return false
    }

    fun getPackageLabel(context: Context, sPackageName: String): String {
        try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(sPackageName, PackageManager.GET_META_DATA)
            return applicationInfo.loadLabel(packageManager).toString()

        } catch (e: Exception) {
        }
        return ""
    }

    fun getPackageIcon(context: Context, sPackageName: String): Drawable? {
        try {
            val packageManager = context.packageManager
            val applicationInfo = packageManager.getApplicationInfo(sPackageName, PackageManager.GET_META_DATA)
            return applicationInfo.loadIcon(packageManager)

        } catch (e: Exception) {
        }
        return null
    }

    fun getPackageInfo(context: Context, sPackageName: String): PackageItem? {
        try {
            val packageManager = context.packageManager
            val pkgInfo = packageManager.getPackageInfo(sPackageName, 0) ?: return null

            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val first = Date(pkgInfo.firstInstallTime)
            val last = Date(pkgInfo.lastUpdateTime)
            val sInstallTime = format.format(first)
            val sUpdateTime = format.format(last)

            val sPermission = pkgInfo.applicationInfo.permission
            val sPublicSourceDir = pkgInfo.applicationInfo.publicSourceDir
            val sFilesize = getFileSize(sPublicSourceDir)

            return PackageItem(
                pkgInfo.packageName,
                sInstallTime,
                sUpdateTime,
                pkgInfo.versionName,
                sFilesize,
                sPermission,
                sPublicSourceDir)

        } catch (e: Exception) {
        }
        return null
    }

    fun getPackageCache(sPackageName: String, bClear: Boolean): String {
        val sAbsolutePath = Environment.getExternalStorageDirectory().absolutePath
        val sPath = String.format("%s/Android/data/%s/cache", sAbsolutePath, sPackageName)

        // cache file clear
        if (bClear) clearCacheFiles(sPath)

        return getCacheFileSize(sPath)
    }

    private fun clearCacheFiles(sPath: String) {
        try {
            val directory = File(sPath)
            if (directory.isDirectory) {
                val files = directory.listFiles()
                for (f in files) {
                    f.delete()
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun getCacheFileSize(sPath: String): String {
        try {
            var sFilesize = ""
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

        } catch (e: Exception) {
        }
        return "0kb"
    }

    private fun getFileSize(sPath: String): String {
        try {
            val file = File(sPath)
            val len = file.length()
            return byte2FitMemorySize(len)

        } catch (e: Exception) {
        }
        return ""
    }

    @SuppressLint("DefaultLocale")
    private fun byte2FitMemorySize(byteNum: Long): String {
        return when {
            byteNum < 0 -> "shouldn't be less than zero!"
            byteNum < 1024 -> String.format("%.3fB", byteNum.toDouble())
            byteNum < 1048576 -> String.format("%.3fKB", byteNum.toDouble() / 1024)
            byteNum < 1073741824 -> String.format("%.3fMB", byteNum.toDouble() / 1048576)
            else -> String.format("%.3fGB", byteNum.toDouble() / 1073741824)
        }
    }

    private fun createDirectory(directoryPath: String) {
        val file = File(directoryPath)
        if (!file.exists()) {
            file.mkdirs()
        }
    }

    private fun directoryExists(directoryPath: String): Boolean {
        val file = File(directoryPath)
        return file.isDirectory
    }

    private fun copyFile(sourcePath: String, destinationPath: String) {
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

    private fun deleteFile(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
    }

    private fun fileExists(filePath: String): Boolean {
        val file = File(filePath)
        return file.exists()
    }
}
