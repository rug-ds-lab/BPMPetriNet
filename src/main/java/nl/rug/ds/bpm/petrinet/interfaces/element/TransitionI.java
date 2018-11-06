package nl.rug.ds.bpm.petrinet.interfaces.element;

/**
 * Created by Heerko Groefsema on 14-May-18.
 */
public interface TransitionI extends NodeI {
	boolean isTau();
	void setTau(boolean tau);
}
