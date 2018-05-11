using System;
using Xamarin.Forms;

namespace ProcessView
{
    public class SelectedViewCell : ViewCell
    {
        public static readonly BindableProperty SelectedBackgroundColorProperty =
            BindableProperty.Create("SelectedBackgroundColor", typeof(Color), typeof(ViewCell), Color.Transparent);

        public Color SelectedBackgroundColor
        {
            get { return (Color)GetValue(SelectedBackgroundColorProperty); }
            set { SetValue(SelectedBackgroundColorProperty, value); }
        }
    }
}
