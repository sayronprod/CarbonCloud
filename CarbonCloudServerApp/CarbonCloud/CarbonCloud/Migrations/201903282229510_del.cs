namespace CarbonCloud.Migrations
{
    using System;
    using System.Data.Entity.Migrations;
    
    public partial class del : DbMigration
    {
        public override void Up()
        {
            DropColumn("dbo.AspNetUsers", "UserFolderName");
        }
        
        public override void Down()
        {
            AddColumn("dbo.AspNetUsers", "UserFolderName", c => c.String(nullable: false));
        }
    }
}
