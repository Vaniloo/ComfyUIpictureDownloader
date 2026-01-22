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
import com.imagedownload.comfy.ComfyClient;
import com.imagedownload.model.ImageInfo;


public class SimpleGet {

    public static void main(String[] args) {
        ComfyClient client=new ComfyClient("http://127.0.0.1:8188");
        int times=0;
        Set<String> seen=new HashSet<>();
        long Ms=10000;
        while(true){
            try{
                times++;
                System.out.println("Polling attempt number: "+times);
            String historyJson=client.getHistoryJson();
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
                client.downloadImage(urlString,info.filename);
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
        String baseUrl="http://127.0.0.1:8188/api/view";
        String f=URLEncoder.encode(info.filename,StandardCharsets.UTF_8);
        String type=URLEncoder.encode(info.type,StandardCharsets.UTF_8);
        String subfolder=URLEncoder.encode(info.subfolder,StandardCharsets.UTF_8);
        return baseUrl+"?filename="+f+"&type="+type+"&subfolder="+subfolder;
        
    }
}   