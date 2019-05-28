using SendGrid;
using SendGrid.Helpers.Mail;
using System;
using System.Threading.Tasks;

namespace CarbonCloud.Providers
{
    public class EmailProvider
    {
        public static async Task SentCodeToEmail(string email, string code)
        {
            var apiKey = "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";
            var client = new SendGridClient(apiKey);
            var from = new EmailAddress("noreplycorbondrive@gmail.com", "CarbonCloud");
            var subject = "Підтверження email";
            var to = new EmailAddress(email);
            var htmlContent = $"<h2>Код підтверження {code}</h2>";
            var msg = MailHelper.CreateSingleEmail(from, to, subject, null, htmlContent);
            var response = await client.SendEmailAsync(msg);

            //string a = "vasiliypetrov";
            //MailAddress from = new MailAddress("kovalalex2018@gmail.com", "Tom");
            //MailAddress to = new MailAddress(email);
            //MailMessage m = new MailMessage(from, to);
            //m.Subject = "Підтверження email";
            //m.Body = $"<h2>Код підтверження {code}</h2>";
            //m.IsBodyHtml = true;
            //SmtpClient smtp = new SmtpClient("smtp.gmail.com", 587);
            //smtp.Credentials = new NetworkCredential("kovalalex2018@gmail.com", a.GetHashCode().ToString());
            //smtp.EnableSsl = true;
            //smtp.Send(m);
            //string a = "carbondrive666";
            //MailAddress from = new MailAddress("noreplycorbondrive@gmail.com", "no-reply");
            //MailAddress to = new MailAddress(email);
            //MailMessage m = new MailMessage(from, to);
            //m.Subject = "Підтверження email";
            //m.Body = $"<h2>Код підтверження {code}</h2>";
            //m.IsBodyHtml = true;
            //SmtpClient smtp = new SmtpClient("smtp.gmail.com", 587);
            //smtp.Credentials = new NetworkCredential("noreplycorbondrive@gmail.com", a);
            //smtp.EnableSsl = true;
            //smtp.Send(m);
            //string a = "carbondrive666";
            //MailAddress from = new MailAddress("noreplycorbondrive@gmail.com", "noreply");
            //MailAddress to = new MailAddress(email);
            //MailMessage m = new MailMessage(from, to);
            //m.Subject = "Підтверження email";
            //m.Body = $"<h2>Код підтверження: { code}</h2>";
            //m.IsBodyHtml = true;
            //SmtpClient smtp = new SmtpClient("smtp.gmail.com", 587);
            //smtp.Credentials = new NetworkCredential("noreplycorbondrive@gmail.com", a.GetHashCode().ToString());                
            //smtp.EnableSsl = true;
            //smtp.Send(m);
        }
    }
}