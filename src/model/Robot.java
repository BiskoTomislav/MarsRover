package model;

public interface Robot {
	
	void executeCommands(String commands);
	
	Command moveForward(int movementUnits);
	
	Command turn(int degrees);
}
