using System;
using Xamarin.Forms;

namespace ProcessView.Interface
{
    public class PackageListItem
    {
        public ImageSource PackageIcon { get; set; }

        public string PackageLabel { get; set; }

        public string PackageName { get; set; }

        public PackageListItem(ImageSource PackageIcon, string PackageLabel, string PackageName)
        {
            this.PackageIcon = PackageIcon;
            this.PackageLabel = PackageLabel;
            this.PackageName = PackageName;
        }
    }
}
