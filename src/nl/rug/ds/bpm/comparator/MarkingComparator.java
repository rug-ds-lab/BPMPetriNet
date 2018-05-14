package nl.rug.ds.bpm.comparator;

import java.util.Comparator;

import nl.rug.ds.bpm.net.marking.M;

public class MarkingComparator implements Comparator<M>
{
	@Override
	public int compare(M a, M b) {
		return a.toString().compareTo(b.toString());
	}
}
