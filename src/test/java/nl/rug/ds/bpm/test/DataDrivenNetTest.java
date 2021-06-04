package nl.rug.ds.bpm.test;

import nl.rug.ds.bpm.petrinet.ddnet.DataDrivenNet;
import nl.rug.ds.bpm.petrinet.ddnet.marking.DataMarking;
import nl.rug.ds.bpm.petrinet.interfaces.element.TransitionI;
import nl.rug.ds.bpm.petrinet.interfaces.marking.DataMarkingI;
import nl.rug.ds.bpm.petrinet.ptnet.element.Arc;
import nl.rug.ds.bpm.petrinet.ptnet.element.Place;
import nl.rug.ds.bpm.petrinet.ptnet.element.Transition;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.Net;
import nl.rug.ds.bpm.pnml.ptnet.marshaller.PTNetMarshaller;
import nl.rug.ds.bpm.pnml.ptnet.marshaller.PTNetUnmarshaller;
import nl.rug.ds.bpm.util.exception.IllegalMarkingException;
import nl.rug.ds.bpm.util.exception.MalformedNetException;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Nick van Beest on 4 May 2018
 *
 */
public class DataDrivenNetTest {

	public static void main(String[] args) throws MalformedNetException {
		File file = new File(args[0]);
		Set<Net> pnset = new HashSet<Net>();
		DataDrivenNet pn;

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

		if (pnset.isEmpty()) {
			System.out.println("empty");
			pn = new DataDrivenNet("t1");
			pnset.add((Net) pn.getXmlElement());
		}
		else {
			//get first interfaces
			pn = new DataDrivenNet(pnset.iterator().next());
			
			for (Transition t : pn.getTransitions()) {
				System.out.println(t.getName());
			}
		}
		if (pn.getTransition("y0") == null) {
			System.out.println("Adding test elements");

			pn.addVariable("i", "int", "0");

			Place p = pn.addPlace("x0", "test_x0", 2);
			Transition t = pn.addTransition("y0", "ytest");
			Place p1 = (Place) pn.addPlace("x1");

			Arc a = pn.addArc("x0", "y0", 2);
			Arc a1 = pn.addArc(t, p1);

			t.setScript("i++;", "js");
			t.setGuard("i>=0");
			
			DataDrivenNet page = new DataDrivenNet(pn.addPage("page1"));
			
			page.addPlace("place1");
		}

		DataMarking m = pn.getInitialMarking();

		
		System.out.println("x0 is source: " + pn.isSource("x0"));
		System.out.println("Initial marking: " + m.toString());
		for (String var: m.getBindings().keySet())
			System.out.println(var + "=" + m.getBindings().get(var));

		System.out.println("y0 is enabled: " + pn.isEnabled(pn.getTransition("y0"), m));
		
		DataMarkingI m2 = pn.fire(pn.getTransition("y0"), m);
		System.out.println("fired y0: " + m2.toString());
		for (String var: m2.getBindings().keySet())
			System.out.println(var + "=" + m2.getBindings().get(var));

		try {
			((DataMarking)m2).addTokens("x0", 2);
		}
		catch (IllegalMarkingException e) {
			System.out.println("Error adding tokens");
		}


		DataMarkingI m3 = pn.fire(pn.getTransition("y0"), m);
		System.out.println("fired y0: " + m2.toString());
		for (String var: m2.getBindings().keySet())
			System.out.println(var + "=" + m2.getBindings().get(var));

		pn.setInitialMarking(m3);
		
		Collection<Transition> en = (Collection<Transition>) pn.getEnabledTransitions(m2);
		while (!en.isEmpty()) {
			System.out.print("Enabled: ");
			for (TransitionI transition : en)
				System.out.print(transition.getId() + " ");
			System.out.println("");
			
			TransitionI t = en.iterator().next();
			m2 = pn.fire(t, m2);
			
			System.out.println("fired " + t.getId() + ": " + m2.toString());
			for (String var: m2.getBindings().keySet()) {
				System.out.println(var + "=" + m2.getBindings().get(var));
			}
			en = (Collection<Transition>) pn.getEnabledTransitions(m2);
		}
		PTNetMarshaller pnm = new PTNetMarshaller(pnset, new File(args[0]+".out"));
	}
}
