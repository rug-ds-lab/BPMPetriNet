package nl.rug.ds.bpm.decomposition;

import nl.rug.ds.bpm.petrinet.ptnet.OneSafeNet;
import nl.rug.ds.bpm.petrinet.ptnet.element.Arc;
import nl.rug.ds.bpm.petrinet.ptnet.element.Node;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Loop {
    private final NetTemplate parent;

    private final Set<Node> components;
    private final Set<Node> entries;
    private final Set<Node> exits;
    private final Set<Node> doBody;

    private final Set<Arc> arcs;

    public Loop(NetTemplate parent) {
        this.parent = parent;

        components = new HashSet<>();
        entries = new HashSet<>();
        exits = new HashSet<>();
        doBody = new HashSet<>();

        arcs = new HashSet<>();
    }

    public NetTemplate getParent() {
        return parent;
    }

    public Set<Node> getComponents() {
        return components;
    }

    public void addComponent(Node node) {
        components.add(node);
    }

    public void addComponents(Set<Node> nodes) {
        components.addAll(nodes);
    }

    public void addComponents(BitSet nodeIndexes) {
        components.addAll(parent.asNodes(nodeIndexes));
    }

    public Set<Node> getEntries() {
        return entries;
    }

    public void addEntry(Node node) {
        components.add(node);
        entries.add(node);
    }

    public void addEntries(Set<Node> nodes) {
        components.addAll(nodes);
        entries.addAll(nodes);
    }

    public void addEntries(BitSet nodeIndexes) {
        addEntries(parent.asNodes(nodeIndexes));
    }

    public Set<Node> getExits() {
        return exits;
    }

    public void addExit(Node node) {
        components.add(node);
        exits.add(node);
    }

    public void addExits(Set<Node> nodes) {
        components.addAll(nodes);
        exits.addAll(nodes);
    }

    public void addExits(BitSet nodeIndexes) {
        addExits(parent.asNodes(nodeIndexes));
    }

    public Set<Node> getDoBody() {
        return doBody;
    }

    public void addDoBody(Node node) {
        components.add(node);
        doBody.add(node);
    }

    public void addDoBody(Set<Node> nodes) {
        components.addAll(nodes);
        doBody.addAll(nodes);
    }

    public void addDoBody(BitSet nodeIndexes) {
        addDoBody(parent.asNodes(nodeIndexes));
    }

    public Set<Arc> getArcs() {
        return arcs;
    }

    public void addArc(Arc arc) {
        arcs.add(arc);
    }

    public void addArcs(Set<Arc> arcs) {
        this.arcs.addAll(arcs);
    }

    public void addPreSet(Node target, BitSet nodeIndexes) {
        for (Node node : parent.asNodes(nodeIndexes)) {
            Arc a = parent.getArc(node, target);
            if (a != null)
                arcs.add(a);
        }
    }

    public void addPreSet(int target, BitSet nodeIndexes) {
        Node n = parent.getNodeIndex().get(target);
        addPreSet(n, nodeIndexes);
    }

    public void addPostSet(Node source, BitSet nodeIndexes) {
        for (Node node : parent.asNodes(nodeIndexes)) {
            Arc a = parent.getArc(source, node);
            if (a != null)
                arcs.add(a);
        }
    }

    public void addPostSet(int source, BitSet nodeIndexes) {
        Node n = parent.getNodeIndex().get(source);
        addPostSet(n, nodeIndexes);
    }
}
