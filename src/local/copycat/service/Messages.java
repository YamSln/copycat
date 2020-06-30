package local.copycat.service;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

/**
 * Display messages in an alert window
 * 
 * @author YAM
 *
 */
public class Messages 
{
	// System icon
	private static final String ICON_RESOURCE = "/Images/Tray_Icon.png";
	private static final javafx.scene.image.Image icon = new javafx.scene.image.Image(ICON_RESOURCE);
	
	/**
	 * Instantiation of this class is not allowed
	 */
	private Messages()
	{
		
	}
	
	/**
	 * Displays an unexpected error message
	 */
	public static void uexError()
	{
		Alert error = new Alert(AlertType.ERROR);
		error.setContentText("An unexpected error occured");
		error.setHeaderText("Error code: x001");
		setIcon(error);
		error.show();
	}
	
	/**
	 * Display a connection error message
	 */
	public static void connectionError()
	{
		Alert error = new Alert(AlertType.ERROR);
		error.setContentText("A connection issue occurred\n"
				+ "the action could not be completed");
		error.setHeaderText("Error code: x002");
		setIcon(error);
		error.show();
	}
	
	/**
	 * Displays an error message that contains a given text and header text
	 * 
	 * @param errorText text that will be displayed in the error message body
	 * @param headerText text that will be displayed in the error message header
	 */
	public static void error(String errorText, String headerText)
	{
		Alert error = new Alert(AlertType.ERROR);
		error.setContentText(errorText);
		error.setHeaderText(headerText);
		setIcon(error);
		error.show();
	}
	
	/**
	 * Displays an information message that contains a given text and header text
	 * 
	 * @param informationMessage text that will be displayed in the information message body
	 * @param headerText text that will be displayed in the information message header
	 */
	public static void information(String informationMessage, String headerText)
	{
		Alert info = new Alert(AlertType.INFORMATION);
		info.setContentText(informationMessage);
		info.setHeaderText(headerText);
		setIcon(info);
		info.show();
	}
	
	/**
	 * Sets the system icon to a given alert window
	 * 
	 * @param alert the alert window to set the system icon to
	 */
	private static void setIcon(Alert alert)
	{
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.getIcons().add(icon);
	}
	
}