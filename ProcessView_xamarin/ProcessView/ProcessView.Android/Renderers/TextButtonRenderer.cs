using Android.Views;
using ProcessView.Droid.Renderers;
using ProcessView.Renderers;
using Xamarin.Forms;
using Xamarin.Forms.Platform.Android;

[assembly: ExportRenderer(typeof(TextButton), typeof(TextButtonRenderer))]
namespace ProcessView.Droid.Renderers
{
    public class TextButtonRenderer : ButtonRenderer
    {
        protected override void OnElementChanged(ElementChangedEventArgs<Xamarin.Forms.Button> e)
        {

            base.OnElementChanged(e);
            if (Control != null)
            {
                Control.Gravity = GravityFlags.Center;
                Control.SetPadding(0, 15, 0, 0);
                Control.SetBackgroundResource(Resource.Drawable.TextButtonEditText);
            }
        }
    }
}