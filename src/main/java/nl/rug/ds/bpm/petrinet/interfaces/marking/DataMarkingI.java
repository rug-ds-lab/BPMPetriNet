package nl.rug.ds.bpm.petrinet.interfaces.marking;

import javax.script.Bindings;
import java.util.Map;

/**
 * Created by Heerko Groefsema on 14-May-18.
 */
public interface DataMarkingI extends MarkingI {
	Map<String, String> getBindings();
	void setBinding(String key, String value);
	void setBindings(Map<String, String> bindings);
}
