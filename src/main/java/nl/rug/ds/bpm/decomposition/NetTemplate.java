package nl.rug.ds.bpm.decomposition;

import nl.rug.ds.bpm.petrinet.ptnet.OneSafeNet;
import nl.rug.ds.bpm.petrinet.ptnet.PlaceTransitionNet;
import nl.rug.ds.bpm.petrinet.ptnet.element.Arc;
import nl.rug.ds.bpm.petrinet.ptnet.element.Node;
import nl.rug.ds.bpm.petrinet.ptnet.element.Place;
import nl.rug.ds.bpm.petrinet.ptnet.element.Transition;
import nl.rug.ds.bpm.petrinet.ptnet.marking.Marking;
import nl.rug.ds.bpm.util.exception.IllegalMarkingException;
import nl.rug.ds.bpm.util.exception.MalformedNetException;
import nl.rug.ds.bpm.util.pair.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class NetTemplate {

    private Object parent;

    private String id;

    private Loop loop;

    private int currentIndex = 0;

    private Collection<Node> nodes;

    private Map<Integer,Node> indexedNodes = new HashMap<>();
    private Map<Node,Integer> nodeIndex = new HashMap<>();

    private Collection<Arc> arcs;

    private Map<Node, Collection<Arc>> incoming = new HashMap<>();
    private Map<Node, Collection<Arc>> outgoing = new HashMap<>();
    private Map<Integer, BitSet> presets = new HashMap<>();
    private Map<Integer, BitSet> postsets = new HashMap<>();

    private Collection<Place> starts = new HashSet<>();

    private Collection<Place> ends = new HashSet<>();

    private Map<Place, DecomposedNet> instances = new HashMap<>();

    private List<Pair<Place, Place>> iterationArcs = new ArrayList<>();

    private Map<Place, Arc> upArcs = new HashMap<>();
    private Map<Arc, Place> reverseUpArcs = new HashMap<>();

    private Map<Arc, Pair<Place, NetTemplate>> downArcs = new HashMap<>();

    private Map<Place, NetTemplate> loopLinks = new HashMap<>();


    public NetTemplate(Object parent, String id, Loop loop) {
        this.parent = parent;
        this.id = id;
        this.loop = loop;
        this.nodes = new HashSet<>();
        this.arcs = new HashSet<>();
    }

    public NetTemplate(Object parent, String id, Loop loop, Collection<Node> nodes) {
        this(parent, id, loop);
        this.addNodes(nodes);
    }

    public NetTemplate(Object parent, String id, Loop loop, Collection<Node> nodes, Collection<Arc> arcs) {
        this(parent, id, loop, nodes);
        this.addArcs(arcs);
    }

    public String getId() {
        return this.id;
    }

    public Object getParent() {
        return this.parent;
    }

    public void addNode(Node node) {
        if (this.nodes.add(node)) {
            if (node instanceof Place) {
                Place p = (Place) node;
                if (p.getTokens() > 0) {
                    p.setTokens(0);
                }
            }
            int index = this.currentIndex++;
            this.indexedNodes.put(index, node);
            this.nodeIndex.put(node, index);
        }
    }

    public void removeNode(Node node) {
        this.nodes.remove(node);
        int index = this.nodeIndex.get(node);
        this.nodeIndex.remove(node);
        this.indexedNodes.remove(index);
        this.presets.remove(index);
        this.postsets.remove(index);
        this.starts.remove(node);
        this.ends.remove(node);
    }

    public void addNodes(Collection<Node> nodes) {
        for (Node node : nodes) {
            this.addNode(node);
        }
    }

    public Collection<Node> getNodes() {
        return this.nodes;
    }

    public Map<Integer,Node> getNodeIndex() {
        return this.indexedNodes;
    }

    public int getIndexOfNode(Node node) {
        return this.nodeIndex.get(node);
    }

    public void addArcs(Collection<Arc> arcs) {
        for (Arc arc : arcs) this.addArc(arc);
    }

    public void addArc(Arc arc) {
        this.arcs.add(arc);
        Node src = arc.getSource();
        Node tgt = arc.getTarget();
        int srcIndex = this.nodeIndex.get(src);
        int tgtIndex = this.nodeIndex.get(tgt);
        if (!this.outgoing.containsKey(src)) {
            this.outgoing.put(src, new HashSet<>());
            this.postsets.put(srcIndex, new BitSet(this.currentIndex));
        }
        if (!this.incoming.containsKey(tgt)) {
            this.incoming.put(tgt, new HashSet<>());
            this.presets.put(tgtIndex, new BitSet(this.currentIndex));
        }
        this.outgoing.get(src).add(arc);
        this.incoming.get(tgt).add(arc);
        this.postsets.get(srcIndex).set(tgtIndex);
        this.presets.get(tgtIndex).set(srcIndex);
    }

    public Collection<Arc> getArcs() {
        return this.arcs;
    }

    public Arc getArc(Node src, Node tgt) {
        for (Arc arc : this.outgoing.get(src)) {
            if (arc.getTarget().equals(tgt)) return arc;
        }
        return null;
    }

    public void removeArc(Arc arc) {
        if (this.arcs.remove(arc)) {
            Node src = arc.getSource();
            Node tgt = arc.getTarget();
            int srcIndex = this.nodeIndex.get(src);
            int tgtIndex = this.nodeIndex.get(tgt);
            this.outgoing.get(src).remove(arc);
            this.incoming.get(tgt).remove(arc);
            this.postsets.get(srcIndex).clear(tgtIndex);
            this.presets.get(tgtIndex).clear(srcIndex);
        }
    }

    public Collection<Arc> getIncoming(Node node) {
        Collection<Arc> arcs = this.incoming.get(node);
        if (arcs == null) return Collections.emptySet();
        else return arcs;
    }

    public Collection<Arc> getOutgoing(Node node) {
        Collection<Arc> arcs = this.outgoing.get(node);
        if (arcs == null) return Collections.emptySet();
        else return arcs;
    }

    public BitSet getPostSetBitSet(Node node) {
        int index = this.nodeIndex.get(node);
        return this.getPostSetBitSet(index);
    }

    public BitSet getPostSetBitSet(int index) {
        BitSet set = this.postsets.get(index);
        if (set == null) return new BitSet(this.currentIndex);
        else return set;
    }

    public BitSet getPreSetBitSet(Node node) {
        int index = this.nodeIndex.get(node);
        return this.getPreSetBitSet(index);
    }

    public BitSet getPreSetBitSet(int index) {
        BitSet set = this.presets.get(index);
        if (set == null) return new BitSet(this.currentIndex);
        else return set;
    }

    public Set<Node> asNodes(BitSet b) {
        Set<Node> nodes = new HashSet<>();

        Iterator<Integer> i = b.stream().iterator();
        while (i.hasNext()) {
            nodes.add(this.indexedNodes.get(i.next()));
        }

        return nodes;
    }

    public void addStart(Place start) {
        this.addNode(start);
        this.starts.add(start);
    }

    public Collection<Place> getStarts() {
        if (!this.starts.isEmpty()) return this.starts;
        Set<Node> implicit = this.nodes.stream().filter(node -> {
            return this.getIncoming(node).isEmpty() && node instanceof Place;
        }).collect(Collectors.toSet());
        Collection<Place> implicitStarts = new HashSet<>();
        for (Node im : implicit) implicitStarts.add((Place) im);
        return implicitStarts;
    }

    public void addEnd(Place end) {
        this.addNode(end);
        this.ends.add(end);
    }

    public boolean isEnd(Node node) {
        return this.ends.contains(node);
    }

    public void addIterationArc(Place end, Place start) {
        this.iterationArcs.add(new Pair<>(end, start));
    }

    public void addUpArc(Place end, Arc upArc) {
        this.upArcs.put(end, upArc);
    }

    public void replaceUpArc(Arc old, Arc newArc) {
        Place end = this.reverseUpArcs.remove(old);
        if (end != null) {
            this.upArcs.put(end, newArc);
            this.reverseUpArcs.put(newArc, end);
        }
    }

    public Map<Place,Arc> getUpArcs() {
        return this.upArcs;
    }

    public void addDownArc(Arc downArc, Place start, NetTemplate loopNet) {
        this.downArcs.put(downArc, new Pair<>(start, loopNet));
    }

    public void replaceDownArc(Arc old, Arc newArc) {
        Pair<Place, NetTemplate> info = this.downArcs.remove(old);
        if (info != null) this.downArcs.put(newArc, info);
    }

    public void addLoopLink(Place loopPlace, NetTemplate loopTemplate) {
        this.loopLinks.put(loopPlace, loopTemplate);
    }

    protected static NetTemplate asNetTemplate(OneSafeNet net) {
        NetTemplate template = new NetTemplate(net, net.getId(), null, net.getIndexedNodes().values());

        net.getArcs().forEach(arc -> {
            template.addArc(new Arc(arc.getId(), arc.getSource(), arc.getTarget()));
        });

        net.getPlaces().forEach(place -> {
            if (place.getTokens() > 0) template.addStart(place);
        });

        return template;
    }

    protected static NetTemplate asNetTemplate(NetTemplate template) {
        return new NetTemplate(template, template.getId(), template.loop, template.nodes, template.arcs);
    }

    protected DecomposedNet instance(Place start) throws MalformedNetException, IllegalMarkingException {
        DecomposedNet instance = this.instances.get(start);
        if (instance != null) return instance;

        instance = new DecomposedNet(start, this);
        this.instances.put(start, instance);
        // Collect the net by a depth-first search
        this.collect(start, instance);

        // Recursively instantiate iterations.
        for (Pair<Place,Place> iterationArc : this.iterationArcs) {
            if (instance.getPlaces().contains(iterationArc.getFirst())) {
                instance.addIterationInstance(iterationArc.getFirst(), this.instance(iterationArc.getSecond()));
            }
        }

        // Recursively instantiate nested loops.
        for (Arc downArc : downArcs.keySet()) {
            if (instance.getArcs().contains(downArc)) {
                Pair<Place, NetTemplate> info = downArcs.get(downArc);
                instance.addNestedInstance(downArc, info.getSecond().instance(info.getFirst()));
            }
        }

        // Initial marking
        Marking init = new Marking();
        init.addTokens(start.getId(), 1);
        instance.setInitialMarking(init);

        return instance;
    }

    private void collect(Node current, DecomposedNet instance) throws MalformedNetException {
        if (instance.getNodeIndex().containsKey(current)) return;
        // Add the node
        if (current instanceof Place) instance.addPlace((Place) current);
        else instance.addTransition((Transition) current);

        // Walk through the arcs
        for (Arc out : this.getOutgoing(current)) {
            this.collect(out.getTarget(), instance);
            instance.addArc(out);
        }
    }

    public Collection<DecomposedNet> instances() {
        return this.instances.values();
    }

    public String asDotGraph() {
        String netId = this.getDotNetId(this);
        String graph = "digraph " + netId + " {" + "\n";
        for (Node node: this.nodes) {
            graph += this.asDotGraphNode(this, node);
        }
        for (Arc arc: this.arcs) {
            String sId = this.getDotNodeId(this, arc.getSource());
            String tId = this.getDotNodeId(this, arc.getTarget());
            graph += sId + "->" + tId + "\n";
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
    private String asDotGraphNode(NetTemplate net, Node node) {
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
    private String getDotNodeId(NetTemplate net, Node node) {
        return this.getDotNetId(net) + PlaceTransitionNet.slug(node.getId());
    }

    /**
     * Get the name of the net allowed for DOT.
     * @param net The net.
     * @return The id of the net.
     */
    private String getDotNetId(NetTemplate net) {
        String netId = "NetTemplate";
        if (net.getId() != null) {
            netId = "cluster" + PlaceTransitionNet.slug(net.getId());
        }
        return netId;
    }
}
