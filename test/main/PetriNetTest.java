package main;

import nl.rug.ds.bpm.pnml.marshaller.PTNetMarshaller;
import nl.rug.ds.bpm.pnml.marshaller.PTNetUnmarshaller;
import nl.rug.ds.bpm.ptnet.PlaceTransitionNet;
import nl.rug.ds.bpm.ptnet.element.Arc;
import nl.rug.ds.bpm.ptnet.element.Place;
import nl.rug.ds.bpm.ptnet.element.Transition;
import nl.rug.ds.bpm.ptnet.marking.DataMarking;

import java.io.File;
import java.util.Set;

/**
 * Created by Nick van Beest on 4 May 2018
 *
 */
public class PetriNetTest {

	public static void main(String[] args) {
		PTNetUnmarshaller pnu = new PTNetUnmarshaller(new File(args[0]));

		Set<PlaceTransitionNet> pnset = pnu.getNets();
		
		if (pnset.isEmpty())
			System.out.println("empty");
		else {
			//get first net
			PlaceTransitionNet pn = pnset.iterator().next();
			
			for (Transition t: pn.getTransitions()) {
				System.out.println(t.getName());
			}
			
			pn.addVariable("i", "int", "0.0");
			
			Place p = pn.addPlace("x0", "test_x0", 2);
			Transition t = pn.addTransition("y0");
			Place p1 = pn.addPlace("x1");
			
			Arc a = pn.addArc("x0", "y0", 2);
			Arc a1 = pn.addArc(t, p1);
			
			t.setScript("i=i+1;", "JavaScript");
			t.setGuard("i>=0");
			
			DataMarking m = pn.getInitialDataMarking();
			m.addBindingTracking("i");

			System.out.println("Is 1-safe: " + pn.isNSafe(1));
			System.out.println("x0 is source: " + pn.isSource("x0"));
			System.out.println("Initial marking: " + m.toString());
			System.out.println("Binding i: " + m.trackedToString().iterator().next());
			
			System.out.println("y0 is enabled: " + pn.isEnabled(pn.getTransition("y0"), m));
			
			DataMarking m2 = pn.fire(pn.getTransition("y0"), m);
			System.out.println("fired y0: " + m2.toString());
			System.out.println("Binding i: " + m2.trackedToString().iterator().next());

			pn.setInitialDataMarking(m2);
			
			System.out.print("Enabled: ");
			for (Transition transition: pn.getEnabled(m2))
				System.out.print(transition.getId() + " ");
			System.out.println("");
			
		}
		PTNetMarshaller pnm = new PTNetMarshaller(pnset, new File(args[0]+".out"));
	}
}
