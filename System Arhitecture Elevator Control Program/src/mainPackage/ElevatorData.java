package mainPackage;

import java.util.List;

public class ElevatorData {
	int elevatorPosition;
	String doorStatus;
	String motorState;
	List<Integer> cabinSensors;
	
    // Constructor
	public ElevatorData(int elevatorPosition, String doorStatus, String motorState,List<Integer> cabinSensors) {
		this.elevatorPosition = elevatorPosition;
		this.doorStatus = doorStatus;
		this.motorState = motorState;
		this.cabinSensors = cabinSensors;
    }
}
