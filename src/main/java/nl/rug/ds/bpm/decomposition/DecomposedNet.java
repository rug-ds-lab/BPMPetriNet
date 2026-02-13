package nl.rug.ds.bpm.decomposition;

import nl.rug.ds.bpm.petrinet.ptnet.OneSafeNet;
import nl.rug.ds.bpm.petrinet.ptnet.PlaceTransitionNet;
import nl.rug.ds.bpm.petrinet.ptnet.element.Arc;
import nl.rug.ds.bpm.petrinet.ptnet.element.Node;
import nl.rug.ds.bpm.petrinet.ptnet.element.Place;
import nl.rug.ds.bpm.petrinet.ptnet.element.Transition;

import java.util.*;

/**
 * Class DecomposedNet.
 *
 * <p>A {@link DecomposedNet} extends {@link OneSafeNet}s, thus, providing the same information. However, this net
 * has an origin or parent net. Furthermore, if the origin net was cyclic, this kind of net contains a list of
 * subnets to which the origin was decomposed. For those subnets, it retains links from loop places to the
 * corresponding subnet and from loop-entry arcs to starting places of the subnets.</p>
 *
 *
 * @see OneSafeNet
 * @author Thomas M. Prinz
 * @version 1.0.0
 */
public class DecomposedNet extends OneSafeNet {

    private static int instanceCounter = 0;

    private final int instance = DecomposedNet.instanceCounter++;

    private final Place startPlace;

    private final NetTemplate template;

    private final Map<Place,DecomposedNet> iterationInstances = new HashMap<>();

    private final Map<Arc,DecomposedNet> nestedInstances = new HashMap<>();

    private final Collection<Place> loopPlaces = new HashSet<>();

    /**
     * Constructor.
     * @param template The origin or parent net.
     */
    public DecomposedNet(Place start, NetTemplate template) {
        this.startPlace = start;
        this.template = template;
    }

    /**
     * Get the origin / parent.
     * @return The origin / parent.
     */
    public NetTemplate getTemplate() {
        return this.template;
    }

    public void addIterationInstance(Place end, DecomposedNet instance) {
        this.iterationInstances.put(end, instance);
    }

    public void addNestedInstance(Arc arc, DecomposedNet instance) {
        this.nestedInstances.put(arc, instance);
        this.loopPlaces.add((Place) arc.getTarget());
    }

    public Map<Place,DecomposedNet> getIterationInstances() {
        return this.iterationInstances;
    }

    public Map<Arc,DecomposedNet> getNestedInstances() {
        return this.nestedInstances;
    }

    public String asDotGraph() {
        return this.asDotGraph(new HashSet<>());
    }

    /**
     * Produces a DOT output to easily visualize a net.
     * @return The DOT string.
     */
    public String asDotGraph(Collection<DecomposedNet> processed) {
        if (processed.contains(this)) return "";
        processed.add(this);

        String netId = this.getDotNetId(this);
        String graph = "digraph " + netId + " {" + "\n";
        for (Node node: this.getIndexedNodes().values()) {
            graph += this.asDotGraphNode(this, node);
        }
        for (Arc arc: this.getArcs()) {
            String sId = this.getDotNodeId(this, arc.getSource());
            String tId = this.getDotNodeId(this, arc.getTarget());
            graph += sId + "->" + tId + "\n";
        }
        // Print nested nets
        for (Arc arc: this.nestedInstances.keySet()) {
            DecomposedNet nestedNet = this.nestedInstances.get(arc);
            Place startingPlace = nestedNet.startPlace;
            graph += "\n" + nestedNet.asDotGraph(processed).replaceAll("digraph", "subgraph") + "\n";
            graph += this.getDotNodeId(this, arc.getSource()) + "->" + this.getDotNodeId(nestedNet, startingPlace) + "[style=\"dotted\",label=\"start arc\"]" + "\n";
        }
        // Print iterations
        for (Place end: this.iterationInstances.keySet()) {
            DecomposedNet nestedNet = this.iterationInstances.get(end);
            Place startingPlace = nestedNet.startPlace;
            graph += "\n" + nestedNet.asDotGraph(processed).replaceAll("digraph", "subgraph") + "\n";
            graph += this.getDotNodeId(this, end) + "->" + this.getDotNodeId(nestedNet, startingPlace) + "[style=\"dotted\",label=\"iteration arc\"]" + "\n";
        }
        // Print finishes
        for (Place end: this.template.getUpArcs().keySet()) {
            if (this.getPlaces().contains(end)) {
                Arc arc = this.template.getUpArcs().get(end);
                NetTemplate parentTemplate = (NetTemplate) this.template.getParent();
                for (DecomposedNet callee : parentTemplate.instances()) {
                    graph += this.getDotNodeId(this, end) + "->" + this.getDotNodeId(callee, arc.getTarget()) + "[style=\"dotted\",label=\"finish arc\"]" + "\n";
                }
            }
        }

        graph += "}" + "\n";

        return graph;
    }

    /**
     * Create the dot-graph code for a given node.
     * @param net The related net of the node.
     * @param node The node.
     * @return The dot-graph code for the given node.
     */
    protected String asDotGraphNode(DecomposedNet net, Node node) {
        String nId = this.getDotNodeId(net, node);
        return nId + "[" + (node instanceof Transition ? "shape=\"box\" " :
                (this.loopPlaces.contains((Place) node) ? "shape=\"box3d\" " :
                        (this.template.isEnd(node)) ? "shape=\"octagon\" " : "shape=\"circle\" ")) +
                "label=\"" + (node instanceof Transition && ((Transition) node).isTau() ? "tau" : node.getId() + " (" + node.getName() + ")") + "\"" +
                (this.startPlace.equals(node) ? " peripheries=2" : "") +
                "]" + "\n";
    }

    /**
     * Get an id that is ok for DOT.
     * @param net The net containing the node.
     * @param node The node.
     * @return The id.
     */
    protected String getDotNodeId(DecomposedNet net, Node node) {
        return this.getDotNetId(net) + PlaceTransitionNet.slug(node.getId());
    }

    /**
     * Get the name of the net allowed for DOT.
     * @param net The net.
     * @return The id of the net.
     */
    private String getDotNetId(DecomposedNet net) {
        String netId = "DecomposedNet" + net.instance;
        if (net.getId() != null) {
            netId = "cluster" + PlaceTransitionNet.slug(net.getId() + net.instance);
        }
        return netId;
    }

}