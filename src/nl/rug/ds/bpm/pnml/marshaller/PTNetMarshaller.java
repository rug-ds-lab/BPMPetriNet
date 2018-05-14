package nl.rug.ds.bpm.pnml.marshaller;

import nl.rug.ds.bpm.pnml.jaxb.ptnet.Net;
import nl.rug.ds.bpm.pnml.jaxb.ptnet.Pnml;
import nl.rug.ds.bpm.ptnet.PlaceTransitionGraph;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Heerko Groefsema on 30-Apr-18.
 */
public class PTNetMarshaller {

	public PTNetMarshaller(Set<PlaceTransitionGraph> nets, File file) {
		Pnml pnml = new Pnml();
		Set<Net> n = new HashSet<>();

		for (PlaceTransitionGraph placeTransitionNet : nets)
			n.add((Net) placeTransitionNet.getXmlElement());

		pnml.setNets(n);

		try {
			JAXBContext context = JAXBContext.newInstance(Pnml.class);
			Marshaller marshaller = context.createMarshaller();

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(pnml, file);

		} catch (JAXBException e) { e.printStackTrace(); }
	}

	public PTNetMarshaller(Set<PlaceTransitionGraph> nets, OutputStream stream) {
		Pnml pnml = new Pnml();
		Set<Net> n = new HashSet<>();

		for (PlaceTransitionGraph placeTransitionNet : nets)
			n.add((Net) placeTransitionNet.getXmlElement());

		pnml.setNets(n);

		try {
			JAXBContext context = JAXBContext.newInstance(Pnml.class);
			Marshaller marshaller = context.createMarshaller();

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(pnml, stream);

		} catch (JAXBException e) { e.printStackTrace(); }
	}
}
