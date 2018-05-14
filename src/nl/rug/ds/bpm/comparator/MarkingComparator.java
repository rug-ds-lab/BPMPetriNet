package nl.rug.ds.bpm.comparator;

import java.util.Comparator;

import nl.rug.ds.bpm.ptnet.marking.Marking;

public class MarkingComparator implements Comparator<Marking>
{
	@Override
	public int compare(Marking a, Marking b) {
		
		return a.toString().compareTo(b.toString());
	}
}
