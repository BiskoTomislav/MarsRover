package model;

@FunctionalInterface
public interface CommandFactory {

	Command getCommand(MarsRobot robot);
	
}
