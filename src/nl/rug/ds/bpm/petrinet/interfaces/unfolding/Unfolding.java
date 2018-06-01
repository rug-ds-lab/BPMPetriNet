package nl.rug.ds.bpm.petrinet.interfaces.unfolding;

import java.util.BitSet;
import java.util.List;

/**
 * Created by Nick van Beest on 1 Jun. 2018
 *
 */
public interface Unfolding {
	public int getInitial();
	public int getSink();
	
	public List<String> getLabels();
	public String getLabel(int event);
	public BitSet getInvisibleEvents();
	
	public BitSet getDirectSuccessors(int event);
	public BitSet getDirectPredecessors(int event);
	public BitSet getConcurrency(int event);
	public BitSet getDirectConflicts(int event);
	
	public BitSet getTransitiveSuccessors(int event);
	public BitSet getTransitivePredecessors(int event);
	public BitSet getConflicts(int event);
	
	public BitSet getCutoffEvents();
	public int getCorrespondingEvent(int cutoff);
}
