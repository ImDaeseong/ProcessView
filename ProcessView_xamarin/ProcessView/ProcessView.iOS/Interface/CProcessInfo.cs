using System;
using NUnit.Framework;
using System.Collections.Generic;
using Xamarin.Forms;
using ProcessView.iOS.Interface;
using ProcessView.Interface;
using System.IO;

[assembly: Dependency(typeof(CProcessInfo))]
namespace ProcessView.iOS.Interface
{
    public class CProcessInfo : IProcessInfo
    {
        public List<string> GetRunningServicesName()
        {           
            return null;
        }

        public List<string> GetRunningTopPackageName()
        {
            return null;
        }

        public List<string> GetCurrentTaskPackageName()
        {
            return null;
        }

        public List<string> GetAllPackageNames()
        {
            return null;
        }

        public bool CopyApk(string sPackageName)
        {
            return false;
        }

        public bool KillApp(string sPackageName)
        {
            return false;
        }

        public bool Install(string sAppPath)
        {
            return false;
        }

        public bool UnInstall(string sPackageName)
        {
            return false;
        }

        public bool StartApp(string sPackageName)
        {
            return false;
        }

        public string GetPackageLabel(string sPackageName)
        {
            return "";
        }

        public ImageSource GetPackageIcon(string sPackageName)
        {
            return null;
        }

        public PackageItem GetPackageInfo(string sPackageName)
        {
            return null;
        }

        public string GetPackageCache(string sPackageName, bool bClear = false)
        {
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