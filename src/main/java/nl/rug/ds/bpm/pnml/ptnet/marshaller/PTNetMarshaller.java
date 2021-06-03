package nl.rug.ds.bpm.pnml.ptnet.marshaller;

import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.Net;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.Pnml;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.File;
import java.io.OutputStream;
import java.util.Set;

/**
 * Created by Heerko Groefsema on 30-Apr-18.
 */
public class PTNetMarshaller {

	public PTNetMarshaller(Set<Net> nets, File file) {
		Pnml pnml = new Pnml();

		pnml.setNets(nets);

		try {
			JAXBContext context = JAXBContext.newInstance(Pnml.class);
			Marshaller marshaller = context.createMarshaller();

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(pnml, file);

		} catch (JAXBException e) { e.printStackTrace(); }
	}

	public PTNetMarshaller(Set<Net> nets, OutputStream stream) {
		Pnml pnml = new Pnml();

		pnml.setNets(nets);

		try {
			JAXBContext context = JAXBContext.newInstance(Pnml.class);
			Marshaller marshaller = context.createMarshaller();

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(pnml, stream);

		} catch (JAXBException e) { e.printStackTrace(); }
	}
}
