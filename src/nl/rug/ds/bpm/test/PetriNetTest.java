package nl.rug.ds.bpm.test;

import java.io.File;
import java.util.Set;

import nl.rug.ds.bpm.petrinet.PetriNet;
import nl.rug.ds.bpm.petrinet.element.Transition;
import nl.rug.ds.bpm.pnml.marshaller.PTNetUnmarshaller;

/**
 * Created by Nick van Beest on 4 May 2018
 *
 */
public class PetriNetTest {

	public static void main(String[] args) {
		PTNetUnmarshaller pnu = new PTNetUnmarshaller(new File(args[0]));

		Set<PetriNet> pnset = pnu.getNets();
		
		if (pnset.isEmpty())
			System.out.println("empty");
		
		for (PetriNet pn: pnset) {
			for (Transition t: pn.getTransitions()) {
				System.out.println(t.getName());
			}
		}
	}

}
