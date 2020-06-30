package local.copycat.controlpanel;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import javafx.application.Platform;
import local.copycat.service.BackupListener;
import local.copycat.service.Messages;

public class TrayManager implements BackupListener
{
	// Icons images source and loading
	private static final String ACTIVE_ICON_RESOURCE = "/Images/Tray_Icon_Active.png";
	private static final String DISABLED_ICON_RESOURCE = "/Images/Tray_Icon_Disabled.png";
	private static final Image activeIcon = loadImage(ACTIVE_ICON_RESOURCE);
	private static final Image disabledIcon = loadImage(DISABLED_ICON_RESOURCE);
	
	// Icon display for the tray icon
	private static Image iconDisplay;
	
	// The tray icon displayed in the system tray
	private static TrayIcon trayIcon;
	
	private Logger logger = Logger.getLogger("Tray manager logger");
	
	/**
	 * Initializes the system tray
	 * 
	 * @param isBackuping weather the backup process is started or not
	 */
	public void init(boolean isBackuping)
	{
		if(!SystemTray.isSupported())
		{
			Messages.error("System tray is not supported", "System Tray Error");
			return;
		}
		
		if(ControlPanelController.isBackuping())
			iconDisplay = activeIcon;
		else
			iconDisplay = disabledIcon;
		
		SystemTray tray = SystemTray.getSystemTray();
		
		PopupMenu trayPopup = new PopupMenu();
		MenuItem controlPanel = new MenuItem("Control Panel");
		controlPanel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(java.awt.event.ActionEvent event) {
				try
				{
					ControlPanelController.displayControlPanel();
				}
				catch(IOException e)
				{
					Messages.uexError();
					logger.log(Level.SEVERE, "Exception on tray menu listener", e);
				}
				
			}
		});
		
		MenuItem exit = new MenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(java.awt.event.ActionEvent event) {
				ControlPanelController.handleSystemExit();			
			}
		});
		trayPopup.add(controlPanel);
		trayPopup.addSeparator();
		trayPopup.add(exit);
		
		trayIcon = new TrayIcon(iconDisplay, "copycat", trayPopup);
		trayIcon.setImageAutoSize(true);
		trayIcon.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mousePressed(MouseEvent e) {
				
				Platform.runLater(new Runnable() {
					
					@Override
					public void run() {
						if(SwingUtilities.isLeftMouseButton(e))
							if(ControlPanelController.isDisplaying())
								ControlPanelController.getStage().toFront();
						
					}
				});

			}
		});
		
		try
		{
			tray.add(trayIcon);
		}
		catch(AWTException e)
		{
			logger.log(Level.SEVERE, "Exception on tray icon adding to system tray", e);
		}
	}
	
	/**
	 * Loads an image from a resource path
	 * 
	 * @param resource the image resource path
	 * @return the loaded image
	 */
	private static Image loadImage(String resource)
	{
		URL source = ControlPanelController.class.getResource(resource);
		Image loadedImage = Toolkit.getDefaultToolkit().getImage(source);
		
		return loadedImage;
	}

	@Override
	public void statusChanged(boolean isRunning) 
	{
		if(isRunning)
			trayIcon.setImage(activeIcon);
		else
			trayIcon.setImage(disabledIcon);
		
	}
}
