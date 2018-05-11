using Android.App;
using Android.OS;
using System.Threading.Tasks;
using System.Threading;
using Android.Content;

namespace ProcessView.Droid
{
    [Activity(MainLauncher = true, Icon = "@drawable/icon", NoHistory = true, Theme = "@style/Theme.Splash")]
    public class SplashActivity : Activity
    {
        protected override void OnCreate(Bundle savedInstanceState)
        {
            base.OnCreate(savedInstanceState);

            SetContentView(Resource.Layout.SplashLayout);
        }

        protected override void OnResume()
        {
            base.OnResume();

            Task startupWork = new Task(() => { LoadMainActivity(); });
            startupWork.Start();
        }


        private void LoadMainActivity()
        {
            StartActivity(new Intent(Application.Context, typeof(MainActivity)));
        }

        public override void OnBackPressed()
        {
        }

    }
}