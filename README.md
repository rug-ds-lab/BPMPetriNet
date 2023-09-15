## BPMPetriNet
The BPMPetriNet package provides a Petri net implementation for Business Process Modeling.

The package provides the following functionality:
* Several place/transition net implementations
  * Place/transition net
  * Data-driven net
* (Un)Marshalling place/transition nets (from) to [pnml](http://www.pnml.org/) format
* Unfolding place/transition nets into an event structure
* Conditional evaluation of guards using expressions
* Commandline tool that generates place/transition net of given sizes

### Structure
The package is structured as followed:

* eventstructure
* expression
* petrinet
  * interfaces
    * element
    * marking
    * net
  * ptnet
    * element
    * marking
  * ddnet
    * marking
* pnml.ptnet
  * jaxb
  * marshaller
* util

### Usage
The package provides its core functionalities as follows:

```java
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

### Toolspecific BPM extensions of the pnml file format
The package extends the pnml format with two BPM specific xml blocks.

**1) Process block**

The process block is part of a net and allows for the specification of global process information on:
* groups,
* roles, and
* variables. 

**2) Task block**

The task block is part of a transition and allows for the specification of:
* parent (i.e., the id of the group to which it may belong),
* actor (i.e., the id of the role performing the task),
* unitsoftime (i.e., an abstract unit of time it may take to fire the transition),
* guard (i.e., the Boolean expression that must hold to fire the transition),
* isTau (i.e., whether the transition is silent or not),
* subProcess (i.e., the id of a net that contains the sub-process linked to the transition), and
* script (i.e., a piece of JavaScript that simulates some execution of the transition for data-driven nets).

### Implementing your own Petri net format
To use the unfolding functionality provided by this package and the verification functionality
provided by the [BPMVerification](https://github.com/rug-ds-lab/BPMVerification) package with
your custom net implementations, the following elements of the Petri net should implement a
number of interfaces provided in nl.rug.ds.bpm.petrinet.interfaces.

Petri net:
* UnfoldableNet (for unfolding support)
* VerifiableNet (for verification support)
* VerifiableDataNet (for data driven verification support)

Marking:
* MarkingI
* ConditionalMarkingI (for conditional verification support)
* DataMarkingI (for data driven verification support)

Net elements:
* NodeI (superclass of place and transition)
* PlaceI
* TransitionI
* ArcI