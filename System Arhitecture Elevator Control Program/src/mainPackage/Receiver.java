package mainPackage;

import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Receiver extends Thread {


    @Override
    public void run() {
        while (true) {
            try {
                URL url = new URL("http://localhost:5000/values");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                int status = con.getResponseCode();
                if (status == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuilder content = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        content.append(inputLine);
                    }
                    in.close();
                   // System.out.println("Response from Python REST API: " + content.toString());
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.fromJson(content.toString(), JsonObject.class);

                    // Extracting individual values
                    MessageProcessing.crawlSelect	   = jsonObject.get("crawlSelect").getAsInt();
                    MessageProcessing.auxDir		   = jsonObject.get("directionRequest").getAsBoolean();
                    MessageProcessing.doorCommand 	   = jsonObject.get("doorCommand").getAsString();
                    MessageProcessing.reset 		   = jsonObject.get("reset").getAsBoolean();
                    MessageProcessing.speedSupervisor  = jsonObject.get("speedSupervisor").getAsInt();
                    MessageProcessing.targetLevel 	   = jsonObject.get("targetLevel").getAsInt();
                    
                    //*
                    //debug 
                    System.out.println("------------------Start of Receiver values ---------------------");
                    System.out.println("crawlSelect: " +  MessageProcessing.crawlSelect);
                    System.out.println("directionRequest: " +  MessageProcessing.auxDir);
                    System.out.println("doorCommand: " +  MessageProcessing.doorCommand);
                    System.out.println("reset: " +  MessageProcessing.reset);
                    System.out.println("speedSupervisor: " +  MessageProcessing.speedSupervisor);
                    System.out.println("targetLevel: " +  MessageProcessing.targetLevel);
                    System.out.println("------------------End of Receiver values -----------------------");
                    //*/
                    
 
                  
                } else {
                    System.out.println("Error: HTTP status " + status);
                }

                con.disconnect();
                Thread.sleep(1000); // 1 s

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {}
            }
        }
    }
    /*
    // FOR DEBUGGING
    public static void main(String[] args) {
        Receiver connector = new Receiver();
        Thread thread = new Thread(connector);
        thread.start();
    }
    */
}