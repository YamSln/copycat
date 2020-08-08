package local.copycat.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import javafx.application.Platform;
import local.copycat.controlpanel.ControlPanelController;

/**
 * Performs backup service and controls the backup thread
 * 
 * @author YAM
 *
 */
public class BackupService 
{
	private ArrayList<BackupListener> backupListeners = new ArrayList<BackupListener>();
	
	private ArrayList<File> sources;
	private File destination;
	
	// The service thread of the backup service
	private static Thread serviceThread = null;
	
	// The sleep time of the service thread between each process
	private long sleepTime;
	
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * Copies a directory into another directory
	 * also adds the current connected user name to the end of the copies directory
	 * inside the destination directory
	 * 
	 * @param source the source directory to copy
	 * @param destionation the destination directory to copy into
	 * @return {@code true} if the directory copies successfully, otherwise returns {@code false}
	 */
	public boolean copyUserDirectory(File source, File destionation)
	{
		if(source.exists() && destionation.exists())
		{
			try
			{
				String directoryName = source.getName() + "_" + System.getProperty("user.name");
				FileUtils.copyDirectory(source, new File(destionation.getAbsolutePath() + "\\" + directoryName));
				return true;
			}
			catch(IOException e)
			{
				logger.log(Level.SEVERE, null, e);
				return false;
			}
		}

		return false;
	}
	
	/**
	 * Copies a file into a directory
	 * 
	 * @param source the file to copy
	 * @param destionation the directory to copy the file into
	 * @return {@code true} if the file was copied successfully, otherwise returns {@code false}
	 */
	public boolean copyFile(File source, File destionation)
	{
		try
		{
			Files.copy(source.toPath(), destionation.toPath(), StandardCopyOption.REPLACE_EXISTING);	
			return true;
		}
		catch(IOException e)
		{
			logger.log(Level.SEVERE, null, e);
			return false;
		}
		
	}
	
	/**
	 * Performs the backup service
	 * 
	 * @param source source file to copy
	 * @param destionation destination directory to copy into
	 * @return {@code true} if the service started successfully, otherwise returns {@code false}
	 */
	public boolean startService()
	{
		if(this.sources != null && this.destination != null && sleepTime > 0)
		{
			if(this.destination.exists() && checkSources(sources))
			{
				serviceThread = new Thread(new Runnable() 
				{
					
					@Override
					public void run() 
					{
						try
						{	
							while(!Thread.currentThread().isInterrupted())
							{
								for(File source : sources)
								{
									if(copyUserDirectory(source, destination))
										threadStatusChanged(true);
									else
									{
										Platform.runLater(new Runnable() {
											
											@Override
											public void run() {
												Platform.setImplicitExit(false);
												threadStatusChanged(false);
												ControlPanelController.setBackuping(false);
												if(!destination.exists())
													Messages.error("Destination could not be found", "File error");
												else
													Messages.error("Source: " + source.getAbsolutePath() + " does not exist", "File error");
												stopService();
											}
										});
										
										break;
									}
								
								}
								
								System.out.println(sleepTime);			
								Thread.sleep(sleepTime);
							}
						}
						catch(InterruptedException e)
						{
							Platform.runLater(new Runnable() {
								
								@Override
								public void run() {
									threadStatusChanged(false);
									Messages.information("Backup has been stopped", "Backup stopped");
								}
							});
							
						}
						
					}
				});
				
				serviceThread.start();
				return true;
			}
			
			return false;
			
		}
		
		return false;
	}
	
	/**
	 * Stops the backup service
	 */
	public void stopService()
	{
		serviceThread.interrupt();
		serviceThread = null;
	}
	
	public ArrayList<File> getSources() 
	{
		return this.sources;
	}

	public void setSources(ArrayList<File> sources) 
	{
		this.sources = sources;
	}

	public File getDestination() 
	{
		return this.destination;
	}

	public void setDestination(File destination) 
	{
		this.destination = destination;
	}
	
	public void addSource(File source)
	{
		this.sources.add(source);
	}
	
	public boolean removeSource(File source)
	{
		return this.sources.remove(source);
	}
	
	/**
	 * Sets the sleep time of the service thread
	 * 
	 * @param sleepTimeMillis sleep time to set to the the service thread
	 */
	public void setSleepTime(long sleepTimeMillis)
	{
		this.sleepTime = sleepTimeMillis;
	}
	
	/**
	 * Returns the current sleep time of the service thread
	 * 
	 * @return the current sleep time of the service thread
	 */
	public long getSleepTime()
	{
		return this.sleepTime;
	}
	
	public void addListener(BackupListener backupListener)
	{
		backupListeners.add(backupListener);
	}
	
	private void threadStatusChanged(boolean threadIsRunning)
	{
		for(BackupListener bl : backupListeners)
			bl.statusChanged(threadIsRunning);
	}
	
	private boolean checkSources(ArrayList<File> sources)
	{
		if(sources.isEmpty())
			return false;
		
		for(File source : sources)
		{
			if(!source.exists())
				return false;
		}

		return true;
	}
	
	
}