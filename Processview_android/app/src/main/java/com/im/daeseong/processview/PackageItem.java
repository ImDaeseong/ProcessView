package com.im.daeseong.processview;

public class PackageItem
{
    public String InstallTime;
    public String UpdateTime;
    public String VersionName;
    public String Filesize;
    public String PermissionInfo;
    public String ApkFilePath;
    public String PackageName;

    public PackageItem(String PackageName , String InstallTime, String UpdateTime, String VersionName, String Filesize, String PermissionInfo, String ApkFilePath)
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