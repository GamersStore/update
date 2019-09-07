package config;

import java.awt.Toolkit;
import javax.swing.JFrame;

public class Config
{
    private static Config instanciaInit = null;
    public static Config getInstance()
    {
        if(instanciaInit == null)
        {
            instanciaInit = new Config();
        }
        return instanciaInit;
    }
    
    public static String dirUpdate = "C:\\ProgramData\\GamersStore\\";
    public static String dirInstall = "C:\\Program Files\\GamersStore\\";
    
    public static String getDirUpdate()
    {
        return dirUpdate;
    }
    
    public static String getDirInstall()
    {
        return dirInstall;
    }
    
    public static void setIcon(JFrame frame)
    {
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(Config.getInstance().getClass().getResource("/lib/images/LogoGS.png")));
    }
    
    public static void center(JFrame frame)
    {
        frame.setLocationRelativeTo(null);
    }
}
