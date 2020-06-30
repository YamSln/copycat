package local.copycat.controlpanel;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import local.copycat.service.BackupListener;
import local.copycat.service.BackupService;
import local.copycat.service.FileService;
import local.copycat.service.Messages;
import local.copycat.service.SleepTimes;

/**
 * Controls the control panel UI rendered according to the FXML and CSS files
 * 
 * @author YAM
 *
 */
public class ControlPanelController implements Initializable, BackupListener
{
	// Backup time options
	private static final String THREE_HOURS = "03:00 Hours";
	private static final String TWO_HOURS = "02:00 Hours";
	private static final String ONE_HOUR = "01:00 Hour";
	private static final String HALF_AN_HOUR = "00:30 Minutes";
	private static final String[] TIME_OPTIONS = {THREE_HOURS, TWO_HOURS, ONE_HOUR, HALF_AN_HOUR};
	private static ObservableList<String> timeOptions = FXCollections.observableArrayList();
	private static String selectedTimeOption;
	
	private static final String ICON_RESOURCE = "/Images/Tray_Icon.png";
	private static Image icon = new Image(ICON_RESOURCE);
	
	// Control panel stage
	private static Stage stage;
	
	// Control panel display status
	private static boolean displaying;
	
	// File and Backup services used to perform the actual backup process
	private static FileService fileService = new FileService();
	private static BackupService backupService = new BackupService();
	
	// Window is used to display the windows file chooser when browsing for source and destination directories
	private Window window;
	
	// Current backup process status
	private static boolean backuping;
	
	// Weather the control panel is displayed or not
	private static boolean running;
	
	@FXML
	private Button sourceBrowseButton;
	
	@FXML
	private Button destinationBrowseButton;
	
	@FXML
	private Button performBackupButton;
	
	@FXML
	private Button stopBackupButton;
	
	@FXML
	private TextField sourcePathTextField;
	
	@FXML
	private TextField destPathTextField;
	
	@FXML
	private ComboBox<String> timeComboBox;
	
	@FXML
	private javafx.scene.control.MenuItem closeMenuItem;
	
	@FXML
	private javafx.scene.control.MenuItem exitMenuItem;
	
	@FXML
	private javafx.scene.control.MenuItem aboutMenuItem;
	
	/**
	 * Handles the onAction event of the source browse button
	 */
	@FXML
	private void handleSourceBrowseButton()
	{
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select source folder");
		fileService.setSource(directoryChooser.showDialog(window));
		
		if(fileService.getSource() != null)
			sourcePathTextField.setText(fileService.getSource().getAbsolutePath());
	}
	
	/**
	 * Handles the onAction event of the destination browse button
	 */
	@FXML
	private void handleDestinationBrowseButton()
	{
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select destination folder");
		fileService.setDest(directoryChooser.showDialog(window));
		
		if(fileService.getDest() != null)
			destPathTextField.setText(fileService.getDest().getAbsolutePath());	
	}
	
	/**
	 * Handles the onAction event of the backup start button
	 */
	@FXML
	private void handlePerformBackupButton()
	{
		if(fileService.getSource() != null && fileService.getSource().exists())
		{
			if(fileService.getDest() != null && fileService.getSource().exists())
			{
				if(fileService.getTime() > 0)
				{
					setStartedButtons();
					handleBackupStart();
					Messages.information("Backup has been started", "Backup started");
				}
				else
					Messages.error("Set backup timing to start backup", "No timing specification found");
			}
			else
				Messages.error("Set destination path to start backup", "No destination found");
		}
		else
			Messages.error("Set source path to start backup", "No source found");
	}
	
	/**
	 * Handles the onAction event of the backup stop button
	 */
	@FXML
	private void handleStopBackupButton()
	{
		setStoppedButtons();
		handlebackupStop();
	}
	
	/**
	 * Handles the time option combo box selection event
	 * 
	 * @param event the event received upon selection
	 */
	@FXML
	private void handleComboAction(ActionEvent event)
	{
		selectedTimeOption = timeComboBox.getValue();
		
		switch(selectedTimeOption)
		{
			case THREE_HOURS:
				fileService.setTime(SleepTimes.THREE_HOURS.getSleepTime());
				break;
			case TWO_HOURS:
				fileService.setTime(SleepTimes.TWO_HOURS.getSleepTime());
				break;
			case ONE_HOUR:
				fileService.setTime(SleepTimes.ONE_HOUR.getSleepTime());
				break;
			case HALF_AN_HOUR:
				fileService.setTime(SleepTimes.HALF_AN_HOUR.getSleepTime());
				break;
		}
		
		System.out.println(selectedTimeOption);
	}
	
	/**
	 * Exits the system
	 */
	@FXML
	private void exitSystem()
	{
		handleSystemExit();
	}
	
	/**
	 * Closes the control panel
	 */
	@FXML
	private void handleControlPanelClose()
	{
		closeControlPanel();
	}
	
	/**
	 * Displays about message
	 */
	@FXML
	private void displayAboutMessage()
	{
		Messages.information("copycat 2020 1.0\n all rights reserved", "About");
	}
	
	/**
	 * Runs upon control panel display initialization
	 * performs display initializations according to previous user selection
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) 
	{
		running = true;
		
		if(timeOptions.size() == 0)
			timeOptions.addAll(TIME_OPTIONS);
		timeComboBox.setItems(timeOptions);
		
		if(backuping)
			setStartedButtons();		
		else
			setStoppedButtons();
		
		addBackupListeners(this);
		
		if(fileService.getSource() != null)
			sourcePathTextField.setText(fileService.getSource().getAbsolutePath());
		
		if(fileService.getDest() != null)
			destPathTextField.setText(fileService.getDest().getAbsolutePath());
		
		initSleepTimeSelection();
	}
	
	/**
	 * Initiates the time options combo box selected time display
	 * according to previous user selection
	 */
	private void initSleepTimeSelection()
	{
		long sleepTime = fileService.getTime();
		
		if(sleepTime != 0)
		{		
			if(sleepTime == SleepTimes.THREE_HOURS.getSleepTime())
				timeComboBox.getSelectionModel().select(THREE_HOURS);
			else if(sleepTime == SleepTimes.TWO_HOURS.getSleepTime())
				timeComboBox.getSelectionModel().select(TWO_HOURS);
			else if(sleepTime == SleepTimes.ONE_HOUR.getSleepTime())
				timeComboBox.getSelectionModel().select(ONE_HOUR);
			else if(sleepTime == SleepTimes.HALF_AN_HOUR.getSleepTime())
				timeComboBox.getSelectionModel().select(HALF_AN_HOUR);
		}
	}
	
	/**
	 * Starts the backup process
	 */
	public static void handleBackupStart()
	{
		backupService.setSleepTime(fileService.getTime());
		
		if(backupService.startService(fileService.getSource(), fileService.getDest()))
			backuping = true;
	}
	
	/**
	 * Stops the backup process
	 */
	public static void handlebackupStop()
	{
		backupService.stopService();
		backuping = false;
	}
	
	/**
	 * Returns the current backup status
	 * 
	 * @return the current backup status
	 */
	public static boolean isBackuping()
	{
		return backuping;
	}
	
	/**
	 * Sets new backup status
	 * 
	 * @param isBackuping new backup status to set
	 */
	public static void setBackuping(boolean isBackuping)
	{
		backuping = isBackuping;
	}
	
	/**
	 * Sets the current running status of the control panel display
	 * 
	 * @param isRunning running status to set
	 */
	public static void setRunning(boolean isRunning)
	{
		running = isRunning;
	}
	
	/**
	 * Returns the current running status of the control panel display
	 * 
	 * @return the current running status of the control panel display
	 */
	public static boolean isRunning()
	{
		return running;
	}
	
	/**
	 * Sets buttons to stopped state status
	 */
	private void setStoppedButtons()
	{
		performBackupButton.setDisable(false);
		stopBackupButton.setDisable(true);
	}
	
	/**
	 * Set buttons to active state status
	 */
	private void setStartedButtons()
	{
		performBackupButton.setDisable(true);
		stopBackupButton.setDisable(false);
	}

	@Override
	public void statusChanged(boolean isRunning) 
	{
		if(isRunning)
		{
			backuping = true;
			setStartedButtons();
		}
		else
		{
			backuping = false;
			setStoppedButtons();
		}
		
	}
	
	/**
	 * Handles the system exit alert and process
	 */
	public static void handleSystemExit()
	{	
		Platform.runLater(new Runnable() 
		{
			
			@Override
			public void run() 
			{
				Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to exit copycat?"
				+ System.lineSeparator()
				+ "all backup processes will be stopped", ButtonType.YES, ButtonType.NO);
				alert.setHeaderText("Are you sure?");
				alert.setTitle("Exit system");
				Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
				stage.getIcons().add(icon);
				alert.showAndWait();		
				if(alert.getResult() == ButtonType.YES)
					System.exit(0);
			}
			
		});
	}
	
	/**
	 * Displays the control panel upon clicking it in the tray icon menu
	 * 
	 * @throws IOException
	 */
	public static void displayControlPanel() throws IOException
	{
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() 
			{
				if(!displaying)
				{
				    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("controlPanelUI.fxml"));
				    Parent root1 = null;
					try 
					{
						root1 = (Parent) fxmlLoader.load();
					} 
					catch (IOException e) 
					{
						Messages.uexError();
						e.printStackTrace();
					}
				    stage = new Stage();
				    stage.setResizable(false);
				    stage.setTitle("Copycat Control Panel");
				    stage.setScene(new Scene(root1));  
				    stage.getIcons().add(icon);
				    displaying = true;
				    stage.getScene().getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST
				    		, new EventHandler<Event>() {

						@Override
						public void handle(Event arg0) {
							displaying = false;						
						}
				    	
					});
				    stage.show();
				}
			}
		});
	}
	
	/**
	 * Closes the control panel
	 */
	public static void closeControlPanel()
	{
		stage.close();
		stage = null;
		displaying = false;
	}
	
	public static void addBackupListeners(BackupListener backupListeners)
	{
		backupService.addListener(backupListeners);
	}
	
	/**
	 * Return {@code true} if the control panel is displaying, otherwise returns {@code false}
	 * 
	 * @return true if the control panel is displaying, otherwise returns false
	 */
	public static boolean isDisplaying()
	{
		return displaying;
	}
	
	/**
	 * Returns the current stage of the control panel
	 * 
	 * @return the current stage of the control panel
	 */
	public static Stage getStage()
	{
		return stage;
	}
	
}