package nl.rug.ds.bpm.pnml.marshaller;

import nl.rug.ds.bpm.pnml.jaxb.ptnet.Net;
import nl.rug.ds.bpm.pnml.jaxb.ptnet.Pnml;
import nl.rug.ds.bpm.util.exception.MalformedNetException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Heerko Groefsema on 30-Apr-18.
 */
public class PTNetUnmarshaller {
	private Set<Net> nets;

	public PTNetUnmarshaller(File file) throws MalformedNetException {
		nets = new HashSet<>();

		try {
			JAXBContext context = JAXBContext.newInstance(Pnml.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();

			nets.addAll(((Pnml) unmarshaller.unmarshal(file)).getNets());
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new MalformedNetException("Malformed PNML file.");
		}
	}

	public PTNetUnmarshaller(InputStream is) throws MalformedNetException {
		nets = new HashSet<>();

		try {
			JAXBContext context = JAXBContext.newInstance(Pnml.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();

			nets.addAll(((Pnml) unmarshaller.unmarshal(is)).getNets());
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new MalformedNetException("Malformed PNML file.");
		}
	}

	public Set<Net> getNets() {
		return nets;
	}
}
