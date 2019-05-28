using CarbonCloud.Models;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Threading.Tasks;
using System.Web;

namespace CarbonCloud.Providers
{
    public class FileProvider
    {
        public static string UsersFolder= @"D:\Home\UsersFolders\";
        public static void CreateFolderForNewUser(string foldername)
        {
            Directory.CreateDirectory(UsersFolder + foldername);            
        }
        public static bool CreateFolder(string path)
        {
            if(!Directory.Exists(UsersFolder+path))
            {
                try
                {
                    Directory.CreateDirectory(UsersFolder + path);
                }
                catch
                {
                    return false;
                }
                return true;
            }else
            {
                return false;
            }
        }
        public static void DeleteAll(string user_id)
        {
            DirectoryInfo info = new DirectoryInfo(UsersFolder + user_id);
            FileInfo[] files = info?.GetFiles();
            DirectoryInfo[] dirs = info.GetDirectories();
            foreach(FileInfo file in files)
            {
                file.Delete();
            }
            foreach(DirectoryInfo folder in dirs)
            {
                folder.Delete(true);
            }
        }
        public static void DeleteFolderOrFile(string path,bool isFolder)
        {
            if (isFolder)
            {
                Directory.Delete(UsersFolder + path, true);
            }else
            {
                File.Delete(UsersFolder + path);
            }
            
        }
        public static void RenameDirectoryOrFile(string id,string old,string newname,bool isFolder)
        {
            string path = UsersFolder + id;
            if (isFolder)
            {
                Directory.Move(path + old, path + newname);
            }else
            {
                File.Move(path + old, path + newname);
            }
        }
        public static FileGetModel GetFilesFromDisk(string id,string folder)
        {
            DirectoryInfo info = new DirectoryInfo(UsersFolder + id + folder);
            FileInfo[] files = info?.GetFiles();
            DirectoryInfo[] dirs = info.GetDirectories();
            string[] filesexport=new string[files.Length];
            string[] dirsexport = new string[dirs.Length];
            for(int i=0;i<files.Length;i++)
            {
                filesexport[i] = files[i].Name;
            }
            for(int i=0;i<dirs.Length;i++)
            {
                dirsexport[i] = dirs[i].Name;
            }
            FileGetModel fileGetModel = new FileGetModel { Dirs = dirsexport, Files = filesexport };
            return fileGetModel;
        }
    }
}