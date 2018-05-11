using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ProcessView.Interface
{
    public class PackageItem
    {
        public string InstallTime { get; set; }
        public string UpdateTime { get; set; }
        public string VersionName { get; set; }
        public string Filesize { get; set; }
        public string PermissionInfo { get; set; }
        public string ApkFilePath { get; set; }
        public string PackageName { get; set; }

        public PackageItem(string PackageName , string InstallTime, string UpdateTime, string VersionName, string Filesize, string PermissionInfo, string ApkFilePath)
        {
            this.PackageName = PackageName;
            this.InstallTime = InstallTime;
            this.UpdateTime = UpdateTime;
            this.VersionName = VersionName;
            this.Filesize = Filesize;
            this.PermissionInfo = PermissionInfo;            
            this.ApkFilePath = ApkFilePath;
        }
    }
}
