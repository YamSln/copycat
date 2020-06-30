package local.copycat.controlpanel;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Client extends Application 
{
	// Port number for the server socket
	private static final int PORT = 9999;
	@SuppressWarnings("unused")
	private static ServerSocket socket;
	
	// Manages the system tray
	private TrayManager trayManager = new TrayManager();
	
	private static final Logger logger = Logger.getLogger(Client.class.getName());
	
	// Runs the main stage when application starts
	@Override
	public void start(Stage primaryStage) 
	{
		try 
		{
			checkIfRunning();
			ControlPanelController.handleBackupStart();
			trayManager.init(ControlPanelController.isBackuping());
			ControlPanelController.addBackupListeners(trayManager);
			Platform.setImplicitExit(false);
		} 
		catch(Exception e) 
		{
			e.printStackTrace();
			logger.log(Level.SEVERE, "Exception on application stage start", e);
		}
		
	}
	
	/**
	 * Launches the application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * Checks weather the application is running
	 * by checking if its already listening to the same port
	 */
	private static void checkIfRunning() 
	{
		  try 
		  {
		    socket = new ServerSocket(PORT,0,InetAddress.getByAddress(new byte[] {127,0,0,1}));
		  }
		  catch (BindException e) 
		  {
		    System.exit(1);
		  }
		  catch (IOException e) 
		  {
		    e.printStackTrace();
		    System.exit(2);
		    logger.log(Level.SEVERE, "Exception on socket verifing", e);
		  }
		  
	}
	
}