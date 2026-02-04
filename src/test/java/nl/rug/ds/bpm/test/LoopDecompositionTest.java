package nl.rug.ds.bpm.test;

import nl.rug.ds.bpm.decomposition.LoopDecomposition;
import nl.rug.ds.bpm.petrinet.interfaces.element.TransitionI;
import nl.rug.ds.bpm.petrinet.interfaces.marking.MarkingI;
import nl.rug.ds.bpm.petrinet.ptnet.OneSafeNet;
import nl.rug.ds.bpm.petrinet.ptnet.PlaceTransitionNet;
import nl.rug.ds.bpm.petrinet.ptnet.element.Arc;
import nl.rug.ds.bpm.petrinet.ptnet.element.Place;
import nl.rug.ds.bpm.petrinet.ptnet.element.Transition;
import nl.rug.ds.bpm.petrinet.ptnet.marking.Marking;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.Net;
import nl.rug.ds.bpm.pnml.ptnet.marshaller.PTNetMarshaller;
import nl.rug.ds.bpm.pnml.ptnet.marshaller.PTNetUnmarshaller;
import nl.rug.ds.bpm.util.exception.IllegalMarkingException;
import nl.rug.ds.bpm.util.exception.MalformedNetException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Nick van Beest on 4 May 2018
 *
 */
public class LoopDecompositionTest {

	public static void main(String[] args) throws MalformedNetException, IllegalMarkingException {
		if (args.length == 0) {
			System.out.println("Please specify a file containing a net to decompose.");
			return;
		}
		File file = new File(args[0]);
		Set<Net> pnset = new HashSet<Net>();

		if (file.exists()) {
			try {
				PTNetUnmarshaller pnu = new PTNetUnmarshaller(file);
				pnset = pnu.getNets();
			} catch (MalformedNetException e) {
				System.out.println("File is not a PNML file.");
			}
		} else {
			System.out.println("No such file.");
		}

		PlaceTransitionNet pn;
		
		if (pnset.isEmpty()) {
			System.out.println("empty");
			pn = new PlaceTransitionNet("t1");
			pnset.add((Net) pn.getXmlElement());
		} else {
			pn = new OneSafeNet(pnset.iterator().next());

			System.out.println("Index");
			System.out.println(pn.getNodeIndex());
			System.out.println("Original net");
			System.out.println(pn.asDotGraph());

			System.out.println("Decomposed nets");
			Collection<OneSafeNet> acyclic = (new LoopDecomposition()).decompose((OneSafeNet) pn);
			for (OneSafeNet an: acyclic) {
				System.out.println(an.asDotGraph());
			}
		}
	}
}
