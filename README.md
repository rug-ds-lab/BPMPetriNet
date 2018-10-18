## BPMPetriNet
The BPMPetriNet package provides a Petri net implementation for Business Process Modeling.

The package provides the following functionality:
* Several place/transition net implementations
* (Un)Marshalling place/transition nets (from) to pnml format
* Unfolding place/transition nets into an event structure
* Conditional evaluation of guards using expressions

### Structure
The package is structured as followed:

* eventstructure
* expression
* petrinet
  * interfaces
  * ptnet
  * ddnet
* pnml.ptnet
* util

### Usage
The package provides its core functionalities as follows:

```Java
PTNetUnmarshaller pnu = new PTNetUnmarshaller(file);            //Read the pnml file
Set<Net> pnset = pnu.getNets();                                 //Retrieve the different nets from the file
Net net = pnset.iterator().next();                              //Retrieve a pnml place/transition net

PlaceTransitionNet pn = new PlaceTransitionNet(net);            //Create the required net type from the pnml net

Place p1 = pn.addPlace("p1", "initial", 1);                     //Add the initial place with 1 token
Transition t1 = pn.addTransition("t1", "transition");           //Add a transition
Place p2 = pn.addPlace("p2", "sink");                           //Add the sink place

Arc a = pn.addArc("p1", "t1", 1);                               //Add an arc by name with weight 1
Arc a1 = pn.addArc(t1, p2);                                     //Add another arc with standard weight

Marking m = pn.getInitialMarking();                             //Retrieve the initial marking
Transition t = pn.getEnabledTransitions(m).iterator().next();   //Retrieve an enabled transition
Marking n = pn.fire(t, m);                                      //Fire the transition

PTNetMarshaller pnm = new PTNetMarshaller(pnset, file));        //Write the pnml file
```

### Implementing your own Petri net format
To use the unfolding functionality provided by this package and the verification functionality
provided by the [BPMVerification package](https://github.com/rug-ds-lab/BPMVerification) with
your custom net implementations, the following elements of the Petri net should implement a
number of interfaces:

Petri net:
* Unfolding
* TransitionGraph (optional, for verification)
* DataDrivenGraph (optional, for data driven verification)
Marking:
* M
* ConditionalM (optional, for conditional verification)
* DataM (optional, for data driven verification)
Place:
* P
Transition:
* T