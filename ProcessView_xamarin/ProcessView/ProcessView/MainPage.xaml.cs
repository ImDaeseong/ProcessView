using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Xamarin.Forms;
using ProcessView.Interface;

namespace ProcessView
{
    public partial class MainPage : ContentPage
    {
        public static MainPage instance = new MainPage();
        public static MainPage GetMainPageInstance()
        {
            if (instance == null)
            {
                return new MainPage();
            }
            return instance;
        }

        public int nSelectTabIndex;

        public MainPage()
        {
            InitializeComponent();

            /*
            MessagingCenter.Unsubscribe<MainPage, string>(this, "Uninstall");

            MessagingCenter.Subscribe<MainPage, string>(this, "Uninstall", (sender, arg) => {              
                if (DependencyService.Get<IProcessInfo>().UnInstall(arg))
                {
                    string sMsg = string.Format("{0} 제거가 완료되었습니다.", arg);
                    DependencyService.Get<IToast>().Show(sMsg); 
                }
                else
                {
                    string sMsg = string.Format("{0} 제거가 실패되었습니다.", arg);
                    DependencyService.Get<IToast>().Show(sMsg);
                }
            });
            */

            NavigationPage.SetHasNavigationBar(this, false);

            SetTabTextColor(1);
        }

        private void TapGestureRecognizerRun_Tapped(object sender, EventArgs e)
        {
            try
            {
                if (nSelectTabIndex == 1) return;

                var content = new RunningTopView();
                ChangeContent.Content = content;
                SetTabTextColor(1);
            }
            catch { }
        }

        private void TapGestureRecognizerService_Tapped(object sender, EventArgs e)
        {
            try
            {
                if (nSelectTabIndex == 2) return;

                var content = new RunningServiceView();
                ChangeContent.Content = content;
                SetTabTextColor(2);
            } catch { }
        }
        
        private void TapGestureRecognizerAll_Tapped(object sender, EventArgs e)
        {
            try
            {
                if (nSelectTabIndex == 3) return;

                var content = new InstallAllView();
                ChangeContent.Content = content;
                SetTabTextColor(3);
            } catch { }
        }

        private void SetTabTextColor(int nIndex)
        {
            nSelectTabIndex = nIndex;

            if (nIndex == 1)
            {
                sl1.ScaleTo(1, 75);
                sl2.ScaleTo(0.9, 75);
                sl3.ScaleTo(0.9, 75);

                tab1.TextColor = Color.FromHex("#1DA1F2");
                tab2.TextColor = Color.FromHex("#8899A6");
                tab3.TextColor = Color.FromHex("#8899A6");
            }
            else if (nIndex == 2)
            {
                sl1.ScaleTo(0.9, 75);
                sl2.ScaleTo(1, 75);
                sl3.ScaleTo(0.9, 75);

                tab1.TextColor = Color.FromHex("#8899A6");
                tab2.TextColor = Color.FromHex("#1DA1F2");
                tab3.TextColor = Color.FromHex("#8899A6");
            }
            else if (nIndex == 3)
            {
                sl1.ScaleTo(0.9, 75);
                sl2.ScaleTo(0.9, 75);
                sl3.ScaleTo(1, 75);

                tab1.TextColor = Color.FromHex("#8899A6");
                tab2.TextColor = Color.FromHex("#8899A6");
                tab3.TextColor = Color.FromHex("#1DA1F2");
            }          
        }
        
    }
}
