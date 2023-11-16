package com.im.daeseong.processview;

public class PackageItem {
    public String packageName;
    public String installTime;
    public String updateTime;
    public String versionName;
    public String filesize;
    public String permissionInfo;
    public String apkFilePath;

    public PackageItem(String packageName, String installTime, String updateTime, String versionName, String filesize, String permissionInfo, String apkFilePath) {
        this.packageName = packageName;
        this.installTime = installTime;
        this.updateTime = updateTime;
        this.versionName = versionName;
        this.filesize = filesize;
        this.permissionInfo = permissionInfo;
        this.apkFilePath = apkFilePath;
    }
}
