package controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.InputStream;
import java.util.Iterator;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import model.MarsRobot;
import model.MarsRobot.MarsRobotBuilder;
import model.MarsRobot.Position;
import view.Main;

public class ViewController {

	private static final int SCREEN_DIRECTION_OFFSET = 90;
	private static final int X_LINE_OFFSET = 300;
	private static final int Y_LINE_OFFSET = 215;

	MarsRobot curiosity;
	
	// Reference to the main application.
    private Main main;
    
    IntegerProperty directionObserver;
	
    @FXML
	private SplitPane splitPane;
    @FXML
	private Button forwardButton;
	@FXML
	private Button turnLeftButton;
	@FXML
	private Button turnRightButton;
	@FXML
	private Button execCommandsButton;
	@FXML
	TextField commandsInputText;
	@FXML
	ImageView rover;
	@FXML
	ImageView marsSurface;
	@FXML
	Text directionOnScreen;
	@FXML
	Text positionOnScreen;
	@FXML
	Text signalInfo;

	/**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public ViewController() {
    }
    
    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    	
    	MarsRobotBuilder robotBuilder = new MarsRobotBuilder();
    	robotBuilder.addDefaultCommands();
    	curiosity = robotBuilder.build();
    	
    	setObservers();
    	setButtons();
    	setArrowKeys();
    	
    	signalInfo.setFill(Color.GREEN);
		signalInfo.setText("SIGNAL STRENGHT GOOD!");
    }

	private void setObservers() {
		try {
			directionObserver = new JavaBeanIntegerPropertyBuilder().bean(curiosity).name("direction").build();
			directionObserver.addListener((obs, oldValue, newValue) -> {

				// Screen adjustments
				if (newValue.intValue() == 0 || newValue.intValue() == 180) {
					rover.setRotate(newValue.intValue() + SCREEN_DIRECTION_OFFSET);
				}
				if (newValue.intValue() == 90 || newValue.intValue() == 270) {
					rover.setRotate(newValue.intValue() - SCREEN_DIRECTION_OFFSET);
				}
				directionOnScreen.setText(newValue.intValue() + "°");
			});
			
			curiosity.addPropertyChangeListener(new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
					String property = propertyChangeEvent.getPropertyName();
					if ("position".equals(property)) {
						Position oldPosition = (Position) propertyChangeEvent.getOldValue();
						Position newPosition = (Position) propertyChangeEvent.getNewValue();
						
						rover.setX(newPosition.getX() * 10);
						rover.setY(newPosition.getY() * -10);
						
						drawLineBehindRobot(oldPosition, newPosition);
						positionOnScreen.setText("(" + Math.round(newPosition.getX()) + ", " + Math.round(newPosition.getY()) + ")");
						if (Math.round(newPosition.getX()) > 26 || Math.round(newPosition.getY()) > 18
								|| Math.round(newPosition.getX()) < -26 || Math.round(newPosition.getY()) < -18) {
							signalInfo.setFill(Color.RED);
							signalInfo.setText("WARNING! WEAK SIGNAL!");
						} else {
							signalInfo.setFill(Color.GREEN);
							signalInfo.setText("SIGNAL STRENGHT GOOD!");
						}
						if (Math.round(newPosition.getX()) > 28 || Math.round(newPosition.getY()) > 21
								|| Math.round(newPosition.getX()) < -28 || Math.round(newPosition.getY()) < -21) {
							ClassLoader classloader = Thread.currentThread().getContextClassLoader();
							InputStream is = classloader.getResourceAsStream("static.png");
							Image image = new Image(is);
							marsSurface.setImage(image);
							marsSurface.toFront();
							removeLines();

						} else {
							ClassLoader classloader = Thread.currentThread().getContextClassLoader();
							InputStream is = classloader.getResourceAsStream("mars.jpg");
							Image image = new Image(is);
							marsSurface.setImage(image);
							marsSurface.toBack();
						}
					}
				}
			});
			
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public void setArrowKeys() {
		splitPane.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
		    @Override
		    public void handle(KeyEvent  keyEvent ) {
		    	if(keyEvent.getCode() == KeyCode.W) {
		    		forwardButton.fire();
		    	}
		    	if(keyEvent.getCode() == KeyCode.A) {
		    		turnLeftButton.fire();
		    	}
		    	if(keyEvent.getCode() == KeyCode.D) {
		    		turnRightButton.fire();
		    	}
		    }
		});
	}

	private void setButtons() {
		forwardButton.setOnAction(event -> {
			curiosity.executeCommands("F");

		});
		turnLeftButton.setOnAction(event -> {
			curiosity.executeCommands("L");
		});
		turnRightButton.setOnAction(event -> {
			curiosity.executeCommands("R");
			
		});
		execCommandsButton.setOnAction(event -> {
			curiosity.executeCommands(commandsInputText.getText());
		});
	}
	
	private void drawLineBehindRobot(Position oldPosition, Position newPosition) {
		Line line = new Line();
		line.setStartX(	oldPosition.getX() *  10 + X_LINE_OFFSET);
		line.setStartY(	oldPosition.getY() * -10 + Y_LINE_OFFSET);
		line.setEndX(	newPosition.getX() *  10 + X_LINE_OFFSET);
		line.setEndY(	newPosition.getY() * -10 + Y_LINE_OFFSET);
		line.setFill(Color.GREEN);
		line.setStrokeWidth(3);

		((AnchorPane)(main.getPrimaryStage().getScene().getRoot())).getChildren().add(line);
	}
	
	private void removeLines() {
		Iterator<Node> i = ((AnchorPane)(main.getPrimaryStage().getScene().getRoot())).getChildren().iterator();
		
		while(i.hasNext()) {
			if(i.next() instanceof Line) {
				i.remove();
			}
		}
	}
	public Main getMain() {
		return main;
	}

	public void setMain(Main main) {
		this.main = main;
	}

	public Button getForwardButton() {
		return forwardButton;
	}

	public void setForwardButton(Button forwardButton) {
		this.forwardButton = forwardButton;
	}

	public Button getTurnLeftButton() {
		return turnLeftButton;
	}

	public void setTurnLeftButton(Button turnLeftButton) {
		this.turnLeftButton = turnLeftButton;
	}

	public Button getTurnRightButton() {
		return turnRightButton;
	}

	public void setTurnRightButton(Button turnRightButton) {
		this.turnRightButton = turnRightButton;
	}

	public Button getExecCommandsButton() {
		return execCommandsButton;
	}

	public void setExecCommandsButton(Button execCommandsButton) {
		this.execCommandsButton = execCommandsButton;
	}
	public TextField getCommandsInputText() {
		return commandsInputText;
	}

	public void setCommandsInputText(TextField commandsInputText) {
		this.commandsInputText = commandsInputText;
	}

	public ImageView getRover() {
		return rover;
	}

	public void setRover(ImageView rover) {
		this.rover = rover;
	}
}
