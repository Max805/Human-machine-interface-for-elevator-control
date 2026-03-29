package mainPackage;

import java.io.IOException;
import java.net.UnknownHostException;

import de.re.easymodbus.exceptions.ModbusException;
import de.re.easymodbus.modbusclient.ModbusClient;

public class ElevatorStateAutomaton extends Thread{
	// elevator PLC simulation Modbus client
	private ModbusClient elevSim;
	
	// Level variables (Between 1 and 4) 
	public int targetLevel;
	private int curentLevel; 
	private boolean doorState; // false = closed/true = opened
	private boolean reset; // false = not reseting/true = resetting
	
	// automaton state variable
	private int state;
	
	// PLC sensors addresses variables
	private int lvlReached;
	private int sftUpperLvl;
	private int sftLowerLvl;
	private int aprUpperLvl;
	private int aprLowerLvl; 
	
	// Constructor
	ElevatorStateAutomaton(String clientAddress, int port) throws UnknownHostException, IOException, ModbusException, InterruptedException {
		elevSim = new ModbusClient(clientAddress, port);
		while (!elevSim.isConnected()) 
			elevSim.Connect();
		elevSim.WriteSingleCoil(0, true);
		synchronized (elevSim){
	       try{
	    	   elevSim.wait(10);
	       } catch (InterruptedException e) {
	          e.printStackTrace();
	       }
	    }
		elevSim.WriteSingleCoil(0, false);
		stopMotor();
		
		state = 0;
		if(elevSim.ReadDiscreteInputs(2, 1)[0]) curentLevel = 1;
		if(elevSim.ReadDiscreteInputs(10, 1)[0]) curentLevel = 2;
		if(elevSim.ReadDiscreteInputs(18, 1)[0]) curentLevel = 3;
		if(elevSim.ReadDiscreteInputs(26, 1)[0]) curentLevel = 4;
		targetLevel = 1;
		// Getting the current state of the system and initializing the automaton
		lvlReached  = (targetLevel-1)*8 + 2;
		sftUpperLvl = (targetLevel-1)*8 + 3;
		sftLowerLvl = (targetLevel-1)*8 + 1;
		aprUpperLvl = (targetLevel-1)*8 + 4;
		aprLowerLvl = (targetLevel-1)*8;
	}

	// Main elevator routines	
	private void stopMotor() throws ModbusException, IOException { 
		elevSim.WriteSingleCoil(10,false);	// up 	 speed 1 turning off
		elevSim.WriteSingleCoil(11,false);	// up 	 speed 2 turning off
		elevSim.WriteSingleCoil(9,false);	// down  speed 1 turning off
		elevSim.WriteSingleCoil(8,false);	// down  speed 2 turning off
		elevSim.WriteSingleRegister(1, 0);	// crawl speed   turning off
		System.out.println("Stopping the motor");
	}
	private void openDoor() throws ModbusException, IOException, InterruptedException {
		elevSim.WriteSingleCoil(13, true);
		synchronized (elevSim){
		       try{
		    	   elevSim.wait(4000);
		       } catch (InterruptedException e) {
		          e.printStackTrace();
		       }
		    }
		elevSim.WriteSingleCoil(13, false);
	}
	private void closeDoor() throws ModbusException, IOException, InterruptedException {
		elevSim.WriteSingleCoil(12, true);
		synchronized (elevSim){
		       try{
		    	   elevSim.wait(4000);
		       } catch (InterruptedException e) {
		          e.printStackTrace();
		       }
		    }
		elevSim.WriteSingleCoil(12, false);
	}
	private void state00StartingRoutine() throws ModbusException, IOException, InterruptedException {
		openDoor();
		System.out.println("Elevator is stationary.");
	}
	private void state01StartingRoutine() throws ModbusException, IOException {
		elevSim.WriteSingleCoil(11, true);
		System.out.println("Elevator is moving up with the fast speed.");
	}
	private void state02StartingRoutine() throws ModbusException, IOException {
		elevSim.WriteSingleCoil(10, true);
		System.out.println("Elevator is moving up with the slow speed.");
	}
	private void state03StartingRoutine() throws InterruptedException {
		synchronized (elevSim){
	       try{
	    	   elevSim.wait(700);
	       } catch (InterruptedException e) {
	          e.printStackTrace();
	       }
	    }
		System.out.println("Elevator is waiting to settle the upwards movement");
	}
	private void state04StartingRoutine() throws ModbusException, IOException {
		elevSim.WriteSingleRegister(1, -1);
		System.out.println("Elevator is moving down with crawl speed 1");
	}
	private void state05StartingRoutine() throws ModbusException, IOException {
		elevSim.WriteSingleCoil(8, true);
		System.out.println("Elevator is is moving down with the fast speed.");
	}
	private void state06StartingRoutine() throws ModbusException, IOException {
		elevSim.WriteSingleCoil(9,true);
		System.out.println("Elevator is moving down with the slow speed.");
	}
	private void state07StartingRoutine() throws InterruptedException {
		synchronized (elevSim){
	       try{
	    	   elevSim.wait(700);
	       } catch (InterruptedException e) {
	          e.printStackTrace();
	       }
	    }
		System.out.println("Elevator is waiting to settle the downward movement");
	}
	private void state08StartingRoutine() throws ModbusException, IOException {
		elevSim.WriteSingleRegister(1, 1);
		System.out.println("Elevator is moving up with crawl speed 1");
	}
	
	// Thread run method
	public void run() {
		state = 0;
		Main.curentState = 0;
		try {
			elevSim.WriteSingleCoil(12, false);
		} catch (ModbusException | IOException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		try {
			elevSim.WriteSingleCoil(13, false);
		} catch (ModbusException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(true) {
			
			if(elevSim.isConnected()) {
				if(reset) {
					state = 0;
					Main.curentState = 0;
					System.out.println("Resetting");
					reset = false;
					Main.reset = false;
					try {
						// stopping the motor
						stopMotor();
						
						// setting the close/open door flags to false
						elevSim.WriteSingleCoil(12, false);
						elevSim.WriteSingleCoil(13, false);
						
						// reseting the system
						elevSim.WriteSingleCoil(0, true);
						Thread.sleep(10);
						elevSim.WriteSingleCoil(0, false);
						

						stopMotor();
						elevSim.WriteSingleCoil(12, false);
						elevSim.WriteSingleCoil(13, false);
					} catch (ModbusException | IOException | InterruptedException e) {
						System.err.println("Failed reseting");
					}
				} else {
					// Calculating the current addresses from the target level
					lvlReached  = (targetLevel-1)*8 + 2;
					sftUpperLvl = (targetLevel-1)*8 + 3;
					sftLowerLvl = (targetLevel-1)*8 + 1;
					aprUpperLvl = (targetLevel-1)*8 + 4;
					aprLowerLvl = (targetLevel-1)*8;
					
					if(Main.speed!=0)
						state = 9;
					// Executing the state routines
					switch(state) {
					// Normal elevator
					case 0: // Stationary state
						// Checking if the target level is lower than the current level
						try {
							Main.curentState = 0;
							if(targetLevel == curentLevel) {
								if(doorState) {
									System.out.println("opening door");
									//doorState = false;
									//Main.doorState = false;
									openDoor();
									Main.doorStatus = "open";
									openDoor();
								}
								else {
									closeDoor();
									Main.doorStatus = "closed";
								}
								Main.motorState = "off";
							}
							if(targetLevel < curentLevel) {
								closeDoor();
								state = 5;
								stopMotor();
								state05StartingRoutine();
								System.out.println(targetLevel); 
								System.out.println(curentLevel);
							}
							if(targetLevel > curentLevel) {
								
								closeDoor();
								stopMotor();
								state = 1;
								state01StartingRoutine();
								System.out.println(targetLevel); 
								System.out.println(curentLevel);
							}
							if(Main.speed != 0)
								state = 9;
						} catch (ModbusException | IOException | InterruptedException e) {
							System.err.println("Couldn't shut down the motors");
						}
						break;
					case 1: // Up2 state
						// Checking if the approach lower sensor has been reached
						try {
							if(elevSim.ReadDiscreteInputs(aprLowerLvl, 1)[0]) {
								elevSim.WriteSingleCoil(11,false);	// up 	 speed 2 turning off
								state = 2;
								state02StartingRoutine();
							}Main.curentState = 1;
							Main.motorState = "upFast";
						} catch (ModbusException | IOException e) {
							System.err.println("Couldn't read the aproach lower sensor");
						}
						break;
					case 2: // Up1 state
						// Checking if the safety lower sensor has been reached
						try {
							
							if(elevSim.ReadDiscreteInputs(sftLowerLvl, 1)[0]) {
								elevSim.WriteSingleCoil(10,false);	// up 	 speed 1 turning off
								state = 3;
								
								MessageProcessing.commandQ.remove();
								
								state03StartingRoutine();
							}Main.curentState = 2;
							Main.motorState = "upSlow";
						} catch (ModbusException | IOException | InterruptedException e) {
							System.err.println("Couldn't read the safety lower sensor");
						}
						break;
					case 3: // Waiting to stop the upwards movement state
						try {
							elevSim.WriteSingleCoil(10,false);	// up 	 speed 1 turning off
							elevSim.WriteSingleCoil(11,false);	// up 	 speed 2 turning off
							elevSim.WriteSingleCoil(9,false);	// down  speed 1 turning off
							elevSim.WriteSingleCoil(8,false);	// down  speed 2 turning off
							elevSim.WriteSingleRegister(1, 0);	// crawl speed   turning off
							Main.curentState = 3;
							state = 4;
							Main.motorState = "off";
							state04StartingRoutine();
							
						} catch (ModbusException | IOException e) {
							System.err.println("Couldn't shut down the motor");
						}
						break;
					case 4: // crawl speed -1 state
						try {
							
							if(elevSim.ReadDiscreteInputs(lvlReached, 1)[0]) {
								elevSim.WriteSingleRegister(1, 0);	// crawl speed   turning off
								state = 0;
								state00StartingRoutine();
								Main.doorStatus = "open";
								curentLevel = targetLevel;
								Thread.sleep(6000);
								Main.doorStatus = "closed";
								
							}Main.curentState = 4;
							Main.motorState = "crawl -1";
						} catch (ModbusException | IOException | InterruptedException e) {
							System.err.println("Couldn't read the level reached sensor");
						}
						break;
					case 5: // Down2 state
						// Checking if the approach upper sensor has been reached 
						try {
							
							if(elevSim.ReadDiscreteInputs(aprUpperLvl, 1)[0]) {
								elevSim.WriteSingleCoil(8,false);	// down  speed 2 turning off
								state = 6;
								state06StartingRoutine();
							}Main.curentState = 5;
							Main.motorState = "Down fast";
						} catch (ModbusException | IOException e) {
							System.err.println("Couldn't read the aproach upper sensor");
						}
						break;
					case 6: // Down1 state
						// Checking if the safety Upper sensor has been reached
						try {
							
							if(elevSim.ReadDiscreteInputs(sftUpperLvl, 1)[0]) {
								elevSim.WriteSingleCoil(9,false);	// down  speed 2 turning off
								
								
								MessageProcessing.commandQ.remove();
								
								
								state = 7;
								state07StartingRoutine();
							}Main.curentState = 6;
							Main.motorState = "Down slow";
						} catch (ModbusException | IOException | InterruptedException e) {
							System.err.println("Couldn't read the safety Upper sensor");
						}
						break;
					case 7: // Waiting to stop the downwards movement state
						try {
							elevSim.WriteSingleCoil(9,false);	// down  speed 1 turning off
							elevSim.WriteSingleCoil(8,false);	// down  speed 2 turning off
							elevSim.WriteSingleRegister(1, 0);	// crawl speed   turning off
							Main.curentState = 7;
							state = 8;
							state08StartingRoutine();
							Main.motorState = "off";
						} catch (ModbusException | IOException e) {
							System.err.println("Couldn't shut down the motor");
						}
						break;
					case 8: // crawl speed 1 state
						try {
							Main.curentState = 8;
							if(elevSim.ReadDiscreteInputs(lvlReached, 1)[0]) {
								elevSim.WriteSingleRegister(1, 0);	// crawl speed   turning off
								state = 0;
								state00StartingRoutine();
								Main.doorStatus = "open";
								curentLevel = targetLevel;
								Thread.sleep(6000);
								Main.doorStatus = "closed";
							}
							Main.motorState = "crawl +1";
						} catch (ModbusException | IOException | InterruptedException e) {
							System.err.println("Couldn't read the level reached sensor");
						}
						break;
						
					// states accessed by the administrator commands
					// speed supervisor states and crawl select
					
					case 9: // motor speed control
						try {
							if(Main.speed == 0) {
								stopMotor();
								//Main.curentState = 0;
								state = 0;
							} else { 
								//Main.curentState = 9;
								state = 9;
								switch(Main.speed) {
								// down speed
								case -1:
									elevSim.WriteSingleRegister(1, -1);
									break;
								case -2: 
									elevSim.WriteSingleRegister(1, -2);
									break;
								case -3:
									elevSim.WriteSingleRegister(1, -3);
									break;
								case -4: 
									elevSim.WriteSingleRegister(1, -4);
									break;
								case -5:
									elevSim.WriteSingleRegister(1, -5);
									break;
								case -6:
									elevSim.WriteSingleCoil(9,true);
									break;
								case -7:
									elevSim.WriteSingleCoil(8,true);
									break;
								// up speed
								case 1: 
									elevSim.WriteSingleRegister(1, 1);
									break;
								case 2: 
									elevSim.WriteSingleRegister(1, 2);
									break;
								case 3:
									elevSim.WriteSingleRegister(1, 3);
									break;
								case 4: 
									elevSim.WriteSingleRegister(1, 4);
									break;
								case 5:
									elevSim.WriteSingleRegister(1, 5);
									break;
								case 6:
									elevSim.WriteSingleCoil(10,true);
									break;
								case 7:
									elevSim.WriteSingleCoil(11,true);
									break;
								default:
									state = 0;
									//Main.curentState = 0;
									stopMotor();
								}
							}
						} catch (ModbusException | IOException e) {
							System.err.println("Motor error");
						}
						break;
					default: System.out.println("default");
					}
				}
			}
			else { 
				// restarting the plc simulation in case of diconection
				System.out.println("Restarting PLC connection");
				try {
					elevSim.Connect();
					System.out.println("PLC connection succesful.");
				} catch (IOException e) {
					System.err.println("Failed to connect.");
				}
			}
			//*
			// Sleeping at the end of the loop
			
			/*
			try {
				synchronized (elevSim){
		    	   elevSim.wait(10);
			    }
			} catch (InterruptedException e1) {
				System.out.println("failed to sleep");
			}//*/
			
			
			// Getting info from the outside
			doorState = Main.doorState;
			targetLevel = Main.target;
			reset = Main.reset;	
			Main.floor = curentLevel;
			try {
				Sender.Send();
			} catch (Exception e2) {
				System.out.println("failed sending info to the opcua client");
			}
			
		}
	}
}
