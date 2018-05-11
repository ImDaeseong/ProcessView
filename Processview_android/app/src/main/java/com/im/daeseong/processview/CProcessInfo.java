package com.im.daeseong.processview;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CProcessInfo {


    //실행중인 서비스 목록
    public static List<String> GetRunningServicesName(Context context)
    {
        try
        {
            List<String> ServiceInfo = new ArrayList<>();
            ActivityManager activityManager  = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);

            for (ActivityManager.RunningServiceInfo  srvInfo :  activityManager.getRunningServices(Short.MAX_VALUE))
            {
                if (srvInfo.service.getPackageName().equals("com.im.daeseong.processview") ){
                    continue;
                }

                ServiceInfo.add(srvInfo.service.getPackageName());
            }
            return ServiceInfo;

        } catch(Exception e) {
        }

        return null;
    }

    //최상위 액티비티
    public static List<String> GetRunningTopPackageName(Context context)
    {
        try
        {
            List<String> TopPackageInfo = new ArrayList<>();
            ActivityManager activityManager  = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);

            for (ActivityManager.RunningTaskInfo ActivityInfo : activityManager.getRunningTasks(Short.MAX_VALUE))
            {
                if (ActivityInfo.topActivity.getPackageName().equals("com.im.daeseong.processview") ){
                    continue;
                }

                TopPackageInfo.add(ActivityInfo.topActivity.getPackageName());
            }
            return TopPackageInfo;
        }
        catch(Exception e) {
        }

        return null;
    }

    //최근 실행한 Task 목록
    public static List<String> GetCurrentTaskPackageName(Context context)
    {
        try
        {
            List<String> CurrentPackageInfo = new ArrayList<>();
            ActivityManager activityManager  = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);

            for (ActivityManager.RecentTaskInfo AppInfo : activityManager.getRecentTasks(Short.MAX_VALUE, ActivityManager.RECENT_IGNORE_UNAVAILABLE | ActivityManager.RECENT_WITH_EXCLUDED) )
            {
                if (AppInfo.baseIntent.getComponent().getPackageName().equals("com.im.daeseong.processview") ){
                    continue;
                }

                CurrentPackageInfo.add(AppInfo.baseIntent.getComponent().getPackageName());
            }
            return CurrentPackageInfo;

        } catch (Exception e){
        }

        return null;
    }

    //전체앱 목록
    public static List<String> GetAllPackageNames(Context context)
    {
        try
        {
            List<String> AllPackageInfo = new ArrayList<>();
            PackageManager packageManager = context.getPackageManager();

            for(ApplicationInfo applicationInfo : packageManager.getInstalledApplications(PackageManager.GET_META_DATA)){

                if(applicationInfo.packageName.equals("com.im.daeseong.processview") ){
                    continue;
                }

                AllPackageInfo.add(applicationInfo.packageName);
            }
            return AllPackageInfo;

        }
        catch(Exception e) {
        }
        return null;
    }

    public static  boolean CopyApk(Context context, String sPackageName)
    {
        try
        {
            PackageManager packageManager = context.getPackageManager();

            PackageInfo pkgInfo = packageManager.getPackageInfo(sPackageName, 0);

            String sPublicSourceDir = pkgInfo.applicationInfo.publicSourceDir;
            String sFileName = String.format("%s.apk", sPackageName);// GetFileName(sPublicSourceDir);

            String sPath = String.format("%s/apkList", Environment.getExternalStorageDirectory().getAbsolutePath() );
            if (!DirectoryExists(sPath)) CreateDirectory(sPath);

            String fullFilename = sPath = String.format("%s/%s", sPath, sFileName);
            CopyFile(sPublicSourceDir, fullFilename);

            return true;

        } catch (PackageManager.NameNotFoundException e){
        }
        return false;
    }

    public static boolean KillApp(Context context, String sPackageName)
    {
        try
        {
            ActivityManager activityManager  = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            activityManager.killBackgroundProcesses(sPackageName);

            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(sPackageName, PackageManager.GET_META_DATA);
            android.os.Process.sendSignal(applicationInfo.uid, android.os.Process.SIGNAL_KILL);

            return true;
        } catch(Exception e) {
        }
        return false;
    }

    public static boolean Install(Context context, String sAppPath)
    {
        //var appPath = Path.Combine(Android.OS.Environment.ExternalStorageDirectory + "/download/", sApkFilename);
        try
        {
            File file = new File(sAppPath);
            if(file.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
            return true;

        } catch (Exception e){
        }
        return false;
    }

    public static boolean UnInstall(Context context, String sPackageName)
    {
        try
        {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            Uri packageURI = Uri.parse("package:" + sPackageName);
            intent.setData(packageURI);
            context.startActivity(intent);
            return true;

        } catch (Exception e){
        }
        return false;
    }

    public static boolean StartApp(Context context, String sPackageName)
    {
        try
        {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(sPackageName);
            if (intent != null)
                context.startActivity(intent);
            return true;

        }catch (Exception e){
        }
        return false;
    }

    public static String GetPackageLabel(Context context, String sPackageName)
    {
        try
        {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(sPackageName, PackageManager.GET_META_DATA);

            String sLabel = applicationInfo.loadLabel(packageManager).toString();
            return sLabel;

        }catch (Exception e){
        }
        return "";
    }

    public static Drawable GetPackageIcon(Context context, String sPackageName)
    {
        try
        {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(sPackageName, PackageManager.GET_META_DATA);

            Drawable icon = applicationInfo.loadIcon(packageManager);
            return icon;

        }catch (Exception e){
        }
        return null;
    }

    public static PackageItem GetPackageInfo(Context context, String sPackageName)
    {
        try
        {
            PackageManager packageManager = context.getPackageManager();

            PackageInfo pkgInfo = packageManager.getPackageInfo(sPackageName, 0);
            if (pkgInfo == null) return null;

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date First = new Date(pkgInfo.firstInstallTime);
            Date Last = new Date(pkgInfo.lastUpdateTime);
            String sInstallTime = format.format(First);
            String sUpdateTime = format.format(Last);

            String sPermission = pkgInfo.applicationInfo.permission;
            String sPublicSourceDir = pkgInfo.applicationInfo.publicSourceDir;
            String sFilesize = GetFileSize(sPublicSourceDir);

            PackageItem item = new PackageItem(pkgInfo.packageName, sInstallTime, sUpdateTime, pkgInfo.versionName, sFilesize, sPermission, sPublicSourceDir);
            return item;

        }catch (Exception e){
        }
        return null;
    }

    public static String GetPackageCache(String sPackageName, boolean bClear)// = false)
    {
        String sAbsolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String sPath = String.format("%s/Android/data/%s/cache", sAbsolutePath, sPackageName);

        //cache file clear
        if (bClear) ClearCacheFiles(sPath);

        return GetCacheFileSize(sPath);
    }

    private static  void ClearCacheFiles(String sPath)
    {
        try
        {
            File directory = new File(sPath);
            if(directory.isDirectory()){

                File[] files = directory.listFiles();
                for (File f : files){
                    f.delete();
                }
            }

        }catch (Exception e){
        }
    }

    private static String GetCacheFileSize(String sPath)
    {
        try
        {
            String sFilesize = "";
            long lFileSize = 0;
            File directory = new File(sPath);
            if(directory.isDirectory()){

                File[] files = directory.listFiles();
                for (File f : files){
                    lFileSize += f.length();
                }
            }
            sFilesize = byte2FitMemorySize(lFileSize);
            return sFilesize;

        }catch (Exception e){
        }
        return "0kb";
    }

    private static String GetFileSize(String sPath)
    {
        try
        {
            File file = new File(sPath);
            long len = file.length();
            String sFilesize = byte2FitMemorySize(len);
            return sFilesize;

        }catch (Exception e){
        }
        return "";
    }

    @SuppressLint("DefaultLocale")
    private static String byte2FitMemorySize(final long byteNum) {
        if (byteNum < 0) {
            return "shouldn't be less than zero!";
        } else if (byteNum < 1024) {
            return String.format("%.3fB", (double) byteNum);
        } else if (byteNum < 1048576) {
            return String.format("%.3fKB", (double) byteNum / 1024);
        } else if (byteNum < 1073741824) {
            return String.format("%.3fMB", (double) byteNum / 1048576);
        } else {
            return String.format("%.3fGB", (double) byteNum / 1073741824);
        }
    }

    private static void CreateDirectory(String directoryPath)
    {
        File file = new File(directoryPath);
        if(!file.exists()){
            file.mkdirs();
        }
    }

    private static boolean DirectoryExists(String directoryPath)
    {
        File file = new File(directoryPath);
        return  file.isDirectory();
    }

    private static void CopyFile(String sourcePath, String destinationPath)
    {
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            File sFile = new File(sourcePath);
            File dFile = new File(destinationPath);

            inChannel = new FileInputStream(sFile).getChannel();
            outChannel = new FileOutputStream(dFile).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);

        } catch (IOException e) {
        } finally {
            try {
                if (inChannel != null) {
                    inChannel.close();
                }
                if (outChannel != null) {
                    outChannel.close();
                }
            }catch (IOException e){
            }
        }
    }

    private static void DeleteFile(String filePath)
    {
        File file = new File(filePath);
        if(file.exists()){
            file.delete();
        }
    }

    private static  boolean FileExists(String filePath)
    {
        File file = new File(filePath);
        return  file.exists();
    }
}
