package nl.rug.ds.bpm.eventstructure;

/**
 * Created by Nick van Beest on 10 May 2018
 *
 */
public enum BehaviorRelation {
	// PRIME EVENT STRUCTURES
	CAUSALITY,
	INV_CAUSALITY,
	CONFLICT,
	CONCURRENCY,
	// ASYMMETRIC EVENT STRUCTURES
	ASYM_CONFLICT,
	INV_ASYM_CONFLICT
}
