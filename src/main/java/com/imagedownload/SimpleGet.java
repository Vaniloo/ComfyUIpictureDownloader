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
import com.imagedownload.parser.HistoryParser;


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
            List<ImageInfo> imageInfos= HistoryParser.parseFilenameFromJson(historyJson);
            for(ImageInfo info:imageInfos){
            if(!seen.contains(info.filename)){
                seen.add(info.filename);
                String urlString=HistoryParser.buildUrl("http://127.0.0.1:8188/api/view", info);
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
}   