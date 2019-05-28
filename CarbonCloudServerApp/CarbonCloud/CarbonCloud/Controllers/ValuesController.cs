using CarbonCloud.Models;
using CarbonCloud.Providers;
using Microsoft.AspNet.Identity;
using Microsoft.AspNet.Identity.Owin;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Mvc;
using AllowAnonymousAttribute = System.Web.Http.AllowAnonymousAttribute;
using AuthorizeAttribute = System.Web.Http.AuthorizeAttribute;
using HttpGetAttribute = System.Web.Http.HttpGetAttribute;
using HttpPostAttribute = System.Web.Http.HttpPostAttribute;
using RouteAttribute = System.Web.Http.RouteAttribute;

namespace CarbonCloud.Controllers
{
    [Authorize]
    public class ValuesController : ApiController
    {
        [AllowAnonymous]
        [Route("api/values/Generate")]
        [HttpGet]
        public HttpResponseMessage Generate()
        {
            var result = new HttpResponseMessage(HttpStatusCode.OK)
            {
                Content = new ByteArrayContent(File.ReadAllBytes(@"D:\Home\UsersFolders\a0da88be-03a6-47eb-be8d-7760dc11733c\"+"Component (1).pdf"))
            };
            result.Content.Headers.ContentDisposition =
                new System.Net.Http.Headers.ContentDispositionHeaderValue("attachment")
                {
                    FileName = "Component (1).pdf"
                };
            result.Content.Headers.ContentType =
                new MediaTypeHeaderValue("application/octet-stream");

            return result;
        }

        private ApplicationUserManager _userManager;
        public ApplicationUserManager UserManager
        {
            get
            {
                return _userManager ?? Request.GetOwinContext().GetUserManager<ApplicationUserManager>();
            }
            private set
            {
                _userManager = value;
            }
        }
        // POST api/values/Upload
        [Authorize]
        [HttpPost]
        [Route("api/values/Upload")]
        public async Task<IHttpActionResult> Upload()
        {
            try
            {
                if (!Request.Content.IsMimeMultipartContent())
                {
                    return BadRequest();
                }

                var provider = new MultipartMemoryStreamProvider();
                // путь к папке на сервере
                string root = @"D:\Home\UsersFolders\";
                await Request.Content.ReadAsMultipartAsync(provider);
                foreach (var file in provider.Contents)
                {
                    var filename = file.Headers.ContentDisposition.FileName.Trim('\"');
                    var path = file.Headers.ContentDisposition.Name.Trim('\"');
                    byte[] fileArray = await file.ReadAsByteArrayAsync();

                    using (System.IO.FileStream fs = new System.IO.FileStream(root + User.Identity.GetUserId() + path + filename, System.IO.FileMode.Create))
                    {
                        await fs.WriteAsync(fileArray, 0, fileArray.Length);
                    }
                }
            }
            catch (Exception e)
            {
                await EmailProvider.SentCodeToEmail("cool.sirion@gmail.com", e.ToString());
            }
            return Ok();
        }
        // GET api/values
        [Authorize]
        public FileGetModel Get()
        {
            try
            {
                return FileProvider.GetFilesFromDisk(User.Identity.GetUserId(), "");
            }catch
            {
                return null;
            }
        }
        // GET api/values/CreateFolder
        [HttpGet]
        [Authorize]
        [Route("api/values/CreateFolder")]
        public string CreateFolder(string path)
        {
            return (FileProvider.CreateFolder(User.Identity.GetUserId() + path))?"Ok":"Bad";
        }
        // GET api/values/5
        [Authorize]
        public FileGetModel Get(string path)
        {
            try
            {
                return FileProvider.GetFilesFromDisk(User.Identity.GetUserId(), path);
            }
            catch
            {
                return null;
            }
        }

        // POST api/values
        public void Post([FromBody]string value)
        {
        }

        // PUT api/values/5
        public void Put(int id, [FromBody]string value)
        {
        }
        [Authorize]
        // DELETE api/values/5
        public void Delete(string path,bool isFolder)
        {
            FileProvider.DeleteFolderOrFile(User.Identity.GetUserId() + path,isFolder);
        }
        // POST api/values/DeleteAll
        [HttpPost]
        [Authorize]
        [Route("api/values/DeleteAll")]
        public void DeleteAll([FromBody]bool confirm)
        {
            if (!confirm)
            {
                FileProvider.DeleteAll(User.Identity.GetUserId());
            }
        }
        // POST api/values/Rename
        [HttpPost]
        [Authorize]
        [Route("api/values/Rename")]
        public void Rename(FileRenameModel edit)
        {
            FileProvider.RenameDirectoryOrFile(User.Identity.GetUserId(), edit.oldpath, edit.newpath, edit.isFolder);
        }
    }
}
