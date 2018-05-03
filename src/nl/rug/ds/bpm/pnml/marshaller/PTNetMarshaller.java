package nl.rug.ds.bpm.pnml.marshaller;

import nl.rug.ds.bpm.petrinet.PetriNet;
import nl.rug.ds.bpm.pnml.jaxb.core.Net;
import nl.rug.ds.bpm.pnml.jaxb.core.Pnml;

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

	public PTNetMarshaller(Set<PetriNet> nets, File file) {
		Pnml pnml = new Pnml();
		Set<Net> n = new HashSet<>();

		for (PetriNet petriNet: nets)
			n.add((Net) petriNet.getXmlElement());

		pnml.setNets(n);

		try {
			JAXBContext context = JAXBContext.newInstance(Pnml.class);
			Marshaller marshaller = context.createMarshaller();

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(pnml, file);

		} catch (JAXBException e) {	}
	}

	public PTNetMarshaller(Set<PetriNet> nets, OutputStream stream) {
		Pnml pnml = new Pnml();
		Set<Net> n = new HashSet<>();

		for (PetriNet petriNet: nets)
			n.add((Net) petriNet.getXmlElement());

		pnml.setNets(n);

		try {
			JAXBContext context = JAXBContext.newInstance(Pnml.class);
			Marshaller marshaller = context.createMarshaller();

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(pnml, stream);

		} catch (JAXBException e) {	}
	}
}
