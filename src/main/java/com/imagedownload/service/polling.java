package com.imagedownload.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.imagedownload.comfy.ComfyClient;
import com.imagedownload.model.ImageInfo;
import com.imagedownload.parser.HistoryParser;

public class polling {
    private ComfyClient client;
    private long intervalMs;
    private String lastPromptId;
    public void startPolling() {
        int times=0;
        while(true){
            try{
            times++;
            System.out.println("Polling attempt number: "+times);
            String historyJson=client.getHistoryJson();
            String lateStringtPromptId=getLastestPromptId(historyJson);
            if(lateStringtPromptId == null || lateStringtPromptId.equals(lastPromptId)){
                Thread.sleep(intervalMs);
                continue;
            }
            List<ImageInfo> imageInfos= HistoryParser.parseImageInfoFromPrompt(historyJson, lateStringtPromptId);
            for(ImageInfo info:imageInfos){
                String urlString=HistoryParser.buildUrl("http://127.0.0.1:8188/api/view", info);
                System.out.println("Downloading from URL: "+urlString);
                client.downloadImage(urlString,info.filename);
            }
                lastPromptId=lateStringtPromptId;
                Thread.sleep(intervalMs);
                continue;
            }catch(InterruptedException e){
                System.out.println("polling interrupted");
                e.printStackTrace();
            }
            catch(Exception e){
                e.printStackTrace();
            try{
                Thread.sleep(intervalMs);
            }catch(InterruptedException ie){
                break;
            }}
        }


    }
    public polling(ComfyClient client,long intervalMs,String lastPromptId){ 
        this.client=client;
        this.intervalMs=intervalMs;
        this.lastPromptId=lastPromptId;
    }
    public static String getLastestPromptId(String jsonString){
        String latestPromptId="";
        try{
        JsonObject jsonObject= JsonParser.parseString(jsonString).getAsJsonObject();
        for(var prompt:jsonObject.entrySet()){
            latestPromptId=prompt.getKey();
        }
        }catch(Exception e){
            e.printStackTrace();}
            return latestPromptId;
    }
}  