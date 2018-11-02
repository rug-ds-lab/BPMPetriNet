package nl.rug.ds.bpm.util.comparator;

import java.util.Comparator;

import nl.rug.ds.bpm.util.pair.Pair;

public class PairComparator<T, S> implements Comparator<Pair<T, S>> {
	
	@Override
	public int compare(Pair<T, S> a, Pair<T, S> b) {
		return a.toString().compareTo(b.toString());
	}
}
