using System;
using System.Collections.Generic;
using Xamarin.Forms;

namespace ProcessView.Interface
{
    public interface IProcessInfo
    {
        List<string> GetRunningServicesName();
        List<string> GetRunningTopPackageName();
        List<string> GetCurrentTaskPackageName();
        List<string> GetAllPackageNames();
        bool CopyApk(string sPackageName);
        bool KillApp(string sPackageName);
        bool Install(string sAppPath);
        bool UnInstall(string sPackageName);
        bool StartApp(string sPackageName);
        string GetPackageLabel(string sPackageName);
        ImageSource GetPackageIcon(string sPackageName);
        PackageItem GetPackageInfo(string sPackageName);
        string GetPackageCache(string sPackageName, bool bClear = false);
    }
}
