package funciones;

import config.Config;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import org.json.JSONObject;

public class httpRequest
{
    public JSONObject execute(String File)
    {
        return execute(File, null);
    }
    
    public JSONObject execute(String File, Map<String,Object> Params)
    {
        try
        {
            URL obj = new URL(Config.getUrlApi()+File);
            HttpURLConnection conn = (HttpURLConnection)obj.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("User-Agent", "Api Desktop");
            if(Params != null)
            {
                StringBuilder postData = new StringBuilder();
                for(Map.Entry<String,Object> param : Params.entrySet())
                {
                    if(postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
                byte[] postDatabytes = postData.toString().getBytes("UTF-8");
                conn.setRequestProperty("Content-Length", String.valueOf(postDatabytes.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postDatabytes);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;
            StringBuffer response = new StringBuffer();
            while((inputLine = in.readLine()) != null){
                response.append(inputLine);
            }
            in.close();

            JSONObject json = new JSONObject(response.toString());
            return json;
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        return null;
    }
}