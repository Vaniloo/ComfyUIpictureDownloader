package com.imagedownload.comfy;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ComfyClient {
    private final String baseUrl;
    public ComfyClient(String baseUrl){
        this.baseUrl=baseUrl;
    }
    public String getHistoryJson(){
        HttpURLConnection connection=null;
        BufferedInputStream inputStream=null;
        String historyJson="";
        try {
            URL url=new URL(baseUrl+"/history");
            connection=(HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(50000);
            connection.setReadTimeout(50000);
            connection.connect();
            int responseCode=connection.getResponseCode();
            if(responseCode>=200&&responseCode<300){
                StringBuilder sb =new StringBuilder();
                inputStream=new BufferedInputStream(connection.getInputStream());
                byte[]buffer=new byte[4096];
                int len;
                while((len=inputStream.read(buffer))!=-1){
                    sb.append(new String(buffer,0,len));
                }
                historyJson=sb.toString();
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            if(inputStream!=null){
                try{
                    inputStream.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            if(connection!=null){
                connection.disconnect();
            }
        }

        return historyJson;
    }
    public void downloadImage(String urlString, String filename) {
        HttpURLConnection connection=null;
        BufferedInputStream inputStream=null;
        BufferedOutputStream fos=null;
        try {
            URL url=new URL(urlString);
            connection=(HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(50000);
            connection.setReadTimeout(50000);
            connection.connect();
            int responseCode=connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            if(responseCode>=200&&responseCode<300){
                File file=new File(filename);
                fos=new BufferedOutputStream(new FileOutputStream(file));
                inputStream=new BufferedInputStream(connection.getInputStream());
                byte[]buffer=new byte[4096];
                int len;
                while((len=inputStream.read(buffer))!=-1){
                    fos.write(buffer,0,len);
                }
            }
            else if(responseCode==404){
                System.out.println("Resource not found (404)");
            }
            else if(responseCode==500){
                System.out.println("Server error (500)");
            }
            else if(responseCode==403){
                System.out.println("Access forbidden (403)");
            }
            else{
                System.out.println("Unhandled response code: " + responseCode);
            }
                
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(fos!=null){
                try{
                    fos.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }

    }
}