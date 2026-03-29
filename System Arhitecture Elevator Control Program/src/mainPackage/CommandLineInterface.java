package mainPackage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CommandLineInterface extends Thread{
    public void run() {
    	
    	
    	// send command
		//*
		String usrCmd = null;
		String prevCmd = "in";
		
	    while(true) {
	    	// command processing
	    	
	    	System.out.print(Main.reset);
	    	if(!MessageProcessing.commandQ.isEmpty()) {
	    		try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
		        usrCmd = MessageProcessing.commandQ.element();
		        //if(usrCmd != prevCmd) {
			        try {
						Sender.ResetCommandValues();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			        prevCmd = usrCmd;
		        //}
		        switch(usrCmd) {
		        // reset command
		        case "reset": 
		            Main.reset = true;
		            MessageProcessing.commandQ.remove();
		            break;
		        // door commands
		        case "close": 
		            Main.doorState = false;
		            MessageProcessing.commandQ.remove();
		            break;
		        case "open": 
		            Main.doorState = true;
		            MessageProcessing.commandQ.remove();
		            break;
		            
		        // floor commands
		        case "1": 
		            Main.target = 1;
		            System.out.println("Floor1");
		            Main.logs = "Going to floor 1";
		            break;
		        case "2":
		            Main.target = 2;
		            System.out.println("Floor2");
		            Main.logs = "Going to floor 2";
		            break;
		        case "3":
		            Main.target = 3;
		            System.out.println("Floor3");
		            Main.logs = "Going to floor 3";
		            break;
		        case "4": 
		            Main.target = 4;
		            System.out.println("Floor4");
		            Main.logs = "Going to floor 4";
		            break;
		            
		          //motor commands
                case "up1": 
                	Main.speed = 6;
                    System.out.println("Starting up speed 1");
		            Main.logs = "up speed 1";
    				MessageProcessing.commandQ.remove();
                	break;
                case "up2": 
                	Main.speed = 7;
                    System.out.println("Starting up speed 2");
		            Main.logs = "up speed 2";
                    MessageProcessing.commandQ.remove();
                    break;
                case "down1": 
                	Main.speed = -6;
                    System.out.println("Starting down speed 1");
		            Main.logs = "down speed 1";
                    MessageProcessing.commandQ.remove();
                    break;
                case "down2":
                	Main.speed = -7;
                    System.out.println("Starting down speed 2");
		            Main.logs = "down speed 2";
                    MessageProcessing.commandQ.remove();
                    break;
                
                // crawl command up
                case "crawl1":
                	Main.speed = 1;
                    System.out.println("Crawl speed 1 up");
		            Main.logs = "crawl speed 1";
                	break;
                case "crawl2":
                	Main.speed = 2;
                    System.out.println("Crawl speed 2 up");
		            Main.logs = "crawl speed 2";
                	break;
                case "crawl3":
                	Main.speed = 3;
                    System.out.println("Crawl speed 3 up");
		            Main.logs = "crawl speed 3";
                	break;
                case "crawl4":
                	Main.speed = 4;
                    System.out.println("Crawl speed 4 up");
		            Main.logs = "crawl speed 4";
                	break;
                case "crawl5":
                	Main.speed = 5;
                    System.out.println("Crawl speed 5 up");
		            Main.logs = "crawl speed 5";
                	break;
                // crawl command down
                case "crawl-1":
                	Main.speed = -1;
                    System.out.println("Crawl speed 1 down");
		            Main.logs = "crawl speed -1";
                	break;
                case "crawl-2":
                	Main.speed = -2;
                    System.out.println("Crawl speed 2 down");
		            Main.logs = "crawl speed -2";
                	break;
                case "crawl-3":
                	Main.speed = -3;
                    System.out.println("Crawl speed 3 down");
		            Main.logs = "crawl speed -3";
                	break;
                case "crawl-4":
                	Main.speed = -4;
                    System.out.println("Crawl speed 4 down");
		            Main.logs = "crawl speed -4";
                	break;
                case "crawl-5":
                	Main.speed = -5;
                    System.out.println("Crawl speed 5 down");
		            Main.logs = "crawl speed -5";
                	break;
                // stop command
                case "stop":
                	Main.speed = 0;
                    System.out.println("stop");
		            Main.logs = "Stopping motor";
                    MessageProcessing.commandQ.remove();
                	break;
                default: 
                    System.out.println("Wrong syntax");
                }
	    	}
	    	//*
	    	try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				System.err.println("Failed to sleep");
			}
			//*///
	    }//*/
	    
	    
	    
	    
	    
	    
	    // command line
    	/*
        try (// command line interface
        Scanner console = new Scanner(System.in)) {
            String usrCmd = null;
            Main.target = 1;
            Main.doorState = false;
            while(true) {
                usrCmd = console.nextLine();
                Main.reset = false;
                switch(usrCmd) {
                // reset command
                case "reset": 
                    Main.reset = true;
                    break;
                // door commands
                case "close": 
                    Main.doorState = false;
                    break;
                case "open": 
                    Main.doorState = true;
                    break;
                    
                // floor commands
                case "1": 
                    Main.target = 1;
                    System.out.println("Floor1");
                    break;
                case "2":
                    Main.target = 2;
                    System.out.println("Floor2");
                    break;
                case "3":
                    Main.target = 3;
                    System.out.println("Floor3");
                    break;
                case "4": 
                    Main.target = 4;
                    System.out.println("Floor4");
                    break;
                    
                //motor commands
                case "up1": 
                	Main.speed = 6;
                    System.out.println("Starting up speed 1");
                	break;
                case "up2": 
                	Main.speed = 7;
                    System.out.println("Starting up speed 2");
                	break;
                case "down1": 
                	Main.speed = -6;
                    System.out.println("Starting down speed 1");
                	break;
                case "down2":
                	Main.speed = -7;
                    System.out.println("Starting down speed 2");
                	break;
                
                // crawl command up
                case "crawl1":
                	Main.speed = 1;
                    System.out.println("Crawl speed 1 up");
                	break;
                case "crawl2":
                	Main.speed = 2;
                    System.out.println("Crawl speed 2 up");
                	break;
                case "crawl3":
                	Main.speed = 3;
                    System.out.println("Crawl speed 3 up");
                	break;
                case "crawl4":
                	Main.speed = 4;
                    System.out.println("Crawl speed 4 up");
                	break;
                case "crawl5":
                	Main.speed = 5;
                    System.out.println("Crawl speed 5 up");
                	break;
                // crawl command down
                case "crawl-1":
                	Main.speed = -1;
                    System.out.println("Crawl speed 1 down");
                	break;
                case "crawl-2":
                	Main.speed = -2;
                    System.out.println("Crawl speed 2 down");
                	break;
                case "crawl-3":
                	Main.speed = -3;
                    System.out.println("Crawl speed 3 down");
                	break;
                case "crawl-4":
                	Main.speed = -4;
                    System.out.println("Crawl speed 4 down");
                	break;
                case "crawl-5":
                	Main.speed = -5;
                    System.out.println("Crawl speed 5 down");
                	break;
                // stop command
                case "stop":
                	Main.speed = 0;
                    System.out.println("stop");
                	break;
                default: 
                    System.out.println("Wrong syntax");
                }
            }
        } //*/
    }
}