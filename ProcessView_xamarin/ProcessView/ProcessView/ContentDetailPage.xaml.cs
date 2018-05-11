using System;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using ProcessView.Interface;

namespace ProcessView
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class ContentDetailPage : ContentPage
    {
        private IProcessInfo AppInfo;

        public string PackageName { get; set; }

        public ContentDetailPage(string PackageName)
        {
            InitializeComponent();

            this.PackageName = PackageName;

            AppInfo = DependencyService.Get<IProcessInfo>();

            PackageItem pkgitem = AppInfo.GetPackageInfo(PackageName);

            string sCacheInfo = AppInfo.GetPackageCache(PackageName);

            lblPackageName.Text = string.Format("패키지명: {0}", pkgitem.PackageName);
            lblInstallTime.Text = string.Format("설치일: {0}", pkgitem.InstallTime);
            lblUpdateTime.Text = string.Format("업데이트일: {0}", pkgitem.UpdateTime);            
            lblApkFilePath.Text = string.Format("APK 경로: {0}", pkgitem.ApkFilePath);
            lblFilesize.Text = string.Format("APK 파일 사이즈: {0}", pkgitem.Filesize);
            lblPermissionInfo.Text = string.Format("권한: {0}", pkgitem.PermissionInfo);
            lblCacheInfo.Text = string.Format("임시파일 삭제: {0}", sCacheInfo); 

            ImageSource icon = AppInfo.GetPackageIcon(pkgitem.PackageName);
            if (icon == null) icon = "Android.png";
            PackageIcon.Source = icon;
            
            PackageLabel.Text = AppInfo.GetPackageLabel(pkgitem.PackageName);
            PackageVersion.Text = pkgitem.VersionName;


            NavigationPage.SetHasNavigationBar(this, false);
        }

        protected override void OnAppearing()
        {
            base.OnAppearing();
        }

        protected override void OnDisappearing()
        {
            base.OnDisappearing();
        }
              
        
        private void Run_Clicked(object sender, EventArgs e)
        {
            try
            {
                AppInfo.StartApp(PackageName);
            }
            catch
            {
                string sMsg = string.Format("{0} 실행 할 수 없습니다.", PackageName);
                ToastMessage(sMsg);
            }
        }

        private void Kill_Clicked(object sender, EventArgs e)
        {
            try
            {
                if (AppInfo.KillApp(PackageName))
                {
                    string sMsg = string.Format("{0} 종료가 성공되었습니다.", PackageName);
                    ToastMessage(sMsg);
                }
                else
                {
                    string sMsg = string.Format("{0} 종료가 실패되었습니다.", PackageName);
                    ToastMessage(sMsg);
                }
            }
            catch
            {
                string sMsg = string.Format("{0} 종료 할 수 없습니다.", PackageName);
                ToastMessage(sMsg);
            }            
        }

        private void Remove_Clicked(object sender, EventArgs e)
        {
            try
            {
                if (AppInfo.UnInstall(PackageName))
                {
                    string sMsg = string.Format("{0} 제거가 완료되었습니다.", PackageName);
                    ToastMessage(sMsg);
                }
                else
                {
                    string sMsg = string.Format("{0} 제거가 실패되었습니다.", PackageName);
                    ToastMessage(sMsg);
                }

                /*                
                MessagingCenter.Send(MainPage.GetMainPageInstance(), "Uninstall", PackageName);
                await Navigation.PopModalAsync();
                */
            }
            catch { }
        }

        private void Copy_Clicked(object sender, EventArgs e)
        {
            try
            {
                if (AppInfo.CopyApk(PackageName))
                {
                    string sMsg = string.Format("{0} 복사가 완료되었습니다.", PackageName);
                    ToastMessage(sMsg);
                }
                else
                {
                    string sMsg = string.Format("{0} 복사가 실패되었습니다.", PackageName);
                    ToastMessage(sMsg);
                }
            }
            catch
            {
                string sMsg = string.Format("{0} 복사 할 수 없습니다.", PackageName);
                ToastMessage(sMsg);
            }
        }

        private void CacheDel_Clicked(object sender, EventArgs e)
        {
            try
            {
                AppInfo.GetPackageCache(PackageName, true);
            }
            catch{}
        }
                

        private void ToastMessage(string sMessage)
        {
            DependencyService.Get<IToast>().Show(sMessage);
        }

    }

}