package local.copycat.service;

/**
 * Defines the sleep times constant values (in milliseconds)
 * 
 * @author YAM
 *
 */
public enum SleepTimes
{
	THREE_HOURS(10800000), // Three hours sleep time
	TWO_HOURS(7200000), // Two hours sleep time
	ONE_HOUR(3600000), // One hours sleep time
	HALF_AN_HOUR(1800000); // 30 minutes sleep time
	
	//Sleep time attribute of the current instance
	private long sleepTime;
	
	/**
	 * Initiates new instance of a sleep time
	 * 
	 * @param sleepTime the sleep time to set to the current instances attribute
	 */
	SleepTimes(long sleepTime) 
	{
		this.sleepTime = sleepTime;
	}
	
	/**
	 * Returns the current sleep time
	 * 
	 * @return the current sleep time
	 */
	public long getSleepTime()
	{
		return this.sleepTime;
	}
	
}