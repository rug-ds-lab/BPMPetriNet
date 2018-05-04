package nl.rug.ds.bpm.test;

import java.io.File;
import java.util.Set;

import nl.rug.ds.bpm.petrinet.marking.DataMarking;
import nl.rug.ds.bpm.petrinet.marking.Marking;
import nl.rug.ds.bpm.petrinet.PetriNet;
import nl.rug.ds.bpm.petrinet.element.Arc;
import nl.rug.ds.bpm.petrinet.element.Place;
import nl.rug.ds.bpm.petrinet.element.Transition;
import nl.rug.ds.bpm.pnml.marshaller.PTNetMarshaller;
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
			
			pn.addVariable("i", "int", "0.0");
			
			Place p = pn.addPlace("x0", "test_x0", 2);
			Transition t = pn.addTransition("y0");
			Arc a = pn.addArc("x0", "y0", 2);
			
			t.setScript("i++;", "JavaScript");
			
			DataMarking m = pn.getInitialDataMarking();
			m.addVariableTracking("i");
			
			System.out.println("x0 is source: " + pn.isSource("x0"));
			System.out.println("Initial marking: " + m.toString());
			System.out.println("y0 is enabled: " + pn.enabled(pn.getTransition("y0"), m));
			System.out.println("y0 guard satisfied: " + pn.satisfiesGuard(pn.getTransition("y0"), m));
			
			System.out.println("fired: " + pn.execute(pn.getTransition("y0"), m).toString());
		}
		PTNetMarshaller pnm = new PTNetMarshaller(pnset, new File(args[0]+".out"));
	}
	

}
