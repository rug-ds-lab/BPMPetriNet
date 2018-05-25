package nl.rug.ds.bpm.util.exception;

import nl.rug.ds.bpm.util.log.LogEvent;
import nl.rug.ds.bpm.util.log.Logger;

/**
 * Created by Heerko Groefsema on 25-May-18.
 */
public class MalformedNetException extends Exception {
	public MalformedNetException(String message) {
		super(message);
		Logger.log(message, LogEvent.ERROR);
	}
}
