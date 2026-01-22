package com.imagedownload;

import com.imagedownload.comfy.ComfyClient;
import com.imagedownload.service.polling;

public class app {
    private  static final String baseUrl="http://127.0.0.1:8188";
    private  static final long intervalMs=10000;
    private  static final String downloadDir="";


    public static void main(String[] args) {
        ComfyClient client=new ComfyClient(baseUrl);
        polling poller=new polling(client,intervalMs,downloadDir);
        poller.startPolling();
    }
    
}
