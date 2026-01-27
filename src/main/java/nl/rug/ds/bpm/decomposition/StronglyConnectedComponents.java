package nl.rug.ds.bpm.decomposition;

import nl.rug.ds.bpm.petrinet.ptnet.OneSafeNet;
import nl.rug.ds.bpm.petrinet.ptnet.element.Node;

import java.util.Stack;

public class StronglyConnectedComponents {
    private Stack<Node> stack;

    private OneSafeNet net;

    public StronglyConnectedComponents(OneSafeNet net) {
        this.net = net;
    }

    public void getLoops()
}
