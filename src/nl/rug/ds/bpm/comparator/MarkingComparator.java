package nl.rug.ds.bpm.comparator;

import nl.rug.ds.bpm.net.marking.M;

import java.util.Comparator;

public class MarkingComparator implements Comparator<M>
{
	@Override
	public int compare(M a, M b) {
		return a.compareTo(b);
	}
}
