// See https://aka.ms/new-console-template for more information

using log4net;
using MySql.Data.MySqlClient;

namespace MotorcycleContest;

public class Program
{
    private static readonly ILog log = LogManager.GetLogger(typeof(Program));
    private static void TestConnection()
    {
        string connStr = "Server=localhost;Port=3306;Database=MotorcycleContest;UID=mariadb;PWD=mariadb;CharSet=utf8";
        using (MySqlConnection conn = new MySqlConnection(connStr))
        {
            try
            {
                conn.Open();
                Console.WriteLine("Connection successful");
            }
            catch (Exception ex)
            {
                Console.WriteLine("Connection failed: " + ex.Message);
            }
        }
    }

    public static void Main(string[] args)
    {
        //TestConnection();
        log.Info("This is an info message");
    }
}