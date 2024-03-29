package local.copycat.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Configures the source, destination and sleep time of the backup process
 * according to user selection
 * 
 * @author YAM
 *
 */
public class FileService 
{
	// Configuration properties
	private Properties properties = new java.util.Properties();
    private static final String SRC_KEY = "src";
    private static final String DEST_KEY = "dest";
    private static final String TIME_KEY = "time";
    
    // Source file, destination file, and sleep time attributes
	private ArrayList<File> sources;
	private File destination;
	private long time;
	
	// Configuration and logging attributes
	private static final String CONFIG_FILENAME = "backup.config";
	private File config = new File(CONFIG_FILENAME);
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	
	/**
	 * Upon initiation:
	 * 
	 * 1 - creates configuration file if not exists
	 * 2 - tries to load configuration properties if exists in the configuration file
	 */
	public FileService()
	{
		try
		{
			InputStream inputStream = new FileInputStream(createConfigFile());
			properties.load(inputStream);
			
			sources = new ArrayList<File>();
			
			if(properties.containsKey(SRC_KEY))
			{
				List<String> sourcePaths = Arrays.asList(properties.getProperty(SRC_KEY).substring(1, properties.getProperty(SRC_KEY).length() - 1).split(", "));
				
				for(String path : sourcePaths)
					sources.add(new File(path));
			}
			
			if(properties.containsKey(DEST_KEY))
				destination = new File(properties.getProperty(DEST_KEY));
			
			if(properties.containsKey(TIME_KEY))
				time = Long.parseLong(properties.getProperty(TIME_KEY));
				
		}
		catch(IOException e)
		{
			logger.log(Level.SEVERE, null, e);
		}
	}
	
	/**
	 * Creates a configuration file if not exists
	 * returns the configuration file even if exists
	 * 
	 * @return the configuration file even if exists
	 * @throws IOException
	 */
	private File createConfigFile() throws IOException
	{
		if(!config.exists())
			config.createNewFile();
		
		return config;
	}
	
	/**
	 * Saves the properties attribute to the configuration file
	 */
	private void savePropertiesToConfigFile()
	{
		try
		{
			OutputStream outputStream = new FileOutputStream(config);
			properties.store(outputStream, null);
		}
		catch(IOException e)
		{
			logger.log(Level.SEVERE, null, e);
		}
	}
	
	/**
	 * Sets the source file attribute and property
	 * 
	 * @param source source file to set
	 */
	public void addSource(File source)
	{
		if(source != null)
		{
			this.sources.add(source);
			properties.setProperty(SRC_KEY, sources.toString());
			savePropertiesToConfigFile();
		}
	}
	
	public void removeSource(File source)
	{
		if(source != null)
		{
			this.sources.remove(source);
			if(!properties.contains("\\"))
				properties.remove(SRC_KEY);
			else
				properties.setProperty(SRC_KEY, sources.toString());
			savePropertiesToConfigFile();

		}
	}
	
	/**
	 * Returns the current source file
	 * 
	 * @return the current source file
	 */
	public ArrayList<File> getSources()
	{
		return this.sources;
	}
	
	/**
	 * Sets the destination file attribute and property
	 * 
	 * @param destination the destination file to set
	 */
	public void setDest(File destination)
	{
		if(destination != null)
		{
			properties.setProperty(DEST_KEY, destination.getAbsolutePath());
			savePropertiesToConfigFile();
			this.destination = destination;
		}
	}
	
	/**
	 * Returns the current destination file
	 * 
	 * @return the current destination file
	 */
	public File getDest()
	{
		return this.destination;
	}
	
	/**
	 * Sets the sleep time attribute and property
	 * 
	 * @param time the sleep time to set
	 */
	public void setTime(long time)
	{
		properties.setProperty(TIME_KEY, String.valueOf(time));
		savePropertiesToConfigFile();
		this.time = time;
	}
	
	/**
	 * Returns the current sleep time
	 * 
	 * @return the current sleep time
	 */
	public long getTime()
	{
		return this.time;
	}
	
	public String allSourcesExists()
	{
		String sourceNotExists = null;
		
		for(File source : sources)
		{		
			if(!source.exists())
			{
				sourceNotExists = source.getAbsolutePath();
				break;
			}
				
		}
		
		return sourceNotExists;

	}
	
}