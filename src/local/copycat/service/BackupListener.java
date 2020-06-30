package local.copycat.service;

/**
 * Used to notify the implementing classes about changes in the backup service status
 * 
 * @author YAM
 *
 */
public interface BackupListener 
{
	/**
	 * Notifies on status change
	 * 
	 * @param isRunning new status
	 */
	void statusChanged(boolean isRunning);
}