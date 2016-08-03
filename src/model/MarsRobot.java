package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

/**
 * Mars robot model that can move in 2D Cartesian plane.
 * 
 * @author tbisko
 *
 */
public class MarsRobot implements Robot {

	private final PropertyChangeSupport propertySupport;
	
	/** 360 degrees on 2D Cartesian plane */
	private Integer direction;
	/** Coordinates in Cartesian coordinate system (x, y) */
	private Position position;

	/** Mapping of commands to their representing letter */
	private Map<Character, Command> commands = new HashMap<>();
	
	/** Private constructor used by inner builder class */
	private MarsRobot(MarsRobotBuilder builder) {
		this.propertySupport = new PropertyChangeSupport(this);
		this.direction = builder.direction;
		this.position = builder.position;
		
		builder.commandFactories.forEach((sign, factory) -> {
			this.commands.put(sign, factory.getCommand(this) );
		});
	}
	
	/**
	 * Method executes commands given as string of characters.
	 *  
	 * @param String commands non-null. Each character represents one command.
	 * <br />Legal commands: 'L' - turn left, 'R' - turn right, 'F' - move forward
	 * <br />Unknown commands will be ignored and logged to standard error output.
	 */
	@Override
	public void executeCommands(String commands) {
		if(commands == null) {
			throw new IllegalArgumentException();
		}
		
		commands.chars().forEach(c -> {
			if(this.commands.containsKey((char)c)) {
				Command command = this.commands.get((char)c);
				command.execute();
			} else {
				System.err.println("Unknown command: " + (char)c);
			}
		});
	}

	/**
	 * Moves robot by one unit in one of four direction NORTH, SOUTH, WEST, EAST.
	 * 
	 * If robot is not facing one of those directions, direction will be restarted to NORTH and robot wont move,<br />
	 * simulating bad wind that moved our robot out of proper direction on which it can move,<br />
	 * until version 2.0 in which robot will be able to move in any direction.
	 */
	@Override
	public Command moveForward(int movementUnits) {
		return () -> {
			double x = this.position.x + StrictMath.cos(StrictMath.toRadians(this.getDirection())) * movementUnits;
			double y = this.position.y + StrictMath.sin(StrictMath.toRadians(this.getDirection())) * movementUnits;
			this.setPosition(new Position(x, y));
			
			
		};
	}
	
	/**
	 * @param degrees, turns left for positive values, turns right for negative values
	 * @return Command that turns robot
	 */
	@Override
	public Command turn(int degrees) {
		if(degrees < 0) {
			/* Turn right, or clockwise */
			return () -> {
				int newDirection = (this.getDirection() + degrees) % 360;
				if(newDirection < 0) {
					newDirection = 360 + newDirection;
				}
				this.setDirection(newDirection);
			};
		} else {
			/* Turn left, or counterclockwise */
			return () -> this.setDirection(this.getDirection() + degrees);
		}
	}
	
	/**
	 * 
	 * @param direction angle on Cartesian plane, 0 - 359 degrees, 
	 * <br /> - values less than zero are illegal
	 * <br /> - if greater or equal to 360, modulo 360 operation is applied
	 */
	public void setDirection(Integer direction) {
		if(direction < 0) {
			throw new IllegalArgumentException();
		}
		
		int oldValue = this.direction;
		this.direction = direction % 360;
		int newValue = this.direction;
		propertySupport.firePropertyChange("direction", oldValue, newValue);
	}
	
	public Integer getDirection() {
		return direction;
	}
	
	public void setPosition(Position position) {
		Position oldValue = new Position(this.position.x, this.position.y);
		this.position = position;
		propertySupport.firePropertyChange("position", oldValue, this.getPosition());
	}

	public Position getPosition() {
		return position;
	}

	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
	
	public static class Position {
		double x;
		double y;
		
		Position(double x, double y){
			this.x = x;
			this.y = y;
		}
		
		public double getX() {
			return x;
		}

		public void setX(double x) {
			this.x = x;
		}

		public double getY() {
			return y;
		}

		public void setY(double y) {
			this.y = y;
		}
		
		@Override
		public String toString() {
			return "x: " + this.x + " y: " + this.y;
		}
	}
	/**
	 * 
	 * Inner builder class for MarsRobot
	 * 
	 * Setting initial direction, position and movement instructions.
	 * 
	 * @author tbisko
	 *
	 */
	public static class MarsRobotBuilder {
		
		private int direction;
		private Position position;
		Map<Character, CommandFactory> commandFactories = new HashMap<>();
		
		/**
		 * Sets default values for direction (NORTH, 90 degrees) and position (0, 0)
		 */
		public MarsRobotBuilder() {
			/* Default values */
			this.direction = DirectionEnum.NORTH.getDegrees();
			this.position = new Position(0.0d, 0.0d);
		}
		
		/**
		 * Set initial robot direction.
		 * @param int initialDirection
		 * @return MarsRobotBuilder
		 */
		public MarsRobotBuilder direction(int initialDirection) {
			this.direction = initialDirection;
			return this;
		}
		
		/**
		 * Set initial robot position.
		 * 
		 * @param int[] initialPosition
		 * @return MarsRobotBuilder
		 */
		public MarsRobotBuilder position(Position initialPosition) {
			this.position = initialPosition;
			return this;
		}
		
		/**
		 * CommandFactory factory = (MarsRobot robot) -> robot.turn(45);
		 * 
		 * @param commandCallSign
		 * @param factory
		 * @return
		 */
		public MarsRobotBuilder addCommand(Character commandCallSign, CommandFactory factory) {

			commandFactories.put(commandCallSign, factory);
			return this;
		}
		
		public MarsRobotBuilder addDefaultCommands() {
			CommandFactory turnLeftCommandfactory = (MarsRobot robot) -> robot.turn(90);
			CommandFactory turnRightCommandfactory = (MarsRobot robot) -> robot.turn(-90);
			CommandFactory moveForwardCommandfactory = (MarsRobot robot) -> robot.moveForward(1);
			
			commandFactories.put('L', turnLeftCommandfactory);
			commandFactories.put('R', turnRightCommandfactory);
			commandFactories.put('F', moveForwardCommandfactory);
			return this;
		}
		
		
		/**
		 * Creates new instance of MarsRobot.
		 * 
		 * @return MarsRobot
		 */
		public MarsRobot build() {
			return new MarsRobot(this);
		}
	}
}
