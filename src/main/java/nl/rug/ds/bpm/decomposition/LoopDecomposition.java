package nl.rug.ds.bpm.decomposition;

import nl.rug.ds.bpm.petrinet.ptnet.OneSafeNet;
import nl.rug.ds.bpm.petrinet.ptnet.element.Arc;
import nl.rug.ds.bpm.petrinet.ptnet.element.Node;
import nl.rug.ds.bpm.petrinet.ptnet.element.Place;
import nl.rug.ds.bpm.petrinet.ptnet.element.Transition;
import nl.rug.ds.bpm.petrinet.ptnet.marking.Marking;
import nl.rug.ds.bpm.util.exception.IllegalMarkingException;
import nl.rug.ds.bpm.util.exception.MalformedNetException;
import nl.rug.ds.bpm.util.log.LogEvent;
import nl.rug.ds.bpm.util.log.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class LoopDecomposition.
 *
 * <p>Decomposes cyclic (in a graph-sense) nets ({@link OneSafeNet}) into sets of acyclic nets.</p>
 *
 * <code>
 * Collection<OneSafeNet> (new LoopDecomposition())->decompose(net);
 * </code>
 *
 * <p>You can find more information in the following papers:</p>
 *
 * <p><cite>Thomas M. Prinz, N. Long Ha, Yongsun Choi:
 * Transformation of Cyclic Process Models with Inclusive Gateways to Be Executable on State-of-the-Art Engines.
 * ICEIS (2) 2025: 280-291. DOI: <a href="https://doi.org/10.5220/0013386400003929">...</a></cite></p>
 *
 * <p><cite>Thomas M. Prinz, Yongsun Choi, N. Long Ha:
 * Soundness unknotted: An efficient soundness checking algorithm for arbitrary cyclic process models by loosening loops.
 * Inf. Syst. 128: 102476 (2025). DOI: <a href="https://doi.org/10.1016/j.is.2024.102476">...</a></cite></p>
 *
 * @author Thomas M. Prinz
 * @version 1.1.0
 */
public class LoopDecomposition {

    /**
     * For debugging purposes.
     */
    public static final boolean VERBOSE = false;

    /**
     * Uniquely detected loops.
     */
    private final Map<String,NetTemplate> uniqueLoops = new HashMap<>();

    /**
     * Decompose the given net into acyclic nets.
     * @param net The net to decompose.
     * @return Set of acyclic nets.
     */
    public OneSafeNet decompose(OneSafeNet net) throws MalformedNetException, IllegalMarkingException {
        NetTemplate root = this.decomposeNet(net);

        Collection<Place> starts = root.getStarts();
        if (!starts.isEmpty()) {
            return root.instance(starts.iterator().next());
        }

        return null;
    }

    /**
     * Decompose (sometimes recursively) a net.
     * @param net The net to decompose.
     * @return Set of decomposed nets.
     */
    private NetTemplate decomposeNet(OneSafeNet net) throws MalformedNetException, IllegalMarkingException {
        if (LoopDecomposition.VERBOSE) {
            Logger.log("Start to decompose net", LogEvent.INFO);
            Logger.log(net.asDotGraph(), LogEvent.INFO);
        }
        NetTemplate template = NetTemplate.asNetTemplate(net);
        return this.decomposeNet(template);
    }

    private NetTemplate decomposeNet(NetTemplate net) {
        if (LoopDecomposition.VERBOSE) {
            Logger.log("Start to decompose net", LogEvent.INFO);
            Logger.log(net.asDotGraph(), LogEvent.INFO);
        }

        // Detect the loops.
        Collection<Loop> loops = (new StronglyConnectedComponents(net)).findLoops();
        Collection<NetTemplate> fragments;
        if (loops.isEmpty()) {
            // The net is already acyclic and needs no decomposition.
            return net;
        } else {
            // Decompose the loops.
            fragments = this.decomposeLoops(net, loops);
            // Since the resulting fragments may still be cyclic, we have to redo the procedure until no loop is
            // left.
            NetTemplate main = null;
            for (Iterator<NetTemplate> it = fragments.iterator(); it.hasNext(); ) {
                NetTemplate fragment = it.next();
                NetTemplate root = this.decomposeNet(fragment);
                if (fragment.getParent().equals(net)) {
                    main = root;
                }
            }
            return main;
        }
    }

    /**
     * Decompose the loops in the net.
     * @param net The net to decompose.
     * @param loops The information about the detected loops.
     * @return Set of decomposed nets.
     */
    private Collection<NetTemplate> decomposeLoops(NetTemplate net, Collection<Loop> loops) {
        // Create a template of the main net.
        NetTemplate mainNet = NetTemplate.asNetTemplate(net);

        // Collect the derived nets.
        Collection<NetTemplate> nets = new HashSet<>();
        nets.add(mainNet);

        // Decompose each loop
        for (Loop loop: loops) {
            // Each loop is uniquely identified by any (or all) of its exits.
            String loopIdentifier = loop.getExits().stream().map(Node::getId).sorted().collect(Collectors.joining());
            boolean createNew = false;

            NetTemplate loopNet;
            // Is there already a process model for it?
            if (!this.uniqueLoops.containsKey(loopIdentifier)) {
                // Create a new process model for the loop.
                loopNet = new NetTemplate(mainNet, loopIdentifier, loop, loop.getComponents());
                this.uniqueLoops.put(loopIdentifier, loopNet);
                nets.add(loopNet);
                createNew = true;
            } else loopNet = this.uniqueLoops.get(loopIdentifier);

            // Add arcs and some new places and transitions to the loop net.
            //
            // Remove the arcs of the loop from the main net and add them to the loop net.
            //
            // There are too much arcs in the net and not enough in the loop net.
            for (Arc arc: mainNet.getArcs()) {
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

                        // We do not have to insert a start place since the exit is already one.
                        loopNet.addStart((Place) target);

                        // Usually, the loop contains the sources of each exit.
                        // However, there is a special case, in which the entry is the exit at the same moment.
                        if (loop.getComponents().contains(source)) {
                            // Since we broke up the loop at the current arc, we also have to
                            // insert an ending place.
                            Place end = new Place(UUID.randomUUID().toString());
                            loopNet.addEnd(end);

                            // Connect this ending place with the source.
                            loopNet.addArc(new Arc(UUID.randomUUID().toString(), source, end));

                            // Add a relation that the end will be going back to iterated the loop.
                            loopNet.addIterationArc(end, (Place) target);
                        }
                        // Store relations between the arc and the "starting" place of the loop net.
                        if (loop.getDoBody().contains(source) || !loop.getComponents().contains(source)) {
                            mainNet.addDownArc(arc, (Place) target, loopNet);
                        }
                    } else if (loop.getExits().contains(source) && !loop.getComponents().contains(target)) {
                        // b. It is a loop-exit arc (going from an exit to a node outside the loop).
                        // Thus, we have to insert a new ending silent transition and place.
                        Transition silent = new Transition(UUID.randomUUID().toString());
                        silent.setTau(true);
                        Place end = new Place(UUID.randomUUID().toString());
                        loopNet.addEnd(end);
                        loopNet.addNode(silent);

                        // Connect the new transition with the loop exit.
                        loopNet.addArc(new Arc(UUID.randomUUID().toString(), source, silent));
                        loopNet.addArc(new Arc(UUID.randomUUID().toString(), silent, end));

                        // Store the link between the end and the arc.
                        loopNet.addUpArc(end, arc);

                        // We do not need the arc in the loop net, so we do not add it.
                    } else {
                        // c. The arc is an inner arc, add it.
                        if (loop.getComponents().contains(source) && loop.getComponents().contains(target)) {
                            loopNet.addArc(new Arc(UUID.randomUUID().toString(), source, target));
                        }
                    }
                }
            }

            // The later entries to the loop (after conversion) are those exits being
            // in the do-body.
            List<Node> doBodyExits = loop.getExits().stream().filter(ex -> loop.getDoBody().contains(ex)).toList();

            //
            // Remove the (non-do-body of the) loop and replace it with a place.
            //

            // Determine the non-do-body.
            Set<Node> nonDoBody = loop.getComponents().stream().filter(node -> !loop.getDoBody().contains(node)).collect(Collectors.toSet());

            if (LoopDecomposition.VERBOSE) {
                Logger.log("loop", LogEvent.INFO);
                Logger.log(loop.getComponents().stream().map(Node::getId).collect(Collectors.toSet()).toString(), LogEvent.INFO);
                Logger.log("do-body", LogEvent.INFO);
                Logger.log(loop.getDoBody().stream().map(Node::getId).collect(Collectors.toSet()).toString(), LogEvent.INFO);
                Logger.log("non-do-body", LogEvent.INFO);
                Logger.log(nonDoBody.stream().map(Node::getId).collect(Collectors.toSet()).toString(), LogEvent.INFO);
            }

            // Handle the loop-exit arcs
            Set<Node> eliminateNodes = new HashSet<>();
            Set<Arc> eliminateArcs = new HashSet<>();
            // We insert a new loop place
            Place loopPlace = new Place(UUID.randomUUID().toString());
            loopPlace.setName(loopIdentifier);
            mainNet.addNode(loopPlace);
            // Store a link from the place to the corresponding loop net.
            mainNet.addLoopLink(loopPlace, loopNet);

            // We redirect all incoming arcs of do-body exits to this loop place
            for (Node doBodyExit: doBodyExits) {
                for (Arc arc: mainNet.getIncoming(doBodyExit)) {
                    Arc redirected = new Arc(arc.getId() + "-c", arc.getSource(), loopPlace);
                    mainNet.addArc(redirected);
                    eliminateArcs.add(arc);
                    mainNet.replaceDownArc(arc, redirected);
                }
            }
            eliminateNodes.addAll(doBodyExits);
            for (Node exit: loop.getExits()) {
                // We redirect all loop-outgoing arcs starting in this point
                for (Arc arc: mainNet.getOutgoing(exit)) {
                    if (!loop.getComponents().contains(arc.getTarget())) {
                        arc.setSource(loopPlace);
                        Arc redirected = new Arc(arc.getId() + "-c", loopPlace, arc.getTarget());
                        mainNet.addArc(redirected);
                        eliminateArcs.add(arc);
                        loopNet.replaceUpArc(arc, redirected);
                    }
                }
            }

            // Eliminate the arcs and nodes in the non-do-body from the main net.
            for (Arc arc: mainNet.getArcs()) {
                if (nonDoBody.contains(arc.getSource()) || nonDoBody.contains(arc.getTarget())) {
                    eliminateArcs.add(arc);
                }
            }
            eliminateNodes.addAll(nonDoBody);
            for (Arc arc: eliminateArcs) mainNet.removeArc(arc);
            for (Node node: eliminateNodes) {
                mainNet.removeNode(node);
            }

            if (LoopDecomposition.VERBOSE) {
                Logger.log("Reduced net", LogEvent.INFO);
                Logger.log(mainNet.asDotGraph(), LogEvent.INFO);
                Logger.log("Resulted loop net", LogEvent.INFO);
                Logger.log(loopNet.asDotGraph(), LogEvent.INFO);
            }

        }

        return nets;
    }
}