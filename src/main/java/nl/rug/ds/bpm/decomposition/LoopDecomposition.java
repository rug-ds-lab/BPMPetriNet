package nl.rug.ds.bpm.decomposition;

import nl.rug.ds.bpm.petrinet.ptnet.OneSafeNet;
import nl.rug.ds.bpm.petrinet.ptnet.element.Arc;
import nl.rug.ds.bpm.petrinet.ptnet.element.Node;
import nl.rug.ds.bpm.petrinet.ptnet.element.Place;
import nl.rug.ds.bpm.petrinet.ptnet.element.Transition;
import nl.rug.ds.bpm.util.exception.MalformedNetException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class LoopDecomposition.
 *
 * <p>Decomposes cyclic process models into sets of acyclic process models interacting with signals. You can find
 * more information in the following papers:</p>
 *
 * <cite>Thomas M. Prinz, N. Long Ha, Yongsun Choi:
 * Transformation of Cyclic Process Models with Inclusive Gateways to Be Executable on State-of-the-Art Engines.
 * ICEIS (2) 2025: 280-291. DOI: <a href="https://doi.org/10.5220/0013386400003929">...</a></cite>
 *
 * <cite>Thomas M. Prinz, Yongsun Choi, N. Long Ha:
 * Soundness unknotted: An efficient soundness checking algorithm for arbitrary cyclic process models by loosening loops.
 * Inf. Syst. 128: 102476 (2025). DOI: <a href="https://doi.org/10.1016/j.is.2024.102476">...</a></cite>
 *
 * @author Thomas M. Prinz
 * @version 1.0.0
 */
class LoopDecomposition {

    /**
     * Uniquely detected loops.
     */
    private final Map<String,OneSafeNet> uniqueLoops = new HashMap<>();

    /**
     * All derived acyclic process models.
     */
    private final Collection<OneSafeNet> acyclicProcesses = new HashSet<>();

    /**
     * Decompose all given process models into acyclic, interacting process models.
     * @param net The process model(s) to decompose.
     * @return Set of acyclic nets.
     */
    public Collection<OneSafeNet> decompose(OneSafeNet net) throws MalformedNetException {
        this.acyclicProcesses.addAll(this.decomposeNet(net));

        return this.acyclicProcesses;
    }

    /**
     * Decompose (sometimes recursively) a net.
     * @param net The net to decompose.
     * @return Set of decomposed nets.
     */
    private Collection<OneSafeNet> decomposeNet(OneSafeNet net) throws MalformedNetException {
        // Detect the loops.
        Collection<Loop> loops = (new StronglyConnectedComponents(net)).findLoops();
        Collection<OneSafeNet> fragments = new HashSet<>();
        if (loops.isEmpty()) {
            // The net is already acyclic and needs no decomposition.
            fragments.add(net);
            return fragments;
        } else {
            // Decompose the loops.
            fragments = this.decomposeLoops(net, loops);
            // Since the resulting fragments may still be cyclic, we have to redo the procedure until no loop is
            // left.
            Collection<OneSafeNet> allFragments = new HashSet<>();
            for (OneSafeNet fragment: fragments) {
                allFragments.addAll(this.decomposeNet(fragment));
            }
            return allFragments;
        }
    }

    /**
     * Decompose the loops in the net.
     * @param net The net to decompose.
     * @param loops The information about the detected loops.
     * @return Set of decomposed nets.
     */
    private Collection<OneSafeNet> decomposeLoops(OneSafeNet net, Collection<Loop> loops) throws MalformedNetException {
        // Create a copy of the main net.
        OneSafeNet mainNet = this.copyNet(net);

        Set<OneSafeNet> nets = new HashSet<>();
        nets.add(mainNet);

        // Decompose each loop
        for (Loop loop: loops) {
            // Each loop is uniquely identified by any (or all) of its exits.
            String loopIdentifier = loop.getExits().stream().map(Node::getId).sorted().collect(Collectors.joining());
            boolean createNew = false;

            OneSafeNet loopNet;
            // Is there already a process model for it?
            if (!this.uniqueLoops.containsKey(loopIdentifier)) {
                // Create a new process model for the loop.
                loopNet = new OneSafeNet();
                loopNet.setId(loopIdentifier);
                nets.add(loopNet);
                this.uniqueLoops.put(loopIdentifier, loopNet);

                // The new net contains each node of the loop.
                for (Node node: loop.getComponents()) {
                    if (node instanceof Place) loopNet.addPlace((Place) node);
                    else loopNet.addTransition((Transition) node);
                }
                createNew = true;
            } else loopNet = this.uniqueLoops.get(loopIdentifier);

            // The later entries to the loop (after conversion) are those exits being
            // in the do-body.
            Set<Node> realEntries = loop.getExits().stream().filter(ex -> loop.getDoBody().contains(ex)).collect(Collectors.toSet());
            Set<Node> netExits = loop.getExits();

            //
            // Remove the (non-do-body of the) loop and replace it with a place.
            //

            // Remove all nodes of the loop being not in its do-body (inclusivey the exits).
            Set<Node> nonDoBody = loop.getComponents().stream().filter(node -> !loop.getDoBody().contains(node)).collect(Collectors.toSet());

            // Eliminate the arcs and nodes in the non-do-body from the main net.
            List<Arc> toEliminate = new ArrayList<>();
            for (Arc arc: mainNet.getArcs()) {
                if (nonDoBody.contains(arc.getSource()) || nonDoBody.contains(arc.getTarget())) {
                    toEliminate.add(arc);
                }
            }
            for (Arc arc: toEliminate) mainNet.removeArc(arc);
            for (Node node: nonDoBody) {
                if (node instanceof Place) mainNet.removePlace((Place) node);
                else mainNet.removeTransition((Transition) node);
            }

            //
            // Remove the arcs of the loop from the main net and add them to the loop net.
            //
            // There are too much arcs in the net and not enough in the loop net.
            for (Arc arc: toEliminate) {
                Node source = arc.getSource();
                Node target = arc.getTarget();

                if (createNew && ((loop.getComponents().contains(source) ||
                        loop.getComponents().contains(target)))) {
                    // The flow is connected to the loop
                    // There are variants:
                    // 1. The flow has a loop exit as target
                    if (loop.getExits().contains(target)) {
                        // Regarding loop decomposition, the loop is broken up at the
                        // incoming arcs of the loop exit and replaced with a place to start
                        // and a place to end.

                        // Insert the new starting place
                        Place start = new Place(UUID.randomUUID().toString());
                        loopNet.addPlace(start);
                        // Insert an arc between the new starting place and the target.
                        loopNet.addArc(new Arc(UUID.randomUUID().toString(), start, target));

                        // Usually, the loop contains the sources of each exit.
                        // However, there is a special case, in which the entry is the exit at the same moment.
                        if (loop.getComponents().contains(source)) {
                            // Since we broke up the loop at the current arc, we also have to
                            // insert an ending place.
                            Place end = new Place(UUID.randomUUID().toString());
                            loopNet.addPlace(end);

                            // Connect this ending place with the source.
                            loopNet.addArc(new Arc(UUID.randomUUID().toString(), source, end));
                        }
                    } else if (loop.getExits().contains(source) && !loop.getComponents().contains(target)) {
                        // b. It is a loop-exit arc (going from an exit to a node outside the loop).
                        // Thus, we have to insert a new ending silent transition and place.
                        Transition silent = new Transition(UUID.randomUUID().toString());
                        Place end = new Place(UUID.randomUUID().toString());
                        loopNet.addPlace(end);
                        loopNet.addTransition(silent);

                        // Connect the new transition with the loop exit.
                        loopNet.addArc(new Arc(UUID.randomUUID().toString(), source, silent));
                        loopNet.addArc(new Arc(UUID.randomUUID().toString(), silent, end));

                        // We do not need the arc in the loop net, so we do not add it.
                    } else {
                        // c. The arc is an inner arc, add it.
                        if (loop.getComponents().contains(source) && loop.getComponents().contains(target)) {
                            loopNet.addArc(new Arc(UUID.randomUUID().toString(), source, target));
                        }
                    }
                }
            }
        }

        return nets;
    }

    /**
     * Copy the net.
     * @param net The net to copy.
     * @return OneSafeNet
     */
    private OneSafeNet copyNet(OneSafeNet net) throws MalformedNetException {
        OneSafeNet copy = new OneSafeNet();
        // We do not have to copy nodes since they do not contain any information (e.g., presets, postsets, etc.)
        for (Node org: net.getNodeIndex().keySet()) {
            if (org instanceof Place) copy.addPlace((Place) org);
            else copy.addTransition((Transition) org);
        }
        net.getArcs().forEach(arc -> {
            try {
                copy.addArc(new Arc(arc.getId(), arc.getSource(), arc.getTarget()));
            } catch (MalformedNetException e) {
                throw new RuntimeException(e);
            }
        });
        return copy;
    }
}