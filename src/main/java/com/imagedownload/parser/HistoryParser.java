package com.imagedownload.parser;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.imagedownload.model.ImageInfo;

public class HistoryParser {
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
    public static String buildUrl(String baseUrl, ImageInfo info){
        //String baseUrl="http://127.0.0.1:8188/api/view";
        String f=URLEncoder.encode(info.filename,StandardCharsets.UTF_8);
        String type=URLEncoder.encode(info.type,StandardCharsets.UTF_8);
        String subfolder=URLEncoder.encode(info.subfolder,StandardCharsets.UTF_8);
        return baseUrl+"?filename="+f+"&type="+type+"&subfolder="+subfolder;
        
    }
    
}
