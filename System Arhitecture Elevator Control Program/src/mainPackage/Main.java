package mainPackage;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import de.re.easymodbus.exceptions.ModbusException;

public class Main {
	// static global variables
	static public int target = 1;
	static public boolean doorState = false;
	static public boolean reset = false;
	static public int curentState = -1;
	static public int speed = 0;
	static public int floor;
	
	static public String response;
	
	// internal class
	static public class command {
		private int target; 
		private boolean direction;
		public command(int newTarget, boolean newDirection) {
			target = newTarget;
			direction = newDirection;
		}
		public int getTarget() {return target;}
		public boolean getDirection() {return direction;}
	}
	
	// elevator data
	static public int elevatorPos;
	static public String doorStatus;
	static public String motorState;
	static public List<Integer> cabinSensors = new ArrayList<Integer>(18);
	
	static public String logs;
	
    public static void main(String[] args) throws Exception {
    	//Sender.Send();
		//Sender.ResetCommandValues();
    	//Queue<Integer> q = new LinkedList<Integer>();
    	
    	ElevatorStateAutomaton thread1 = new ElevatorStateAutomaton("ea-pc111.ei.htwg-konstanz.de", 505);
		CommandLineInterface thread2 = new CommandLineInterface();
		MessageProcessing thread3 = new MessageProcessing();
		Receiver thread4 = new Receiver();
		
		thread1.setPriority(10);
		thread2.setPriority(5);
		thread3.setPriority(4);
		thread4.setPriority(3);
		
		thread1.start();
		thread2.start();
		thread3.start();
		thread4.start();
		
	}
}
