package nl.rug.ds.bpm.net.marking;

/**
 * Created by Heerko Groefsema on 14-May-18.
 */
public interface M {
	int maximumTokensAtPlaces = 3;
	
	String toString();
	M clone();
}
