package nl.rug.ds.bpm.pnml.marshaller;

import nl.rug.ds.bpm.pnml.jaxb.ptnet.Net;
import nl.rug.ds.bpm.pnml.jaxb.ptnet.Pnml;
import nl.rug.ds.bpm.ptnet.PlaceTransitionNet;

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
	private Set<PlaceTransitionNet> nets;

	public PTNetUnmarshaller(File file) {
		nets = new HashSet<>();

		try {
			JAXBContext context = JAXBContext.newInstance(Pnml.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();

			for (Net net: ((Pnml) unmarshaller.unmarshal(file)).getNets())
				nets.add(new PlaceTransitionNet(net));
		}
		catch (Exception e) { e.printStackTrace(); }
	}

	public PTNetUnmarshaller(InputStream is) {
		nets = new HashSet<>();

		try {
			JAXBContext context = JAXBContext.newInstance(Pnml.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();

			for (Net net: ((Pnml) unmarshaller.unmarshal(is)).getNets())
				nets.add(new PlaceTransitionNet(net));
		}
		catch (Exception e) { e.printStackTrace(); }
	}

	public Set<PlaceTransitionNet> getNets() {
		return nets;
	}
}
