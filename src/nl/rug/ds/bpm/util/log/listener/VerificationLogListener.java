package nl.rug.ds.bpm.util.log.listener;

import nl.rug.ds.bpm.util.log.LogEvent;

/**
 * Created by Heerko Groefsema on 10-Apr-17.
 */
public interface VerificationLogListener {
	void verificationLogEvent(LogEvent event);
}
