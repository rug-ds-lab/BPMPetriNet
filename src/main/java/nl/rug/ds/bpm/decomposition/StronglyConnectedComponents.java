package nl.rug.ds.bpm.decomposition;

import nl.rug.ds.bpm.petrinet.ptnet.OneSafeNet;
import nl.rug.ds.bpm.petrinet.ptnet.element.Arc;
import nl.rug.ds.bpm.petrinet.ptnet.element.Node;

import java.util.*;

/**
 * Class StronglyConnectedComponents.
 *
 * <p>Provides an algorithm to search for loops (as non-trivial strongly connected components) in a net. The
 * algorithm only requires knowledge about the graph structure and mostly ignores differences between
 * places and transitions.</p>
 *
 * <p>The output of the algorithm is of type {@link Loop}.</p>
 *
 * <p>Example:</p>
 * <code>
 * StronglyConnectedComponents scc = new StronglyConnectedComponents(net);
 * Collection<Loop> loops = scc.findLoops();
 * </code>
 *
 * <p>The algorithm is based on:
 * <cite>R. E. Tarjan, Depth-first search and linear graph algorithms, SIAM J. Comput. 1 (2) (1972) 146–160.
 * doi:10.1137/0201010.</cite>
 * </p>
 *
 * @author Thomas M. Prinz (Thomas.Prinz@uni-jena.de)
 * @version 1.0.0
 */
public class StronglyConnectedComponents {

    /**
     * Properties required by the algorithm to give nodes an index of visit.
     */
    private int globalIndex = 0;
    private final Stack<Node> stack = new Stack<>();
    private int[] index;
    private int[] lowLink;
    private final Collection<Loop> loops = new HashSet<>();
    private final OneSafeNet net;
    private int maxIndex = 0;

    /**
     * Constructor.
     * @param net The net to search for loops.
     */
    public StronglyConnectedComponents(OneSafeNet net) {
        this.net = net;
    }

    /**
     * Find loops (and perform the algorithm).
     * @return A collection of {@link Loop}.
     */
    public Collection<Loop> findLoops() {
        this.analyze();
        return this.loops;
    }

    /**
     * Start the algorithm by initializing and iterating over non-visited nodes.
     */
    private void analyze() {
        // Initialize.
        this.maxIndex = Collections.max(this.net.getNodeIndex().values()) + 1;
        this.index = new int[maxIndex];
        this.lowLink = new int[maxIndex];

        this.net.getNodeIndex().forEach((key, value) -> {
            this.index[value] = -1;
            this.lowLink[value] = -1;
        });

        this.net.getNodeIndex().forEach((key, value) -> {
            if (this.index[value] == -1) {
                this.stronglyConnected(key, value);
            }
        });
    }

    /**
     * Recursively find strongly connected components.
     * @param node The current node to investigate.
     * @param nodeId The index of the current node to investigate.
     */
    private void stronglyConnected(Node node, int nodeId) {
        this.index[nodeId] = this.globalIndex;
        this.lowLink[nodeId] = this.globalIndex++;
        this.stack.push(node);

        Collection<Arc> outgoing = net.getOutgoing(node);
        for (Arc out: outgoing) {
            Node suc = out.getTarget();
            int sucId = this.net.getIndexOfNode(suc);
            if (this.index[sucId] == -1) {
                this.stronglyConnected(suc, sucId);
                this.lowLink[nodeId] = Math.min(this.lowLink[nodeId], this.lowLink[sucId]);
            } else if (this.stack.contains(suc)) {
                this.lowLink[nodeId] = Math.min(this.lowLink[nodeId], this.index[sucId]);
            }
        }

        if (this.index[nodeId] == this.lowLink[nodeId]) {
            // A SCC is detected.
            Loop loop = new Loop(this.net);
            // We collect all preset and postset nodes of *all* nodes within the loop.
            BitSet preset = new BitSet(this.maxIndex);
            BitSet postset = new BitSet(this.maxIndex);
            BitSet loopNodes = new BitSet(this.maxIndex);
            int currentId;
            do {
                Node current = stack.pop();
                currentId = this.net.getIndexOfNode(current);
                loop.addComponent(current);
                loopNodes.set(currentId);
                // We store all preset and postset nodes.
                preset.or(this.net.getPreBitSets().get(currentId));
                postset.or(this.net.getPostBitSets().get(currentId));
            } while (currentId != nodeId);

            if (loop.getComponents().size() >= 2) {
                // It is not a trivial SCC. It is a loop.

                // Determine entries, exits, etc. of the loop based on:
                // Prinz, T. M., Choi, Y. & Ha, N. L. (2024).
                // Soundness unknotted: An efficient soundness checking algorithm for arbitrary cyclic process models by
                // loosening loops.
                // DOI: https://doi.org/10.1016/j.is.2024.102476

                // Collect entries.

                // All preset nodes without those in the loop are nodes outside of loop but with a connection into the
                // loop.
                preset.andNot(loopNodes);
                // Based on those nodes, we find the entries.
                BitSet entriesSet = new BitSet(this.maxIndex);
                for (int p = preset.nextSetBit(0); p >= 0; p = preset.nextSetBit(p + 1)) {
                    BitSet ps = this.net.getPostBitSets().get(p);
                    BitSet tmp = (BitSet) ps.clone();
                    tmp.and(loopNodes);
                    entriesSet.or(tmp);
                }
                loop.addEntries(entriesSet);

                // All postset nodes without those in the loop are nodes outside of loop but with a connection from
                // inside the loop.
                postset.andNot(loopNodes);
                // Based on those nodes, we find the exits.
                BitSet exitsSet = new BitSet(this.maxIndex);
                for (int p = postset.nextSetBit(0); p >= 0; p = postset.nextSetBit(p + 1)) {
                    BitSet ps = this.net.getPreBitSets().get(p);
                    BitSet tmp = (BitSet) ps.clone();
                    tmp.and(loopNodes);
                    exitsSet.or(tmp);
                }
                loop.addExits(exitsSet);

                // Determine the do-body by a depth-first search with ...
                // ... (1) the final set of nodes in the do-body.
                BitSet doBodySet = new BitSet(this.maxIndex);
                // ... (2) a list with nodes to visit next.
                BitSet workingList = new BitSet(this.maxIndex);
                workingList.or(entriesSet);
                doBodySet.or(entriesSet);
                // ... (3) the nodes where to stop the search.
                BitSet cut = new BitSet(this.maxIndex);
                for (int ex = exitsSet.nextSetBit(0); ex >= 0; ex = exitsSet.nextSetBit(ex + 1)) {
                    cut.or(this.net.getPostBitSets().get(ex));
                }
                while (!workingList.isEmpty()) {
                    // Take the next to investigate.
                    int current = workingList.nextSetBit(0);
                    workingList.clear(current);
                    // Compute further nodes to visit.
                    BitSet next = (BitSet) this.net.getPostBitSets().get(current).clone();
                    next.andNot(cut);
                    next.andNot(doBodySet);
                    next.andNot(workingList);
                    next.and(loopNodes);
                    // Add them to the do-body and to the working list.
                    doBodySet.or(next);
                    workingList.or(next);
                }

                loop.addDoBody(doBodySet);

                // Determine the flows within the loop
                for (Arc arc: this.net.getArcs()) {
                    int src = this.net.getIndexOfNode(arc.getSource());
                    int tgt = this.net.getIndexOfNode(arc.getTarget());
                    if (loopNodes.get(src) && loopNodes.get(tgt)) {
                        loop.addArc(arc);
                    }
                }

                this.loops.add(loop);
            }
        }
    }

    /**
     * Get the loops once the algorithm was already performed.
     * @return A collection of {@link Loop}.
     */
    public Collection<Loop> getLoops() {
        return this.loops;
    }
}