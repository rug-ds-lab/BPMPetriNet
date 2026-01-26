package nl.rug.ds.bpm.petrinet.interfaces.marking;

import java.util.Set;

/**
 * Created by Heerko Groefsema on 14-May-18.
 */
public interface MarkingI {
	int getTokensAtPlace(String placeId);
	Set<String> getMarkedPlaces();
	
	int compareTo(MarkingI o);
	String toString();
	MarkingI clone();
}
