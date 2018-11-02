package nl.rug.ds.bpm.util.comparator;

import nl.rug.ds.bpm.petrinet.interfaces.marking.M;

import java.util.Comparator;

public class MarkingComparator implements Comparator<M>
{
	@Override
	public int compare(M a, M b) {
		return a.compareTo(b);
	}
}
