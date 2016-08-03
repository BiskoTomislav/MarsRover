package model;

/**
 * Four basic directions in Cartesian plane,
 * <br />matched with their respective degrees values.
 * 
 * @author tbisko
 *
 */
public 	enum DirectionEnum {
	EAST(0),
	NORTH(90),
	SOUTH(270),
	WEST(180);
	
	private int degrees;
	
	DirectionEnum(int degrees) {
		this.degrees = degrees;
	}

	public int getDegrees() {
		return degrees;
	}

	@Override
	public String toString() {
		String values = "";
		
		for(DirectionEnum value : DirectionEnum.values()) {
			values += value.name() + " (" + value.getDegrees() + " degrees) ";
		}
		
		return values;
	}
	
	
}
