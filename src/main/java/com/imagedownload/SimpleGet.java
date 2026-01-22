package com.imagedownload;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class SimpleGet {
    private static class ImageInfo{
        String filename;
        String type;
        String subfolder;
        ImageInfo(String filename,String type,String subfolder){
            this.filename=filename;
            this.type=type;
            this.subfolder=subfolder;
        }
    }
    public static void main(String[] args) {
        int times=0;
        Set<String> seen=new HashSet<>();
        long Ms=10000;
        while(true){
            try{
                times++;
                System.out.println("Polling attempt number: "+times);
            String historyJson=getHistoryJson();
            if(historyJson == null||historyJson.isEmpty()){
                Thread.sleep(Ms);
                continue;
            }
            List<ImageInfo> imageInfos=parseFilenameFromJson(historyJson);
            for(ImageInfo info:imageInfos){
            if(!seen.contains(info.filename)){
                seen.add(info.filename);
                String urlString=buildUrl(info);
                System.out.println("Downloading from URL: "+urlString);
                downloadImage(urlString,info.filename);
            }
        }
                Thread.sleep(Ms);
                continue;
            }catch(InterruptedException e){
                System.out.println("polling interrupted");
                e.printStackTrace();
            }
            catch(Exception e){
                e.printStackTrace();
            try{
                Thread.sleep(Ms);
            }catch(InterruptedException ie){
                break;
            }}
        }


    }
    public static void downloadImage(String urlString, String filename) {
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
    public static String getHistoryJson(){
        HttpURLConnection connection=null;
        BufferedInputStream inputStream=null;
        String historyJson="";
        try {
            URL baseUrl=new URL("http://127.0.0.1:8188/history");
            connection=(HttpURLConnection) baseUrl.openConnection();
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
    public static String getLastestPromptId(String jsonString){
        String latestPromptId="";
        try{
        JsonObject jsonObject= JsonParser.parseString(jsonString).getAsJsonObject();
        for(var prompt:jsonObject.entrySet()){
            latestPromptId=prompt.getKey();
            break;
        }
    }catch(Exception e){
            e.printStackTrace();}
            return latestPromptId;
    }
        
    public static List<ImageInfo> parseFilenameFromJson(String jsonString){
        List<ImageInfo> imageInfos=new ArrayList<>();
        try{
        JsonObject jsonObject= JsonParser.parseString(jsonString).getAsJsonObject();
        for(var prompt:jsonObject.entrySet()){
            if(prompt.getValue().isJsonObject()&&prompt.getValue().getAsJsonObject().has("outputs")){
                JsonObject promptObject=prompt.getValue().getAsJsonObject();
                for(var  outputEntry:promptObject.entrySet()){
                    if(outputEntry.getKey().equals("outputs")&&outputEntry.getValue().isJsonObject()){
                        JsonObject outputObject=outputEntry.getValue().getAsJsonObject();
                        for(var nodeEntry:outputObject.entrySet()){
                            if(nodeEntry.getValue().getAsJsonObject().has("images")){
                                JsonArray imageArray=nodeEntry.getValue().getAsJsonObject().getAsJsonArray("images");
                                for(var imageElement:imageArray){
                                    if(imageElement.getAsJsonObject().has("filename")){
                                    String filename=imageElement.getAsJsonObject().get("filename").getAsString();
                                    String type=imageElement.getAsJsonObject().get("type").getAsString();
                                    String subfolder=imageElement.getAsJsonObject().get("subfolder").getAsString();
                                    imageInfos.add(new ImageInfo(filename,type,subfolder));

                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }catch(Exception e){
            e.printStackTrace();
        }
    return imageInfos;
    }
    public static String buildUrl(ImageInfo info){
        String baseString="http://127.0.0.1:8188/api/view";
        String f=URLEncoder.encode(info.filename,StandardCharsets.UTF_8);
        String type=URLEncoder.encode(info.type,StandardCharsets.UTF_8);
        String subfolder=URLEncoder.encode(info.subfolder,StandardCharsets.UTF_8);
        return baseString+"?filename="+f+"&type="+type+"&subfolder="+subfolder;
        
    }
}   