package mainPackage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageProcessing extends Thread{
	// variables from the OPC-UA client
	public static int crawlSelect;
	public static int directionRequest = 1;
	public static String doorCommand;
	public static boolean reset;
	public static int speedSupervisor;

	public static boolean auxDir;
	public static int targetLevel = -1;
	/*
	+ "\"targetLevel\": -1,"
	+ "\"directionRequest\": 0,"
	+ "\"doorCommand\": -1,"
	+ "\"reset\": 0,"
	+ "\"speedSupervisor\": 0,"
	+ "\"crawlSelect\": -10"
	*/
	public static BlockingQueue<String> commandQ = new LinkedBlockingQueue<>();
	
	public static List<String> commandQup = new ArrayList<>();
	public static List<String> commandQdown = new ArrayList<>();
	
	public void run() {
		// Message processing algorithm
		int prevTarget = -2;
		boolean doorFlag = true;
		
		int prevSpeed = 0;
		while (true) {

			directionRequest = auxDir ? 1:0;
			
			// door command
			if(Main.curentState == 0 && doorCommand == "true") {
				commandQ.add("close");
				doorFlag = false;
			}
			else if(Main.curentState == 0 && doorCommand == "false") {
				commandQ.add("open");
				doorFlag = false;
			}
			
			
			if((crawlSelect != 0)) {
				if(crawlSelect == prevSpeed) {
					;
					//commandQ.add("stop");
					//prevSpeed = 0;
				} else {
					switch(crawlSelect) {
					case 1:
						commandQ.add("up1");
						prevSpeed = 1;
						break;
					case 2:
						commandQ.add("up2");
						prevSpeed = 2;
						break;
					case -1: 
						commandQ.add("down-1");
						prevSpeed = -1;
						break;
					case -2: 
						commandQ.add("down-2");
						prevSpeed = -2;
						break;
					} 
				}
			}
			

			switch(speedSupervisor) {
			case 1:
				commandQ.add("crawl1");
				prevSpeed = 1;
				break;
			case 2:
				commandQ.add("crawl2");
				prevSpeed = 1;
				break;
			case -1: 
				commandQ.add("crawl-1");
				prevSpeed = 1;
				break;
			case -2: 
				commandQ.add("crawl-2");
				prevSpeed = 1;
				break;
			case 0: 
				if(prevSpeed != 0){
					commandQ.add("stop");
					prevSpeed = 0;
				}
				break; 
			}
			
			
			if(reset == true) {
				commandQ = new LinkedBlockingQueue<>();
				commandQdown = new ArrayList<String>();
				commandQup = new ArrayList<String>();
				commandQ.add("reset");
				
				//Main.reset = true;
				reset = false;
			}
			else {
				// adding the target level to the target level
				if(targetLevel != -1 && prevTarget != targetLevel) {
					prevTarget = targetLevel;
					if(directionRequest == 0)
						commandQdown.add(targetLevel+"");
					else if(directionRequest == 1)
						commandQup.add(targetLevel+"");
				}
				// sorting both queues
				commandQdown.sort(new Comparator<String>(){public int compare(String o1, String o2){
						   if(o1 == o2) return 0;
						   if(Integer.parseInt(o1) > Integer.parseInt(o2)) return 1;
						   return -1;}});
				commandQup.sort(new Comparator<String>(){public int compare(String o1, String o2){
						   if(o1 == o2) return 0;
						   if(Integer.parseInt(o1) > Integer.parseInt(o2)) return -1;
						   return 1;}});
				// checking if the sorting was correct
				if(Main.curentState < 5) {
					commandQ.addAll(commandQup);
					commandQ.addAll(commandQdown);
				}
				else if(Main.curentState > 4) { 
					commandQ.addAll(commandQdown);
					commandQ.addAll(commandQup);
				}
				commandQup = new ArrayList<>();
				commandQdown = new ArrayList<>();
				System.out.println(commandQ);
			}
			crawlSelect = 0;
			directionRequest = -1;
			doorCommand = "closed";
			speedSupervisor = 0;
			targetLevel = -1;
			//speed supervisor
			
			//*
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.err.println("Failed to sleep");
			}//*/
		}
	}
}
