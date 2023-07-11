package nl.rug.ds.bpm.test;

import nl.rug.ds.bpm.petrinet.interfaces.element.TransitionI;
import nl.rug.ds.bpm.petrinet.interfaces.marking.MarkingI;
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
import java.util.HashSet;
import java.util.Set;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Nick van Beest on 4 May 2018
 *
 */
public class PlaceTransitionNetTest {

	public static void main(String[] args) throws MalformedNetException {
		File file = new File(args[0]);
		Set<Net> pnset = new HashSet<Net>();

		if (file.exists()) {
			try {
				PTNetUnmarshaller pnu = new PTNetUnmarshaller(file);
				pnset = pnu.getNets();
			} catch (MalformedNetException e) {
				System.out.println("File is not a PNML file.");
			}
		}
		else
			System.out.println("No such file.");

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

			t.setScript("i=i+1;", "js");
			t.setGuard("i>=0");
			
			PlaceTransitionNet page = new PlaceTransitionNet(pn.addPage("page1"));
			
			page.addPlace("place1");
		}
		
		
		Marking m = pn.getInitialMarking();
		
		System.out.println("x0 is source: " + pn.isSource("x0"));
		System.out.println("Initial marking: " + m.toString());
		
		System.out.println("y0 is enabled: " + pn.isEnabled(pn.getTransition("y0"), m));
		
		MarkingI m2 = pn.fire(pn.getTransition("y0"), m);
		System.out.println("fired y0: " + m2.toString());

		pn.setInitialMarking(m2);
		
		Collection<Transition> en = (Collection<Transition>) pn.getEnabledTransitions(m2);
		while (!en.isEmpty()) {
			System.out.print("Enabled: ");
			for (TransitionI transition : en)
				System.out.print(transition.getId() + " ");
			System.out.println("");
			
			TransitionI t = en.iterator().next();
			m2 = pn.fire(t, m2);
			System.out.println("fired " + t.getId() + ": " + m2.toString());
			en = (Collection<Transition>) pn.getEnabledTransitions(m2);
		}
		PTNetMarshaller pnm = new PTNetMarshaller(pnset, new File(args[0]+".out"));
	}

	@Test
	public void verifiableNetTest() throws MalformedNetException {
		PlaceTransitionNet net = new PlaceTransitionNet();

		Place p0 = net.addPlace("p0", "p0", 1);
		Transition t0 = net.addTransition("t0", "t0");
		Transition t1 = net.addTransition("t1", "t1");
		Transition t2 = net.addTransition("t2", "t2");

		net.addArc(p0, t0);
		net.addArc(p0, t1);
		net.addArc(p0, t2);

		t0.setGuard("x>=0");
		t1.setGuard("x<=0 && y==true");
		t2.setGuard("x<0 && y==false");

		assertFalse(t0.getGuard().contradicts(t1.getGuard()));
		assertTrue(t0.getGuard().canBeContradictedBy(t1.getGuard()));

		assertTrue(t0.getGuard().contradicts(t2.getGuard()));

		assertTrue(t1.getGuard().contradicts(t2.getGuard()));

		Collection<Transition> enabled = new HashSet<>();
		enabled.add(t0);
		enabled.add(t1);
		enabled.add(t2);

		// x==-1 && y==true
		Collection<Transition> parallelEnabledFirstSet = new HashSet<>();
		parallelEnabledFirstSet.add(t1);

		// x==-1 && y==false
		Collection<Transition> parallelEnabledSecondSet = new HashSet<>();
		parallelEnabledSecondSet.add(t2);

		// x==0 && y==true
		Collection<Transition> parallelEnabledThirdSet = new HashSet<>();
		parallelEnabledThirdSet.add(t0);
		parallelEnabledThirdSet.add(t1);

		// x==0 && y==false
		Collection<Transition> parallelEnabledFourthSet = new HashSet<>();

		// x==1 && y==true|false
		Collection<Transition> parallelEnabledFifthSet = new HashSet<>();
		parallelEnabledFifthSet.add(t0);

		Collection<Collection<Transition>> parallelEnabled = new HashSet<>();
		parallelEnabled.add(parallelEnabledFirstSet);
		parallelEnabled.add(parallelEnabledSecondSet);
		parallelEnabled.add(parallelEnabledThirdSet);
		parallelEnabled.add(parallelEnabledFifthSet);

		assertArrayEquals(enabled.toArray(), net.getEnabledTransitions(net.getInitialMarking()).toArray());
		assertArrayEquals(parallelEnabled.toArray(), net.getParallelEnabledTransitions(net.getInitialMarking()).toArray());
	}
}
