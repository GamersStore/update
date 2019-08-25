package BaseDeDatos;

import static funciones.funciones.verificarInternet;
import java.sql.*;

public class sqlServer 
{
    private static Connection conn = null;
    
    public String driver = "com.mysql.jdbc.Driver";

    public String database = "u784479595_gamer";

    public String hostname = "sql181.main-hosting.eu";

    public String port = "3306";

    public String url = "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?useSSL=false";

    public String username = "u784479595_gamer";

    public String password = "112480283-4";

    public Connection conectar()
    {
        if(verificarInternet())
        {
            try
            {
                Class.forName(driver);
                conn = DriverManager.getConnection(url, username, password);
            }
            catch (ClassNotFoundException | SQLException e)
            {
                e.printStackTrace();
            }
            return conn;
        }
        else
        {
            return conn;
        }
    }
}
