using Android.Widget;
using ProcessView.Droid.Interface;
using ProcessView.Interface;
using Xamarin.Forms;

[assembly: Dependency(typeof(CToast))]
namespace ProcessView.Droid.Interface
{
    public class CToast : IToast
    {
        public void Show(string sMessage)
        {
            Toast.MakeText(Android.App.Application.Context, sMessage, ToastLength.Short).Show();
        }
    }
}