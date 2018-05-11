using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using Xamarin.Forms;

namespace ProcessView
{
    public partial class App : Application
    {
        public static void ShowOverlay(string sText)
        {
            DependencyService.Get<Interface.IOverlayDependency>().ShowOverlay(sText);
        }
        public static void HideOverlay()
        {
            DependencyService.Get<Interface.IOverlayDependency>().HideOverlay();
        }

        public App()
        {
            InitializeComponent();

            MainPage = new NavigationPage(new MainPage());
        }
        
        protected override void OnStart()
        {
            // Handle when your app starts
        }

        protected override void OnSleep()
        {
            // Handle when your app sleeps
        }

        protected override void OnResume()
        {
            // Handle when your app resumes
        }
    }
}
