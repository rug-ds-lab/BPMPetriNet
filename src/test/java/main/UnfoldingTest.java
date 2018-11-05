package main;

import nl.rug.ds.bpm.eventstructure.PESPrefixUnfolding;
import nl.rug.ds.bpm.petrinet.ptnet.PlaceTransitionNet;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.Net;
import nl.rug.ds.bpm.pnml.ptnet.marshaller.PTNetUnmarshaller;
import nl.rug.ds.bpm.util.exception.MalformedNetException;

import java.io.File;
import java.util.Set;

public class UnfoldingTest {

	public static void main(String[] args) throws MalformedNetException {
		PTNetUnmarshaller pnu = new PTNetUnmarshaller(new File(args[0]));
		Set<Net> pnset = pnu.getNets();
		PlaceTransitionNet pn;

		if (pnset.isEmpty()) {
			System.out.println("empty");
		}
		else {
			//get first interfaces
			pn = new PlaceTransitionNet(pnset.iterator().next());
			PESPrefixUnfolding unfolding = new PESPrefixUnfolding(pn, "silent");
		}
	}
}