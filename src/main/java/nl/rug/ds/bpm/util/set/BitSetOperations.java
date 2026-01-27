package nl.rug.ds.bpm.util.set;

import java.util.BitSet;

/**
 *  Class that implements static set operations on BitSets.
 */
public class BitSetOperations {

    /**
     * Returns a new BitSet that is the union, or logical or, of b1 and b2. An argument of null is treated as a BitSet with all bits set to false.
     *
     * @param b1 a BitSet.
     * @param b2 a BitSet
     * @return a new BitSet that is the union of b1 and b2.
     */
    public static BitSet union(BitSet b1, BitSet b2) {
        BitSet b = new BitSet();

        if (b1 != null)
            b.or(b1);
        if (b2 != null)
            b.or(b2);

        return b;
    }

    /**
     * Returns a new BitSet that is the intersection, or logical and, of b1 and b2. An argument of null is treated as a BitSet with all bits set to false.
     *
     * @param b1 a BitSet.
     * @param b2 a BitSet
     * @return a new BitSet that is the intersection of b1 and b2.
     */
    public static BitSet intersection(BitSet b1, BitSet b2) {
        BitSet b = new BitSet();

        if (b1 != null && b2 != null) {
            b.or(b1);
            b.and(b2);
        }

        return b;
    }

    /**
     * Returns a new BitSet that is the difference, or logical xor, of b1 and b2. An argument of null is treated as a BitSet with all bits set to false.
     *
     * @param b1 a BitSet.
     * @param b2 a BitSet
     * @return a new BitSet that is the difference of b1 and b2.
     */
    public static BitSet difference(BitSet b1, BitSet b2) {
        BitSet b = new BitSet();

        if (b1 != null)
            b.or(b1);
        if (b2 != null)
            b.xor(b2);
        return b;
    }
}
