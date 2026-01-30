package nl.rug.ds.bpm.petrinet.interfaces.net;

import nl.rug.ds.bpm.petrinet.ptnet.element.Node;

import java.util.BitSet;
import java.util.Map;
import java.util.Set;

/**
 * An interface that defines the methods required for loop decomposition.
 */
public interface DecomposableNet {

    /**
     * Returns a map that maps indexes to nodes.
     *
     * @return a map that maps indexes to nodes.
     */
    Map<Integer, Node> getIndexedNodes();

    /**
     * Returns the Node that corresponds to the given index.
     *
     * @param index the index of the Node.
     * @return the Node that corresponds to the given index, or null if there exists no corresponding Node.
     */
    Node getNodeByIndex(int index);

    /**
     * Returns a map that maps nodes to indexes.
     *
     * @return a map that maps nodes to indexes.
     */
    Map<Node, Integer> getNodeIndex();

    /**
     * Returns the index of the given Node.
     *
     * @param node the Node to obtain the index of.
     * @return the index of the given Node.
     */
    int getIndexOfNode(Node node);

    /**
     * Returns a map that maps indexed Nodes to a BitSet that represents the preset of the indexed Node.
     *
     * @return a map that maps indexed Nodes to a BitSet that represents the preset of the indexed Node.
     */
    Map<Integer, BitSet> getPreBitSets();

    /**
     * Returns a map that maps indexed Nodes to a BitSet that represents the postset of the indexed Node.
     *
     * @return a map that maps indexed Nodes to a BitSet that represents the postset of the indexed Node.
     */
    Map<Integer, BitSet> getPostBitSets();

    /**
     * Returns a Set of Integers for which the BitSet holds true at that index.
     *
     * @param b a BitSet.
     * @return a Set of Integers for which the BitSet holds true at that index.
     */
    Set<Integer> asIndexes(BitSet b);

    /**
     * Returns a Set of Nodes that correspond to the bits that hold true of the given BitSet.
     *
     * @param b a BitSet.
     * @return a Set of Nodes that correspond to the bits that hold true of the given BitSet.
     */
    Set<Node> asNodes(BitSet b);
}
