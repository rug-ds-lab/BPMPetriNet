package nl.rug.ds.bpm.decomposition;

import nl.rug.ds.bpm.petrinet.ptnet.OneSafeNet;
import nl.rug.ds.bpm.petrinet.ptnet.element.Arc;
import nl.rug.ds.bpm.petrinet.ptnet.element.Node;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <cite>R. E. Tarjan, Depth-first search and linear graph algorithms, SIAM J. Comput. 1 (2) (1972) 146–160.
 *  * doi:10.1137/0201010.</cite>
 */
public class StronglyConnectedComponents {

    private int globalIndex = 0;

    private final Stack<Node> stack = new Stack<>();

    private int[] index;

    private int[] lowLink;

    private final Collection<Loop> loops = new HashSet<>();

    private final OneSafeNet net;

    private int maxIndex = 0;

    /**
     *
     * @param net
     */
    public StronglyConnectedComponents(OneSafeNet net) {
        this.net = net;
    }

    /**
     *
     * @return
     */
    public Collection<Loop> findLoops() {
        this.analyze();
        return this.loops;
    }

    /**
     *
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
     *
     * @param node
     * @param nodeId
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

                // Determine entries, exits, etc. of the loop.

                // Prinz, T. M., Choi, Y. & Ha, N. L. (2024).
                // Soundness unknotted: An efficient soundness checking algorithm for arbitrary cyclic process models by
                // loosening loops.
                // DOI: https://doi.org/10.1016/j.is.2024.102476

                preset.andNot(loopNodes);
                postset.andNot(loopNodes);

                BitSet entriesSet = new BitSet(this.maxIndex);
                for (int p = preset.nextSetBit(0); p >= 0; p = preset.nextSetBit(p + 1)) {
                    BitSet ps = this.net.getPostBitSets().get(p);
                    BitSet tmp = (BitSet) ps.clone();
                    tmp.and(loopNodes);
                    entriesSet.or(tmp);
                }
                BitSet exitsSet = new BitSet(this.maxIndex);
                for (int p = postset.nextSetBit(0); p >= 0; p = postset.nextSetBit(p + 1)) {
                    BitSet ps = this.net.getPreBitSets().get(p);
                    BitSet tmp = (BitSet) ps.clone();
                    tmp.and(loopNodes);
                    exitsSet.or(tmp);
                }

                loop.addEntries(entriesSet);
                loop.addExits(exitsSet);

                // Determine the do-body
                BitSet doBodySet = new BitSet(this.maxIndex);
                BitSet cut = new BitSet(this.maxIndex);
                BitSet workingList = new BitSet(this.maxIndex);
                workingList.or(entriesSet);
                doBodySet.or(entriesSet);
                for (int ex = exitsSet.nextSetBit(0); ex >= 0; ex = exitsSet.nextSetBit(ex + 1)) {
                    cut.or(this.net.getPostBitSets().get(ex));
                }
                while (!workingList.isEmpty()) {
                    int current = workingList.nextSetBit(0);
                    workingList.clear(current);
                    BitSet next = (BitSet) this.net.getPostBitSets().get(current).clone();
                    next.andNot(cut);
                    next.andNot(doBodySet);
                    next.andNot(workingList);
                    next.and(loopNodes);
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

    public Collection<Loop> getLoops() {
        return this.loops;
    }
}