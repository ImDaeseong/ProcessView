using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Threading.Tasks;
using Xamarin.Forms;
using Xamarin.Forms.Xaml;
using ProcessView.Interface;

namespace ProcessView
{
    [XamlCompilation(XamlCompilationOptions.Compile)]
    public partial class RunningTopView : ContentView
    {
        private ObservableCollection<PackageListItem> AppInfoList;

        private IProcessInfo AppInfo;

        public RunningTopView()
        {
            InitializeComponent();

            AppInfo = DependencyService.Get<IProcessInfo>();

            AppInfoList = new ObservableCollection<PackageListItem>();
            ProcessListview.ItemsSource = AppInfoList;
            ProcessListview.IsPullToRefreshEnabled = false;
            ProcessListview.ItemTapped += ProcessListview_ItemTapped;
            ProcessListview.ItemSelected += (s, e) => ProcessListview.SelectedItem = null;
        }

        protected override void OnParentSet()
        {
            base.OnParentSet();

            if(Parent != null)
            {                
                RefreshData();                
            } 
        }

        private async void ProcessListview_Refreshing(object sender, EventArgs e)
        {
            try
            {
                await Task.Factory.StartNew(() =>
                {
                    //ProcessListview.IsRefreshing = true;
                    LoadData();
                    ProcessListview.ItemsSource = AppInfoList;
                    ProcessListview.EndRefresh();
                    //ProcessListview.IsRefreshing = false;
                    ProcessListview.Focus();
                });
            }catch { }
        }

        private void LoadData()
        {
            AppInfoList.Clear();
            List<string> lstPackage = AppInfo.GetRunningTopPackageName(); //AppInfo.GetCurrentTaskPackageName();
            for (int i = 0; i < lstPackage.Count; i++)
            {
                ImageSource icon = AppInfo.GetPackageIcon(lstPackage[i].ToString());
                if (icon == null) icon = "Android.png";

                string sPackageLabel = AppInfo.GetPackageLabel(lstPackage[i].ToString());
                string PackageName = lstPackage[i].ToString();
                AppInfoList.Add(new PackageListItem(icon, sPackageLabel, PackageName));
            }
        }

        public async void RefreshData()
        {
            try
            {
                App.ShowOverlay("검색중");
                await Task.Factory.StartNew(() =>
                {                    
                    LoadData();
                    ProcessListview.ItemsSource = AppInfoList;
                    ProcessListview.EndRefresh();
                    App.HideOverlay();
                    ProcessListview.Focus();
                });                
            }catch { }
        }

        private async void ProcessListview_ItemTapped(object sender, ItemTappedEventArgs e)
        {
            try
            {
                var item = (PackageListItem)e.Item;
                PackageItem pkgitem = AppInfo.GetPackageInfo(item.PackageName);
                await Navigation.PushModalAsync(new ContentDetailPage(item.PackageName));
            }
            catch { }
        }

    }
}