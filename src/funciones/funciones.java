package funciones;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class funciones
{    
    public static void ejecutarAsAdm(String file)
    {
        try
        {
            Runtime.getRuntime().exec("powershell.exe Start-Process '"+file+"' -verb RunAs");
        }
        catch (IOException ex)
        {
            System.out.println(ex);
        }
    }
    
    public static boolean makeCarpeta(String ruta)
    {
        File directorio = new File(ruta);
        if(!directorio.exists())
        {
            if(directorio.mkdir())
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return true;
        }
    }
    
    public static boolean getActivo(int puerto)
    {
        try
        {
            ServerSocket SERVER_SOCKETE = new ServerSocket(puerto);
            return true;
        }
        catch (IOException x)
        {
            return false;
        }
    } 
    
    public static boolean verificarInternet()
    {
        String dirWeb = "sql181.main-hosting.eu";
        int puerto = 80;
        try
        {
            Socket s = new Socket(dirWeb, puerto);
            if(s.isConnected())
            {
                return true;
            }
        }
        catch (IOException ex)
        {
            return false;
        }
        return false;
    }
}
