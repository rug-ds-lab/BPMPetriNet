package nl.rug.ds.bpm.util.interfaces.marking;

import java.util.Set;

/**
 * Created by Heerko Groefsema on 14-May-18.
 */
public interface M {
	int maximumTokensAtPlaces = 3;
	
	int getTokensAtPlace(String placeId);
	Set<String> getMarkedPlaces();
	
	int compareTo(M o);
	String toString();
	M clone();
}
