using System;
using System.Collections.Generic;
using System.Linq;
using System.IO;
using Android.OS;
using Android.Graphics;
using Android.Graphics.Drawables;
using Android.Content.PM;
using Android.App;
using Android.Content;
using Xamarin.Forms;
using ProcessView.Droid.Interface;
using ProcessView.Interface;


[assembly: Dependency(typeof(CProcessInfo))]
namespace ProcessView.Droid.Interface
{
    public class CProcessInfo : IProcessInfo
    {
        //실행중인 서비스 목록
        public List<string> GetRunningServicesName()
        {
            try
            {
                List<string> ServiceInfo = new List<string>();

                var activityManager = Android.App.Application.Context.GetSystemService(Android.App.Activity.ActivityService) as Android.App.ActivityManager;

                foreach (ActivityManager.RunningServiceInfo srvInfo in activityManager.GetRunningServices(int.MaxValue))
                {
                    if (srvInfo.Service.PackageName == "ds.id.ProcessView") continue;

                    ServiceInfo.Add(srvInfo.Service.PackageName);
                }
                return ServiceInfo;

            }
            catch { }

            return null;
        }

        //최상위 액티비티
        public List<string> GetRunningTopPackageName()
        {
            try
            {
                List<string> TopPackageInfo = new List<string>();

                var activityManager = Android.App.Application.Context.GetSystemService(Android.App.Activity.ActivityService) as Android.App.ActivityManager;
                foreach (ActivityManager.RunningTaskInfo ActivityInfo in activityManager.GetRunningTasks(int.MaxValue))
                {
                    if (ActivityInfo.TopActivity.PackageName == "ds.id.ProcessView") continue;

                    TopPackageInfo.Add(ActivityInfo.TopActivity.PackageName);
                }
                return TopPackageInfo;

            }
            catch { }

            return null;
        }

        //최근 실행한 Task 목록
        public List<string> GetCurrentTaskPackageName()
        {
            try
            {
                List<string> CurrentPackageInfo = new List<string>();

                var activityManager = Android.App.Application.Context.GetSystemService(Android.App.Activity.ActivityService) as Android.App.ActivityManager;

                foreach (ActivityManager.RecentTaskInfo AppInfo in activityManager.GetRecentTasks(int.MaxValue, RecentTaskFlags.IgnoreUnavailable | RecentTaskFlags.WithExcluded))
                {
                    if (AppInfo.BaseIntent.Component.PackageName == "ds.id.ProcessView") continue;

                    CurrentPackageInfo.Add(AppInfo.BaseIntent.Component.PackageName);
                }
                return CurrentPackageInfo;
            }
            catch { }

            return null;
        }

        //전체앱 목록
        public List<string> GetAllPackageNames()
        {
            try
            {

                //실행되는 앱들만 GetLaunchIntentForPackage 값이 null이 아니다.
                return (from package in Android.App.Application.Context.PackageManager.GetInstalledApplications(PackageInfoFlags.MetaData)
                        where Android.App.Application.Context.PackageManager.GetLaunchIntentForPackage(package.PackageName) != null &&
                        package.PackageName != "ds.id.ProcessView"
                        select package.PackageName).ToList();

                //GetInstalledApplications 전체를 읽기 때문에 느림 / PackageManager.queryIntentActivities() 실행 가능한 정보만 읽음
                //return (from package in Android.App.Application.Context.PackageManager.GetInstalledApplications(PackageInfoFlags.MatchUninstalledPackages | PackageInfoFlags.DisabledComponents)
                //        where package.PackageName != "ds.id.ProcessView" //&& package.Flags != ApplicationInfoFlags.System
                //        select package.PackageName).ToList();
            }
            catch { }
            return null;
        }

        public bool CopyApk(string sPackageName)
        {
            try
            {
                PackageInfo pkgInfo = Android.App.Application.Context.PackageManager.GetPackageInfo(sPackageName, 0);
                string sPublicSourceDir = pkgInfo.ApplicationInfo.PublicSourceDir;
                string sFileName = string.Format("{0}.apk", sPackageName);// GetFileName(sPublicSourceDir);

                string sPath = string.Format("{0}/apkList", Android.OS.Environment.ExternalStorageDirectory.AbsolutePath);
                if (!DirectoryExists(sPath)) CreateDirectory(sPath);

                var fullFilename = System.IO.Path.Combine(sPath, sFileName);
                CopyFile(sPublicSourceDir, fullFilename, true);

                return true;
            }
            catch { }
            return false;
        }

        public bool KillApp(string sPackageName)
        {
            try
            {
                //ApplicationInfo info = Android.App.Application.Context.PackageManager.GetApplicationInfo(sPackageName, PackageInfoFlags.MetaData);
                //if (info.Flags == ApplicationInfoFlags.System)
                //    return false;

                //PackageInfo pkgInfo = Android.App.Application.Context.PackageManager.GetPackageInfo(sPackageName, 0);
                //if (pkgInfo.ApplicationInfo.Flags == ApplicationInfoFlags.System)
                //    return false;

                var actManager = (ActivityManager)Forms.Context.GetSystemService(Context.ActivityService);
                actManager.KillBackgroundProcesses(sPackageName);

                ApplicationInfo info = Android.App.Application.Context.PackageManager.GetApplicationInfo(sPackageName, PackageInfoFlags.MetaData);
                Android.OS.Process.SendSignal(info.Uid, Signal.Kill);

                return true;
            }
            catch { }
            return false;
        }

        public bool Install(string sAppPath)
        {
            //var appPath = Path.Combine(Android.OS.Environment.ExternalStorageDirectory + "/download/", sApkFilename);

            try
            {
                var setupIntent = new Intent(Intent.ActionView);
                setupIntent.SetDataAndType(Android.Net.Uri.FromFile(new Java.IO.File(sAppPath)), "application/vnd.android.package-archive");
                Android.App.Application.Context.StartActivity(setupIntent);
                return true;
            }
            catch { }

            return false;
        }

        public bool UnInstall(string sPackageName)
        {
            try
            {
                var UnIntent = new Intent(Intent.ActionUninstallPackage);
                UnIntent.SetData(Android.Net.Uri.Parse("package:" + sPackageName));
                Android.App.Application.Context.StartActivity(UnIntent);

                return true;

            }
            catch { }

            return false;
        }

        private string GetFileName(string strFilename)
        {
            int nPos = strFilename.LastIndexOf('/');
            int nLength = strFilename.Length;
            if (nPos < nLength)
                return strFilename.Substring(nPos + 1, (nLength - nPos) - 1);
            return strFilename;
        }

        public bool StartApp(string sPackageName)
        {
            try
            {
                var intent = Android.App.Application.Context.PackageManager.GetLaunchIntentForPackage(sPackageName);
                Android.App.Application.Context.StartActivity(intent);
                return true;
            }
            catch { }
            return false;
        }

        public string GetPackageLabel(string sPackageName)
        {
            try
            {
                ApplicationInfo info = Android.App.Application.Context.PackageManager.GetApplicationInfo(sPackageName, PackageInfoFlags.MetaData);
                string sLabel = info.LoadLabel(Android.App.Application.Context.PackageManager);
                return sLabel;
            }
            catch { }
            return "";
        }

        public ImageSource GetPackageIcon(string sPackageName)
        {
            try
            {
                ApplicationInfo info = Android.App.Application.Context.PackageManager.GetApplicationInfo(sPackageName, PackageInfoFlags.MetaData);
                var PackageIcon = info.LoadIcon(Android.App.Application.Context.PackageManager);
                return GetImageSource(PackageIcon);
            }
            catch { }
            return null;
        }

        public PackageItem GetPackageInfo(string sPackageName)
        {
            try
            {
                PackageInfo pkgInfo = Android.App.Application.Context.PackageManager.GetPackageInfo(sPackageName, 0);
                if (pkgInfo == null) return null;

                var First = (new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc)).AddMilliseconds(pkgInfo.FirstInstallTime);
                var Last = (new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc)).AddMilliseconds(pkgInfo.LastUpdateTime);
                string sInstallTime = First.ToLocalTime().ToString("yyyy-MM-dd HH:mm:ss");
                string sUpdateTime = Last.ToLocalTime().ToString("yyyy-MM-dd HH:mm:ss");

                string sPermission = pkgInfo.ApplicationInfo.Permission;
                string sPublicSourceDir = pkgInfo.ApplicationInfo.PublicSourceDir;
                string sFilesize = GetFileSize(sPublicSourceDir);

                PackageItem item = new PackageItem(pkgInfo.PackageName, sInstallTime, sUpdateTime, pkgInfo.VersionName, sFilesize, sPermission, sPublicSourceDir);
                return item;
            }
            catch { }
            return null;
        }

        public string GetPackageCache(string sPackageName, bool bClear = false)
        {
            string sAbsolutePath = Android.OS.Environment.ExternalStorageDirectory.AbsolutePath;
            string sPath = System.IO.Path.Combine(sAbsolutePath, "Android/data/" + sPackageName + "/cache");

            //cache file clear
            if (bClear) ClearCacheFiles(sPath);

            return GetCacheFileSize(sPath);
        }

        private void ClearCacheFiles(string sPath)
        {
            try
            {
                string[] strfileList = Directory.GetFiles(sPath);
                foreach (string strFileName in strfileList)
                {
                    File.Delete(strFileName);
                }
            }
            catch { }
        }

        private string GetCacheFileSize(string sPath)
        {
            try
            {
                string sFilesize = "";
                long lFileSize = 0;
                string[] strfileList = Directory.GetFiles(sPath);
                foreach (string strFileName in strfileList)
                {
                    var fs = new FileInfo(strFileName);
                    lFileSize += fs.Length;
                }

                if (lFileSize < 1024)
                    sFilesize = lFileSize + "Kb";
                else
                {
                    var FilesizeMB = Convert.ToDouble(lFileSize / 1024);
                    sFilesize = FilesizeMB.ToString("##.##") + "Mb";
                }

                return sFilesize;
            }
            catch { }

            return "0kb";
        }


        private ImageSource GetImageSource(Drawable drawable)
        {
            try
            {
                Bitmap bitmap = ((BitmapDrawable)drawable).Bitmap;
                byte[] bitmapData = GetByteArrayFromBitmap(bitmap);
                ImageSource photo = ImageSource.FromStream(() => new MemoryStream(bitmapData));
                return photo;
            }
            catch { }
            return null;
        }

        private byte[] GetByteArrayFromBitmap(Bitmap bitmap)
        {
            byte[] bitmapData;
            using (var st = new MemoryStream())
            {
                bitmap.Compress(Bitmap.CompressFormat.Png, 0, st);
                bitmapData = st.ToArray();
            }
            return bitmapData;
        }

        private string GetFileSize(string sPath)
        {
            try
            {
                string sFilesize = "";
                var fs = new FileInfo(sPath);
                var lFileSize = fs.Length / 1024;
                if (lFileSize < 1024)
                    sFilesize = lFileSize + "Kb";
                else
                {
                    var FilesizeMB = Convert.ToDouble(lFileSize / 1024);
                    sFilesize = FilesizeMB.ToString("##.##") + "Mb";
                }
                return sFilesize;
            }
            catch { }
            return "";
        }

        private void CreateDirectory(string directoryPath)
        {
            Directory.CreateDirectory(directoryPath);
        }

        private bool DirectoryExists(string directoryPath)
        {
            return Directory.Exists(directoryPath);
        }

        private void CopyFile(string sourcePath, string destinationPath, bool overwrite = false)
        {
            File.Copy(sourcePath, destinationPath, overwrite);
        }

        private void DeleteFile(string filePath)
        {
            File.Delete(filePath);
        }

        private bool FileExists(string filePath)
        {
            return File.Exists(filePath);
        }       

    }
}