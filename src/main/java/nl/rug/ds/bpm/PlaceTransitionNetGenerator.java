package nl.rug.ds.bpm;

import nl.rug.ds.bpm.petrinet.ptnet.PlaceTransitionNet;
import nl.rug.ds.bpm.petrinet.ptnet.element.Place;
import nl.rug.ds.bpm.petrinet.ptnet.element.Transition;
import nl.rug.ds.bpm.pnml.ptnet.jaxb.ptnet.Net;
import nl.rug.ds.bpm.pnml.ptnet.marshaller.PTNetMarshaller;
import nl.rug.ds.bpm.util.exception.MalformedNetException;
import nl.rug.ds.bpm.util.log.LogEvent;
import nl.rug.ds.bpm.util.log.Logger;
import nl.rug.ds.bpm.util.log.listener.VerificationLogListener;
import org.apache.commons.cli.*;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class PlaceTransitionNetGenerator implements VerificationLogListener {

    /**
     * Creates a PlaceTransitionNetGenerator.
     *
     * @param args the command line arguments used.
     */
    public PlaceTransitionNetGenerator(String[] args) {
        Logger.addLogListener(this);

        // Apache Commons CLI options
        Options options = new Options();


        Option batchOption = new Option("m", "mode", false, "batch mode, generates a preset number of nets with parallel and exclusive branching for all sizes up to, and including, the set width and depth");
        batchOption.setRequired(false);
        options.addOption(batchOption);

        Option fileOption = new Option("f", "file", true, "the pnml file name for the generated net, default \"net.pnml\"");
        fileOption.setRequired(false);
        options.addOption(fileOption);

        Option outputOption = new Option("o", "output", true, "the output directory path, default \"./\"");
        outputOption.setRequired(false);
        options.addOption(outputOption);

        Option branchOption = new Option("b", "branching", true, "the type of branching to be generated, either exclusive (default) or parallel");
        branchOption.setRequired(false);
        options.addOption(branchOption);

        Option depthOption = new Option("d", "depth", true, "the depth of each branch to be included in the generated net, default 10");
        depthOption.setRequired(false);
        options.addOption(depthOption);

        Option widthOption = new Option("w", "width", true, "the number of branches to be included in the generated net, default 1");
        widthOption.setRequired(false);
        options.addOption(widthOption);

        Option prefixOption = new Option("p", "prefix", true, "the naming prefix to use for transitions");
        prefixOption.setRequired(false);
        options.addOption(prefixOption);

        Option logOption = new Option("l", "log", true, "the log level, either critical, error, warning, info (default), verbose, or debug");
        logOption.setRequired(false);
        options.addOption(logOption);

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);

            boolean batch = cmd.hasOption(batchOption);
            String filename = cmd.getOptionValue(fileOption);
            String outputPath = cmd.getOptionValue(outputOption);
            String branching = cmd.getOptionValue(branchOption);
            String prefix = cmd.getOptionValue(prefixOption);
            int width = Integer.parseInt(cmd.getOptionValue(widthOption));
            int depth = Integer.parseInt(cmd.getOptionValue(depthOption));
            String logLevel = cmd.getOptionValue("log");

            setLogLevel(logLevel);

            if (batch) {
                generateBatch(outputPath, width, depth, prefix);
            }
            else {
                generateSingle(filename, outputPath, branching, width, depth, prefix);
            }
        } catch (Exception e) {
            Logger.log(e.getMessage(), LogEvent.ERROR);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("CommandlineVerifier", options);

            System.exit(1);
        }
    }

    /**
     * Generates a number of nets, up to and including the given parameters.
     *
     * @param outputPath the path to save the file to.
     * @param width the number of branches.
     * @param depth the depth/length of each branch.
     * @param prefix the naming prefix to use for transitions.
     * @throws MalformedNetException when the net fails to be created.
     */
    private void generateBatch(String outputPath, int width, int depth, String prefix) throws MalformedNetException {
        for (int i = 1; i <= width; i++) {
            for (int j = 1; j <= depth; j++) {
                generateSingle("exclnet" + i + "x" + j + ".pnml", outputPath, "exclusive", i, j, prefix);
                generateSingle("parnet" + i + "x" + j + ".pnml", outputPath, "parallel", i, j, prefix);
            }
        }
    }

    /**
     * Generates a single net with the given parameters.
     *
     * @param filename the file name to use.
     * @param outputPath the path to save the file to.
     * @param branching the type of branching, either exclusive or parallel.
     * @param width the number of branches.
     * @param depth the depth/length of each branch.
     * @param prefix the naming prefix to use for transitions.
     * @throws MalformedNetException when the net fails to be created.
     */
    private void generateSingle(String filename, String outputPath, String branching, int width, int depth, String prefix) throws MalformedNetException {
        PlaceTransitionNet net = generateNet(branching, width, depth, prefix);
        saveNet(net, filename, outputPath);
    }


    /**
     * Saves the given net as a pnml file in the given path and with the given file name.
     *
     * @param net the net to save to disk.
     * @param filename the file name to use.
     * @param outputPath the path to save the file to.
     */
    private void saveNet(PlaceTransitionNet net, String filename, String outputPath) {
        File file = new File(outputPath, filename);

        Set<Net> nets = new HashSet<>();
        nets.add((Net) net.getXmlElement());

        PTNetMarshaller marshaller = new PTNetMarshaller(nets, file);
    }

    public static void main(String[] args) {    PlaceTransitionNetGenerator placeTransitionNetGenerator = new PlaceTransitionNetGenerator(args);    }

    /**
     * Creates a PlaceTransitionNet with the given type of branching, dimensions, and naming prefix.
     *
     * @param branching the type of branching, either exclusive or parallel.
     * @param width the number of branches.
     * @param depth the depth/length of each branch.
     * @param prefix the naming prefix to use for transitions.
     * @return a PlaceTransitionNet.
     */
    private PlaceTransitionNet generateNet(String branching, int width, int depth, String prefix) throws MalformedNetException {
        boolean parallel = (branching != null && (branching.equalsIgnoreCase("parallel")));
        int w = (width > 0 ? width : 1);
        int d = (depth > 0 ? depth : 10);
        String pre = (prefix == null || prefix.equalsIgnoreCase("p") ? "t" : prefix);

        PlaceTransitionNet net = new PlaceTransitionNet(prefix + "_net", prefix + "_net");

        Place initial = net.addPlace("initial", 1);
        Place sink = net.addPlace("sink", "sink");

        Transition start = net.addTransition("start", pre + "_start");
        Transition end = net.addTransition("end", pre + "_end");

        net.addArc(initial, start);
        net.addArc(end, sink);

        if (parallel) {
            for (int i = 0; i < width; i++) {
                addBranchBetweenTransitions(net, start, end, d, pre);
            }
        }
        else {
            Place post_start = net.addPlace("post_start", "post_start");
            Place pre_end = net.addPlace("pre_end", "pre_end");

            net.addArc(start, post_start);
            net.addArc(pre_end, end);

            for (int i = 0; i < width; i++) {
                addBranchBetweenPlaces(net, post_start, pre_end, d, pre);
            }
        }

        return net;
    }

    /**
     * Adds a branch of a number of d transitions between the start and end transitions of the given net.
     *
     * @param net the net to add the branch to.
     * @param start the transition from which the branch originates.
     * @param end the transition where the branch leads to.
     * @param d the depth of the branch (number of place-transition pairs).
     * @param pre the naming prefix of transitions.
     * @throws MalformedNetException when elements in the branch fail to be created.
     */
    private void addBranchBetweenTransitions(PlaceTransitionNet net, Transition start, Transition end, int d, String pre) throws MalformedNetException {
        Place post_start = net.addPlace("p" + net.getPlaces().size(), "p" + net.getPlaces().size());
        Place pre_end = net.addPlace("p" + net.getPlaces().size(), "p" + net.getPlaces().size());

        net.addArc(start, post_start);
        net.addArc(pre_end, end);

        addBranchBetweenPlaces(net, post_start, pre_end, d, pre);
    }

    /**
     * Adds a branch of a number of d transitions between the start and end places of the given net.
     *
     * @param net the net to add the branch to.
     * @param start the place from which the branch originates.
     * @param end the place where the branch leads to.
     * @param d the depth of the branch (number of place-transition pairs).
     * @param pre the naming prefix of transitions.
     * @throws MalformedNetException when elements in the branch fail to be created.
     */
    private void addBranchBetweenPlaces(PlaceTransitionNet net, Place start, Place end, int d, String pre) throws MalformedNetException {
        Place prev = start;
        for (int i = 0; i < d; i++) {
            Transition t = net.addTransition("t" + net.getTransitions().size(), pre + net.getTransitions().size());
            net.addArc(prev, t);

            if (i < d - 1) {
                Place post = net.addPlace("p" + net.getPlaces().size(), "p" + net.getPlaces().size());
                net.addArc(t, post);
                prev = post;
            }
            else {
                net.addArc(t, end);
            }
        }
    }

    /**
     * Sets the log level to the given level.
     *
     * @param level the given level.
     */
    public void setLogLevel(String level) {
        if (level != null) {
            switch (level.toLowerCase()) {
                case "critical" -> Logger.setLogLevel(LogEvent.CRITICAL);
                case "error" -> Logger.setLogLevel(LogEvent.ERROR);
                case "warning" -> Logger.setLogLevel(LogEvent.WARNING);
                case "info" -> Logger.setLogLevel(LogEvent.INFO);
                case "verbose" -> Logger.setLogLevel(LogEvent.VERBOSE);
                case "debug" -> Logger.setLogLevel(LogEvent.DEBUG);
                default -> Logger.setLogLevel(LogEvent.INFO);
            }
        } else {
            Logger.setLogLevel(LogEvent.INFO);
        }
    }

    /**
     * Listener for log events.
     *
     * @param event a logged event.
     */
    @Override
    public void verificationLogEvent(LogEvent event) {
        //Use for log and textual user feedback
        System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "] " + event.toString());
    }
}
