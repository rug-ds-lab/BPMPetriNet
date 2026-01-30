package nl.rug.ds.bpm.petrinet.ptnet;

import nl.rug.ds.bpm.petrinet.interfaces.marking.MarkingI;
import nl.rug.ds.bpm.petrinet.interfaces.net.DecomposableNet;
import nl.rug.ds.bpm.petrinet.ptnet.element.Arc;
import nl.rug.ds.bpm.petrinet.ptnet.element.Place;
import nl.rug.ds.bpm.petrinet.ptnet.marking.Marking;
import nl.rug.ds.bpm.util.exception.MalformedNetException;

public class OneSafeNet extends PlaceTransitionNet implements DecomposableNet {

    @Override
    public Arc addArc(String sourceId, String targetId, int weight) throws MalformedNetException {
        if (weight > 1)
            throw new MalformedNetException("Weight over 1 not allowed.");
        return super.addArc(sourceId, targetId, weight);
    }

    @Override
    public void addPlace(Place p) throws MalformedNetException {
        if (p.getTokens() > 1)
            throw new MalformedNetException("Weight over 1 not allowed.");
        else if (!getInitialMarking().getMarkedPlaces().isEmpty())
            throw new MalformedNetException("Only one place with a token allowed.");
        else
            super.addPlace(p);
    }

    @Override
    public Marking getInitialMarking() {
        Marking m = new Marking(1);

        return super.getInitialMarking(m);
    }

    @Override
    public void setInitialMarking(MarkingI marking) throws MalformedNetException {
        if (marking.getMarkedPlaces().size() != 1)
            throw new MalformedNetException("Net must have exactly one place with a token.");
        else if (marking.getTotalTokens() > 1)
            throw new MalformedNetException("Net must initially not have more than token.");
        else
            super.setInitialMarking(marking);
    }
}
