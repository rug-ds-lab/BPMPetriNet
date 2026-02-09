package nl.rug.ds.bpm.decomposition;

import nl.rug.ds.bpm.petrinet.ptnet.OneSafeNet;
import nl.rug.ds.bpm.petrinet.ptnet.element.Arc;
import nl.rug.ds.bpm.petrinet.ptnet.element.Node;
import nl.rug.ds.bpm.petrinet.ptnet.element.Place;
import nl.rug.ds.bpm.petrinet.ptnet.element.Transition;

import java.util.HashMap;
import java.util.Map;

/**
 * Class LoopNet.
 *
 * <p>Extends a {@link DecomposedNet} so that it can store links representing iterations and links representing
 * exits of the loop.</p>
 *
 * @see DecomposedNet
 * @author Thomas M. Prinz
 * @version 1.0.0
 */
public class LoopNet extends DecomposedNet {

    /**
     * A mapping from end places to arcs in the parent net.
     */
    private final Map<Place, Arc> endArcs = new HashMap<>();

    /**
     * A mapping from arcs in the parent net to end places in this net.
     */
    private final Map<Arc,Place> arcToEnds = new HashMap<>();

    /**
     * A mapping from an end place to a start place of this loop net.
     */
    private final Map<Place, Place> iteration = new HashMap<>();

    /**
     * Constructor.
     * @param origin The parent.
     */
    public LoopNet(OneSafeNet origin) {
        super(origin);
    }

    /**
     * Adds a link from an end place to an arc of parent net.
     * @param end The end place.
     * @param arc The arc in the parent net.
     */
    public void addLinkFromEndPlaceToParentNetArc(Place end, Arc arc) {
        this.endArcs.put(end, arc);
        this.arcToEnds.put(arc, end);
    }

    /**
     * Replaces a link from an end place to an arc of the parent net if the parent net's arc was replaced.
     * @param arc The arc to replace.
     * @param replace The new arc.
     */
    public void replaceLinkFromEndPlaceToParentNetArc(Arc arc, Arc replace) {
        if (this.endArcs.containsValue(arc)) {
            Place end = this.arcToEnds.get(arc);
            this.arcToEnds.remove(arc);
            this.arcToEnds.put(replace, end);
            this.endArcs.remove(end);
            this.endArcs.put(end, replace);
        }
    }

    /**
     * Get a mapping from the end places to the arcs in the parent net.
     * @return The mapping.
     */
    public Map<Place,Arc> getArcLinksFromLoopNetEnds() {
        return this.endArcs;
    }

    /**
     * Add a link between an end place and a start place of this loop net to represent an iteration.
     * @param source The source (end).
     * @param target The target (start).
     */
    public void addIterationLink(Place source, Place target) {
        this.iteration.put(source, target);
    }

    /**
     * Get the links as mapping representing iterations.
     * @return The mapping.
     */
    public Map<Place,Place> getIterationLinks() {
        return this.iteration;
    }

    /**
     * Produces a DOT output to easily visualize a net.
     * @return The DOT string.
     */
    public String asDotGraph() {
        String graph = super.asDotGraph();
        // Remove the closing and add additional information.
        graph = graph.replaceAll("}\n$", "");

        for (Place end: this.iteration.keySet()) {
            graph += this.getDotNodeId(this, end) + "->" + this.getDotNodeId(this, this.iteration.get(end)) + "[style=\"dotted\",arrowhead=\"curve\",label=\"repeat arc\"]" + "\n";
        }
        for (Place end: this.endArcs.keySet()) {
            graph += this.getDotNodeId(this, end) + "->" + this.getDotNodeId((DecomposedNet) this.getOrigin(), this.endArcs.get(end).getTarget()) + "[style=\"dotted\",label=\"end arc\"]" + "\n";
        }

        graph += "}" + "\n";
        return graph;
    }

    /**
     * Create the dot-graph code for a given node.
     * @param node The node.
     * @return The dot-graph code for the given node.
     */
    protected String asDotGraphNode(DecomposedNet net, Node node) {
        if (node instanceof Transition) return super.asDotGraphNode(net, node);
        if (!this.iteration.containsKey((Place) node) && !this.endArcs.containsKey((Place) node)) {
            return super.asDotGraphNode(net, node);
        } else {
            String nId = this.getDotNodeId(net, node);
            return nId + "[shape=\"octagon\" label=\"" + node.getId() + " (" + node.getName() + ")" + "\"" + "]" + "\n";
        }
    }

}