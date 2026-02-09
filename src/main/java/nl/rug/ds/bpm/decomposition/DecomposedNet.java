package nl.rug.ds.bpm.decomposition;

import nl.rug.ds.bpm.petrinet.ptnet.OneSafeNet;
import nl.rug.ds.bpm.petrinet.ptnet.element.Arc;
import nl.rug.ds.bpm.petrinet.ptnet.element.Node;
import nl.rug.ds.bpm.petrinet.ptnet.element.Place;
import nl.rug.ds.bpm.petrinet.ptnet.element.Transition;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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

    /**
     * The origin net (or parent).
     */
    private final OneSafeNet origin;

    /**
     * A set of subnets (if available).
     */
    private final Collection<OneSafeNet> subNets = new HashSet<>();

    /**
     * Links from "loop places" to subnets representing the nets.
     */
    private final Map<Place,OneSafeNet> loopLinks = new HashMap<>();

    /**
     * Links from loop-entry arcs to "start places" of the corresponding loop nets.
     */
    private final Map<Arc,Place> arcStarts = new HashMap<>();

    /**
     * Links from starting places in loop nets to their loop nets.
     */
    private final Map<Place, LoopNet> startLoopNets = new HashMap<>();

    /**
     * Constructor.
     * @param origin The origin or parent net.
     */
    public DecomposedNet(OneSafeNet origin) {
        this.origin = origin;
    }

    /**
     * Add a link from a "loop place" to the corresponding loop net.
     * @param loopNode The place representing the loop net.
     * @param loopNet The loop net.
     */
    public void addLoopLink(Place loopNode, DecomposedNet loopNet) {
        this.loopLinks.put(loopNode, loopNet);
        if (!this.subNets.contains(loopNet)) {
            this.subNets.add(loopNet);
        }
    }

    /**
     * Get the set of direct loop nets of this decomposet net.
     * @return A set of subnets related to this net.
     */
    public Collection<OneSafeNet> getSubNets() {
        return this.subNets;
    }

    /**
     * Get a mapping of all loop links from the "loop places" to the loop nets.
     * @return The mapping.
     */
    public Map<Place,OneSafeNet> getLoopLinks() {
        return this.loopLinks;
    }

    /**
     * Add a link from a (previous) loop-entry arc to a starting place within a loop net.
     * @param arc The loop-entry arc.
     * @param place The starting place.
     */
    public void addLinkToPlaceInLoop(Arc arc, Place place, LoopNet loopNet) {
        this.arcStarts.put(arc, place);
        this.startLoopNets.put(place, loopNet);
    }

    /**
     * During loop decomposition, the loop-entry arc may will be replaced.
     * @param arc The arc to replace.
     * @param replace The new arc.
     */
    public void replaceLinkToPlaceInLoop(Arc arc, Arc replace) {
        if (this.arcStarts.containsKey(arc)) {
            Place start = this.arcStarts.get(arc);
            this.arcStarts.remove(arc);
            this.arcStarts.put(replace, start);
        }
    }

    /**
     * Get a mapping from the loop-entry arcs to the starting places of loop nets.
     * @return The mapping.
     */
    public Map<Arc,Place> getArcLinksToLoopNetStarts() {
        return this.arcStarts;
    }

    /**
     * Get the origin / parent.
     * @return The origin / parent.
     */
    public OneSafeNet getOrigin() {
        return this.origin;
    }

    /**
     * Produces a DOT output to easily visualize a net.
     * @return The DOT string.
     */
    public String asDotGraph() {
        String netId = "DecomposedNet";
        if (this.getId() != null) {
            netId = "cluster" + this.slug(this.getId());
        }
        String graph = "digraph " + netId + " {" + "\n";
        for (Node node: this.getIndexedNodes().values()) {
            graph += this.asDotGraphNode(this, node);
        }
        for (Arc arc: this.getArcs()) {
            String sId = this.getDotNodeId(this, arc.getSource());
            String tId = this.getDotNodeId(this, arc.getTarget());
            graph += sId + "->" + tId + "\n";
        }
        // Print subnets
        for (OneSafeNet subNet: this.subNets) {
            graph += "\n" + subNet.asDotGraph().replaceAll("digraph", "subgraph") + "\n";
        }
        // Print links to subnets.
        /*for (Place loopPlace: this.loopLinks.keySet()) {
            OneSafeNet loopNet = this.loopLinks.get(loopPlace);
            graph += this.getDotNodeId(this, loopPlace) + "->" + this.getDotNetId((DecomposedNet) loopNet) + "[style=\"dashed\",label=\"loop net\"]" + "\n";
        }*/
        for (Arc arc: this.arcStarts.keySet()) {
            Place startingPlace = this.arcStarts.get(arc);
            graph += this.getDotNodeId(this, arc.getSource()) + "->" + this.getDotNodeId(this.startLoopNets.get(startingPlace), startingPlace) + "[style=\"dotted\",label=\"start arc\"]" + "\n";
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
                (this.loopLinks.containsKey((Place) node) ? "shape=\"box3d\" " : ("shape=\"circle\" "))) +
                "label=\"" + (node instanceof Transition && ((Transition) node).isTau() ? "tau" : node.getId() + " (" + node.getName() + ")") + "\"" +
                "]" + "\n";
    }

    /**
     * Get an id that is ok for DOT.
     * @param net The net containing the node.
     * @param node The node.
     * @return The id.
     */
    protected String getDotNodeId(DecomposedNet net, Node node) {
        return this.getDotNetId(net) + this.slug(node.getId());
    }

    /**
     * Get the name of the net allowed for DOT.
     * @param net The net.
     * @return The id of the net.
     */
    private String getDotNetId(DecomposedNet net) {
        String netId = "DecomposedNet";
        if (net.getId() != null) {
            netId = "cluster" + this.slug(net.getId());
        }
        return netId;
    }

}