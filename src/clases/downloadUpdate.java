package clases;

import BaseDeDatos.sqlServer;
import config.Config;
import static funciones.funciones.ejecutarAsAdm;
import static funciones.funciones.getActivo;
import static funciones.funciones.makeCarpeta;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        sqlServer con = new sqlServer();
        if(con.conectar() == null)
        {
            JOptionPane.showMessageDialog(null,"La instalacion fallo debido a que no se pudo establecer conexion con el servidor.");
        }
        else
        {
            try
            {
                
                String update = "";

                //get URL update
                PreparedStatement  select = con.conectar().prepareStatement
                (
                    "SELECT Informacion FROM gamersstore WHERE Id = 8"
                );
                ResultSet result = select.executeQuery();
                while (result.next())
                {
                    update = (String)result.getObject("Informacion");
                }

                //Descarga de archivo
                try
                {
                    if(makeCarpeta(Config.getFolderDownloadUpdate()))
                    {
                        URL url = new URL (update);

                        URLConnection urlCon = url.openConnection();

                        int tamaño = urlCon.getContentLength();
                        String nombre = update.substring((update.lastIndexOf("/")) + 1);

                        InputStream is = urlCon.getInputStream();
                        FileOutputStream fos = new FileOutputStream(Config.getFolderDownloadUpdate()+nombre);

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
                        
                        //Borrar archivos actuales
                        if(!getActivo(1750))
                        {
                            JOptionPane.showMessageDialog(null,"La aplicacion se esta ejecutando por lo que es imposible actualizar, cierra la aplicacion y vuelve a intentarlo.");
                            System.exit(0);
                        }
                        else
                        {
                            progreso.setValue(85);
                            
                            //Copiar la base de datos a backup
                            File origin = new File("C:\\Program Files (x86)\\GamersStore\\GamersStore.s3db");
                            File destination = new File(Config.getFolderDownloadUpdate()+"GamersStore.s3db");
                            
                            System.out.println("Backup base de datos");
                            if (origin.exists())
                            {
                                try
                                {
                                    InputStream in = new FileInputStream(origin);
                                    OutputStream out = new FileOutputStream(destination);
                                    
                                    byte[] buf = new byte[1024];
                                    int len;
                                    while ((len = in.read(buf)) > 0)
                                    {
                                        out.write(buf, 0, len);
                                    }
                                    in.close();
                                    out.close();
                                    
                                    System.out.println("Borrando db origen");
                                    origin.delete();
                                }
                                catch (IOException ioe)
                                {
                                    JOptionPane.showMessageDialog(null,"Error al copiar los archivo.\nIntanlo nuevamente ejecutando el programa con permisos de administrador.");
                                    System.exit(0);
                                }
                            }
                            else
                            {
                                JOptionPane.showMessageDialog(null,"Error al copiar los archivo.\nIntanlo nuevamente ejecutando el programa con permisos de administrador.");
                                System.exit(0);
                            }
                            
                            System.out.println("Borrando archivos");
                            
                            if(false)
                            {
                                File carpeta = new File("C:\\Program Files (x86)\\GamersStore");
                                File[] archivos = carpeta.listFiles();

                                for (int i=0; i< archivos.length; i++)
                                {
                                    File archivo = archivos[i];
                                    if(archivo.isDirectory())
                                    {
                                        if(!archivo.getName().equals("lib"))
                                        {
                                            archivo.delete();
                                        }
                                    }
                                    else
                                    {
                                        archivo.delete();
                                    }
                                }
                            }
                        }
                        
                        progreso.setValue(90);
                        
                        //Descomprimir update
                        getZipFiles(Config.getFolderDownloadUpdate()+nombre,"C:\\Program Files (x86)\\GamersStore\\");
                        
                        //Borrando la update descargada en zip
                        File archivoUpdate = new File(Config.getFolderDownloadUpdate()+nombre);
                        archivoUpdate.delete();
                        
                        //Restaurar backup de base de datos
                        File origin = new File(Config.getFolderDownloadUpdate()+"GamersStore.s3db");
                        File destination = new File("C:\\Program Files (x86)\\GamersStore\\GamersStore.s3db");

                        if (origin.exists())
                        {
                            try
                            {
                                InputStream in = new FileInputStream(origin);
                                OutputStream out = new FileOutputStream(destination);

                                byte[] buf = new byte[1024];
                                int len;
                                while ((len = in.read(buf)) > 0)
                                {
                                    out.write(buf, 0, len);
                                }
                                in.close();
                                out.close();

                                System.out.println("Borrando db backup");
                                origin.delete();
                            }
                            catch (IOException ioe)
                            {
                                JOptionPane.showMessageDialog(null,"Error al copiar los archivo.\nSe intentara ejecutar como administrador.");
                                ejecutarAsAdm("C:\\ProgramData\\GamersStore\\update.exe");
                                System.exit(0);
                            }
                        }
                        
                        progreso.setValue(100);
                        
                        ejecutarAsAdm("C:\\Program Files (x86)\\GamersStore\\GamersStore.exe");
                        
                        System.exit(0);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null,"Ocurrio un error al tratar de descargar la actualizacion, El sistema intentara ejecutarse como administrador.");
                        ejecutarAsAdm("C:\\ProgramData\\GamersStore\\update.exe");
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
