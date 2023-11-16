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
    public static List<String> getRunningServicesName(Context context) {
        try {
            List<String> serviceInfo = new ArrayList<>();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

            for (ActivityManager.RunningServiceInfo srvInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (!srvInfo.service.getPackageName().equals("com.im.daeseong.processview")) {
                    serviceInfo.add(srvInfo.service.getPackageName());
                }
            }
            return serviceInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //최상위 액티비티
    public static List<String> getRunningTopPackageName(Context context) {
        try {
            List<String> topPackageInfo = new ArrayList<>();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

            for (ActivityManager.RunningTaskInfo activityInfo : activityManager.getRunningTasks(Integer.MAX_VALUE)) {
                if (!activityInfo.topActivity.getPackageName().equals("com.im.daeseong.processview")) {
                    topPackageInfo.add(activityInfo.topActivity.getPackageName());
                }
            }
            return topPackageInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //최근 실행한 Task 목록
    public static List<String> getCurrentTaskPackageName(Context context) {
        try {
            List<String> currentPackageInfo = new ArrayList<>();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

            for (ActivityManager.RecentTaskInfo appInfo : activityManager.getRecentTasks(Integer.MAX_VALUE, ActivityManager.RECENT_IGNORE_UNAVAILABLE | ActivityManager.RECENT_WITH_EXCLUDED)) {
                if (!appInfo.baseIntent.getComponent().getPackageName().equals("com.im.daeseong.processview")) {
                    currentPackageInfo.add(appInfo.baseIntent.getComponent().getPackageName());
                }
            }
            return currentPackageInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //전체앱 목록
    public static List<String> getAllPackageNames(Context context) {
        try {
            List<String> allPackageInfo = new ArrayList<>();
            PackageManager packageManager = context.getPackageManager();

            for (ApplicationInfo applicationInfo : packageManager.getInstalledApplications(PackageManager.GET_META_DATA)) {
                if (!applicationInfo.packageName.equals("com.im.daeseong.processview")) {
                    allPackageInfo.add(applicationInfo.packageName);
                }
            }
            return allPackageInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean copyApk(Context context, String sPackageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo pkgInfo = packageManager.getPackageInfo(sPackageName, 0);

            String sPublicSourceDir = pkgInfo.applicationInfo.publicSourceDir;
            String sFileName = String.format("%s.apk", sPackageName);

            String sPath = String.format("%s/apkList", Environment.getExternalStorageDirectory().getAbsolutePath());
            if (!directoryExists(sPath)) {
                createDirectory(sPath);
            }

            String fullFilename = String.format("%s/%s", sPath, sFileName);
            copyFile(sPublicSourceDir, fullFilename);

            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean killApp(Context context, String sPackageName) {
        try {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.killBackgroundProcesses(sPackageName);

            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(sPackageName, PackageManager.GET_META_DATA);
            android.os.Process.sendSignal(applicationInfo.uid, android.os.Process.SIGNAL_KILL);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean install(Context context, String sAppPath) {
        try {
            File file = new File(sAppPath);
            if (file.exists()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean uninstall(Context context, String sPackageName) {
        try {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            Uri packageURI = Uri.parse("package:" + sPackageName);
            intent.setData(packageURI);
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean startApp(Context context, String sPackageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(sPackageName);
            if (intent != null) {
                context.startActivity(intent);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getPackageLabel(Context context, String sPackageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(sPackageName, PackageManager.GET_META_DATA);
            return applicationInfo.loadLabel(packageManager).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Drawable getPackageIcon(Context context, String sPackageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(sPackageName, PackageManager.GET_META_DATA);
            return applicationInfo.loadIcon(packageManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PackageItem getPackageInfo(Context context, String sPackageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo pkgInfo = packageManager.getPackageInfo(sPackageName, 0);
            if (pkgInfo == null) return null;

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date first = new Date(pkgInfo.firstInstallTime);
            Date last = new Date(pkgInfo.lastUpdateTime);
            String sInstallTime = format.format(first);
            String sUpdateTime = format.format(last);

            String sPermission = pkgInfo.applicationInfo.permission;
            String sPublicSourceDir = pkgInfo.applicationInfo.publicSourceDir;
            String sFilesize = getFileSize(sPublicSourceDir);

            return new PackageItem(sPackageName, sInstallTime, sUpdateTime, pkgInfo.versionName, sFilesize, sPermission, sPublicSourceDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPackageCache(String sPackageName, boolean bClear) {
        String sAbsolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String sPath = String.format("%s/Android/data/%s/cache", sAbsolutePath, sPackageName);

        // Clear cache files
        if (bClear) {
            clearCacheFiles(sPath);
        }

        return getCacheFileSize(sPath);
    }

    private static void clearCacheFiles(String sPath) {
        try {
            File directory = new File(sPath);
            if (directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File f : files) {
                        f.delete();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getCacheFileSize(String sPath) {
        try {
            long lFileSize = 0;
            File directory = new File(sPath);
            if (directory.isDirectory()) {
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File f : files) {
                        lFileSize += f.length();
                    }
                }
            }
            return byte2FitMemorySize(lFileSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0kb";
    }

    private static String getFileSize(String sPath) {
        try {
            File file = new File(sPath);
            long len = file.length();
            return byte2FitMemorySize(len);
        } catch (Exception e) {
            e.printStackTrace();
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

    private static void createDirectory(String directoryPath) {
        File file = new File(directoryPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private static boolean directoryExists(String directoryPath) {
        File file = new File(directoryPath);
        return file.isDirectory();
    }

    private static void copyFile(String sourcePath, String destinationPath) {
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            File sFile = new File(sourcePath);
            File dFile = new File(destinationPath);

            inChannel = new FileInputStream(sFile).getChannel();
            outChannel = new FileOutputStream(dFile).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inChannel != null) {
                    inChannel.close();
                }
                if (outChannel != null) {
                    outChannel.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    private static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }
}
