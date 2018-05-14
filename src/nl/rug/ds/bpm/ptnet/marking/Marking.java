package nl.rug.ds.bpm.ptnet.marking;

import nl.rug.ds.bpm.comparator.StringComparator;
import nl.rug.ds.bpm.net.marking.ConditionalM;

import java.util.*;

/**
 * Created by Nick van Beest 26-Apr-17.
 */
public class Marking implements ConditionalM {
	private SortedMap<String, Integer> tokenmap;
	private Set<String> conditions;
	
	public Marking() {
		tokenmap = new TreeMap<String, Integer>(new StringComparator());
		conditions = new HashSet<>();
	}
	
	public void addTokens(String placeId, int tokens) {
		if (tokens > 0) {
			if (!tokenmap.containsKey(placeId)) {
				tokenmap.put(placeId, tokens);
			}
			else {
				tokens += tokenmap.get(placeId);
				if (tokens > maximumTokensAtPlaces) tokens = maximumTokensAtPlaces;
				tokenmap.put(placeId, tokens);
			}
		}
	}
	
	public void addTokens(Set<String> placeIds, int tokens) {
		for (String placeId: placeIds) {
			addTokens(placeId, tokens);
		}
	}
	
	public void emptyPlace(String placeId) {
		if (tokenmap.containsKey(placeId)) tokenmap.remove(placeId);
	}
	
	public Set<String> getMarkedPlaces() {
		return (tokenmap.keySet());
	}
	
	public Boolean hasTokens(String placeId) {
		return (tokenmap.containsKey(placeId));
	}
	
	public int getTokensAtPlace(String placeId) {
		return (tokenmap.containsKey(placeId) ? tokenmap.get(placeId) : 0);
	}
	
	public void consumeToken(String placeId) {
		if (hasTokens(placeId)) {
			int tokens = tokenmap.get(placeId);
			
			if (tokens == 1) {
				emptyPlace(placeId);
			}
			else {
				tokenmap.put(placeId, tokens - 1);
			}
		}
	}

	public void consumeTokens(String placeId, int amount) {
		if (hasTokens(placeId)) {
			int tokens = tokenmap.get(placeId);

			if (tokens - amount >= 0) {
				if (tokens - amount == 0) {
					emptyPlace(placeId);
				} else {
					tokenmap.put(placeId, tokens - amount);
				}
			}
		}
	}
	
	public void consumeTokens(Set<String> placeIds) {
		for (String placeId: placeIds) {
			consumeToken(placeId);
		}
	}
	
	public void copyFromMarking(Marking m) {
		tokenmap = new TreeMap<String, Integer>();
		
		for (String placeId: m.getMarkedPlaces()) {
			tokenmap.put(placeId, m.getTokensAtPlace(placeId));
		}
	}
	
	@Override
	public int hashCode() {
		return tokenmap.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
	    if (obj == null) return false;
	    if (getClass() != obj.getClass()) return false;
	    
	    return (this.hashCode() == obj.hashCode());
	}
	
	public void addCondition(String condition) {
		conditions.add(condition);
	}
	
	public void removeCondition(String condition) {
		conditions.remove(condition);
	}
	
	public void setConditions(Set<String> conditions) {
		this.conditions = conditions;
	}
	
	@Override
	public Collection<String> getConditions() {
		return conditions;
	}
	
	@Override
	public String toString() {
		String s = "";

		Iterator<String> p = tokenmap.keySet().iterator();
		String placeId;

		while(p.hasNext()) {
			placeId = p.next();
			s = s + "+" + tokenmap.get(placeId) + placeId;
		}
		return (s.length() > 0 ? s.substring(1) : "");
	}
	
	public Marking clone() {
		Marking marking = new Marking();
		marking.copyFromMarking(this);
		marking.setConditions(new HashSet<>(conditions));
		return marking;
	}
}
