package nl.rug.ds.bpm.util.exception;

import nl.rug.ds.bpm.util.log.LogEvent;
import nl.rug.ds.bpm.util.log.Logger;

/**
 * Created by Heerko Groefsema on 28-May-18.
 */
public class IllegalMarkingException extends Exception {
	public IllegalMarkingException(String message) {
		super(message);
		Logger.log(message, LogEvent.ERROR);
	}
}
