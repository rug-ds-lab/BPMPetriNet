package main;

import nl.rug.ds.bpm.petrinet.interfaces.element.T;
import nl.rug.ds.bpm.petrinet.interfaces.marking.M;
import nl.rug.ds.bpm.petrinet.ptnet.PlaceTransitionNet;
import nl.rug.ds.bpm.petrinet.ptnet.element.Arc;
import nl.rug.ds.bpm.petrinet.ptnet.element.Place;
import nl.rug.ds.bpm.petrinet.ptnet.element.Transition;
import nl.rug.ds.bpm.petrinet.ptnet.marking.Marking;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.Net;
import nl.rug.ds.bpm.pnml.ptnet.marshaller.PTNetMarshaller;
import nl.rug.ds.bpm.pnml.ptnet.marshaller.PTNetUnmarshaller;
import nl.rug.ds.bpm.util.exception.MalformedNetException;

import java.io.File;
import java.util.Collection;
import java.util.Set;

/**
 * Created by Nick van Beest on 4 May 2018
 *
 */
public class PlaceTransitionNetTest {

	public static void main(String[] args) throws MalformedNetException {
		PTNetUnmarshaller pnu = new PTNetUnmarshaller(new File(args[0]));
		Set<Net> pnset = pnu.getNets();
		PlaceTransitionNet pn;
		
		if (pnset.isEmpty()) {
			System.out.println("empty");
			pn = new PlaceTransitionNet("t1");
			pnset.add((Net) pn.getXmlElement());
		}
		else {
			//get first interfaces
			pn = new PlaceTransitionNet(pnset.iterator().next());
			
			for (Transition t : pn.getTransitions()) {
				System.out.println(t.getName());
			}
		}
		if (pn.getTransition("y0") == null) {
			System.out.println("Adding test elements");

			pn.addVariable("i", "int", "0.0");

			Place p = pn.addPlace("x0", "test_x0", 2);
			Transition t = pn.addTransition("y0", "ytest");
			Place p1 = (Place) pn.addPlace("x1");

			Arc a = pn.addArc("x0", "y0", 2);
			Arc a1 = pn.addArc(t, p1);

			t.setScript("i=i+1;", "JavaScript");
			t.setGuard("i>=0");
			
			PlaceTransitionNet page = new PlaceTransitionNet(pn.addPage("page1"));
			
			page.addPlace("place1");
		}
		
		
		Marking m = pn.getInitialMarking();
		
		System.out.println("x0 is source: " + pn.isSource("x0"));
		System.out.println("Initial marking: " + m.toString());
		
		System.out.println("y0 is enabled: " + pn.isEnabled(pn.getTransition("y0"), m));
		
		M m2 = pn.fire(pn.getTransition("y0"), m);
		System.out.println("fired y0: " + m2.toString());

		pn.setInitialMarking(m2);
		
		Collection<Transition> en = (Collection<Transition>) pn.getEnabledTransitions(m2);
		while (!en.isEmpty()) {
			System.out.print("Enabled: ");
			for (T transition : en)
				System.out.print(transition.getId() + " ");
			System.out.println("");
			
			T t = en.iterator().next();
			m2 = pn.fire(t, m2);
			System.out.println("fired " + t.getId() + ": " + m2.toString());
			en = (Collection<Transition>) pn.getEnabledTransitions(m2);
		}
		PTNetMarshaller pnm = new PTNetMarshaller(pnset, new File(args[0]+".out"));
	}
}
