package nl.rug.ds.bpm.util.comparator;

import nl.rug.ds.bpm.petrinet.interfaces.marking.MarkingI;

import java.util.Comparator;

public class MarkingComparator implements Comparator<MarkingI>
{
	@Override
	public int compare(MarkingI a, MarkingI b) {
		return a.compareTo(b);
	}
}
