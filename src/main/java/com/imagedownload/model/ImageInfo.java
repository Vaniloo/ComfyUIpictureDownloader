package com.imagedownload.model;

public class ImageInfo{
        public final String filename;
        public final String type;
        public final String subfolder;
        public ImageInfo(String filename,String type,String subfolder){
            this.filename=filename;
            this.type=type;
            this.subfolder=subfolder;
        }
}