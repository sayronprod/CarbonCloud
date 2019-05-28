using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace CarbonCloud.Models
{
    public class FileRenameModel
    {
        public bool isFolder { get; set; }
        public string oldpath { get; set; }
        public string newpath { get; set; }
    }
}