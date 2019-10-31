package clases;

import config.Config;
import static funciones.funciones.ejecutarAsAdm;
import static funciones.funciones.getActivo;
import static funciones.funciones.makeCarpeta;
import static funciones.funciones.verificarInternet;
import funciones.httpRequest;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

public class downloadUpdate extends Thread
{
    JProgressBar progreso;
    
    public downloadUpdate(JProgressBar progreso)
    {
        super();
        this.progreso = progreso;
    }
    
    public void run()
    {
        if(!verificarInternet())
        {
            JOptionPane.showMessageDialog(null,"La instalacion fallo debido a que no se pudo establecer conexion con el servidor.");
        }
        else
        {
            try
            {
                //get URL update
                httpRequest request = new httpRequest();
                String update = request.execute("getUpdate.php").getString("Update");

                //Descarga de archivo
                try
                {
                    if(makeCarpeta(Config.getDirUpdate()))
                    {
                        URL url = new URL (update);

                        URLConnection urlCon = url.openConnection();

                        int tamaño = urlCon.getContentLength();
                        String nombre = update.substring((update.lastIndexOf("/")) + 1);

                        InputStream is = urlCon.getInputStream();
                        FileOutputStream fos = new FileOutputStream(Config.getDirUpdate()+nombre);

                        int tamArreglo = 1000;
                        byte[] array = new byte[tamArreglo];
                        int leido = is.read(array);
                        int cont = 0;

                        while (leido > 0)
                        {
                            fos.write(array, 0, leido);
                            leido = is.read(array);
                            cont++;

                            progreso.setValue((cont * (tamArreglo*80))/tamaño);
                        }

                        is.close();
                        fos.close();

                        System.out.println("Update descargada");
                        
                        if(!getActivo(1750))
                        {
                            JOptionPane.showMessageDialog(null,"La aplicacion se esta ejecutando por lo que es imposible actualizar, cierra la aplicacion y vuelve a intentarlo.");
                            System.exit(0);
                        }
                        else
                        {
                            progreso.setValue(85);
                        }
                        
                        progreso.setValue(90);
                        
                        //Descomprimir update
                        getZipFiles(Config.getDirUpdate()+nombre,Config.getDirInstall());
                        
                        //Borrando la update descargada en zip
                        File archivoUpdate = new File(Config.getDirUpdate()+nombre);
                        archivoUpdate.delete();
                        
                        progreso.setValue(100);
                        
                        ejecutarAsAdm(Config.getDirInstall()+"GamersStore.exe");
                        
                        System.exit(0);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null,"Ocurrio un error al tratar de descargar la actualizacion, El sistema intentara ejecutarse como administrador.");
                        ejecutarAsAdm(Config.getDirUpdate()+"update.exe");
                        System.exit(0);
                    }
                }
                catch (Exception e)
                {
                    JOptionPane.showMessageDialog(null,"Error al instalar la actualizacion: "+e);
                }
            }
            catch(Exception e)
            {
                JOptionPane.showMessageDialog(null,"Error : "+e);
            }
        }
        System.exit(0);
    }
    
    public void getZipFiles(String zipFile, String destFolder) throws IOException
    { 
        BufferedOutputStream dest = null; 
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile))); 
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null)
        { 
            System.out.println("Extracting: " + entry.getName()); 
            int count; 
            byte data[] = new byte[1024]; 

            if (entry.isDirectory())
            { 
                new File(destFolder + "/" + entry.getName()).mkdirs(); 
                continue; 
            }
            else
            { 
                int di = entry.getName().lastIndexOf('/'); 
                if (di != -1)
                { 
                    new File(destFolder + "/" + entry.getName().substring(0, di)).mkdirs(); 
                } 
            }
            FileOutputStream fos = new FileOutputStream(destFolder + "/" + entry.getName()); 
            dest = new BufferedOutputStream(fos); 
            while ((count = zis.read(data)) != -1)
            dest.write(data, 0, count); 
            dest.flush(); 
            dest.close(); 
        } 
    }
}
