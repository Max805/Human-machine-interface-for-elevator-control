package mainPackage;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;

//pune in functie

public class Sender {
	public static void ResetCommandValues() throws Exception {
	    URL url = new URL("http://localhost:5000/update");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setRequestProperty("Content-Type", "application/json");
		
		String jsonInput = "{"
		+ "\"targetLevel\": -1,"
		+ "\"directionRequest\": 0,"
		+ "\"doorCommand\": -1,"
		+ "\"reset\": false,"
		+ "\"speedSupervisor\": -100,"
		+ "\"crawlSelect\": -100"
		+ "}";
		//String jsonInput=String.format("{\"command\":5}");
		//System.out.println("RESETRESET");
		try (OutputStream os = con.getOutputStream()) {
		    os.write(jsonInput.getBytes());
		    os.flush();
		}
		
		int responseCode = con.getResponseCode();
		//System.out.println("Response Code: " + responseCode);
	}
 
	public static void Send() throws Exception {
	    URL url = new URL("http://localhost:5000/update");
	    HttpURLConnection con = (HttpURLConnection) url.openConnection();
	    con.setRequestMethod("POST");
	    con.setDoOutput(true);
	    con.setRequestProperty("Content-Type", "application/json");
	    
	    //// IN CONSTRUCTORUL ACESTA PUI VALORILE GLOBALE, (elevatorPos,doorStatus,motorState,cabinSensors)
	    ElevatorData data = new ElevatorData(Main.floor,
	    									 Main.doorStatus,
	    									 Main.motorState,
	    									 Main.cabinSensors); 
	    Gson gson=new Gson();
	    String jsonInput=gson.toJson(data);
	    //System.out.println("SENDSEND");
	    //System.out.println(jsonInput.toString());
	    try (OutputStream os = con.getOutputStream()) {
	    	
	        os.write(jsonInput.getBytes());
	        os.flush();
	    }
	
	    int responseCode = con.getResponseCode();
	    //System.out.println("Response Code: " + responseCode);
	} 
}