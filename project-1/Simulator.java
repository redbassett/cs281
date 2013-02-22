// ===================================================================
// Simulator.java
// Scott F. H. Kaplan -- http://www.cs.amherst.edu/~sfkaplan
// September 2004
// ===================================================================



// ===================================================================
// IMPORTS

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
// ===================================================================



// ===================================================================
// Create the layers to connect two hosts, and then transmit from one
// to the other.
class Simulator {
// ===================================================================


    // ===============================================================
    // The entry point.  Set up the layers and start the simulation.
    public static void main (String[] args) {

	// Check the number of arguments passed.
	if (args.length != 2) {

	    System.err.println("Usage: java Simulator " +
			       "<medium type> " +
			       "<data link layer type> ");
	    System.exit(1);

	}

	// Assign names to the arguments.
	String mediumType = args[0];
	String dataLinkLayerType = args[1];

	// Create the medium (there is only one).
	Medium medium = createMedium(mediumType);

	// Create the physical layers.
	PhysicalLayer[] physicalLayers = createPhysicalLayers(medium);

	// Create the requested data link layers, connecting each one
	// to its physical layer.
	DataLinkLayer[] dataLinkLayers =
	    createDataLinkLayers(dataLinkLayerType, physicalLayers);

	// Create the requested network layers, connecting each one to
	// its data link layer.
	NetworkLayer[] networkLayers =
	    createNetworkLayers(dataLinkLayers);

	// Perform the simulation!
	simulate(networkLayers);

    } // main
    // ===============================================================



    // ===============================================================
    // Create the requested medium type and return it.
    protected static Medium createMedium (String mediumType) {

	// Look up the class by name.
	String className = mediumType + "Medium";
	Class mediumClass = null;
	try {
	    mediumClass = Class.forName(className);
	} catch (ClassNotFoundException e) {
	    throw new RuntimeException("Unknown medium subclass " +
				       className);
	}

	// Make a className object, and then see if it really is a
	// Medium subclass.
	Object o = null;
	try {
	    o = mediumClass.newInstance();
	} catch (InstantiationException e) {
	    throw new RuntimeException("Could not instantiate " +
				       className);
	} catch (IllegalAccessException e) {
	    throw new RuntimeException("Could not access " +
				       className);
	}
	Medium medium = null;
	try {
	    medium = (Medium)o;
	} catch (ClassCastException e) {
	    throw new RuntimeException(className +
				       " is not a subclass of Medium");
	}

	return medium;

    } // createMedium
    // ===============================================================



    // ===============================================================
    // Create the requested physical layer type for each of the two
    // hosts.
    protected static PhysicalLayer[]
	createPhysicalLayers (Medium medium) {

	PhysicalLayer[] physicalLayers = new PhysicalLayer[2];

	for (int i = 0; i < physicalLayers.length; i++) {

	    physicalLayers[i] = new PhysicalLayer(medium);

	}

	return physicalLayers;

    } // createPhysicalLayers
    // ===============================================================



    // ===============================================================
    // Create the requested data link layer type for each of the two
    // hosts.
    protected static DataLinkLayer[]
	createDataLinkLayers (String dataLinkType,
			      PhysicalLayer[] physicalLayers) {

	// Look up the class by name.
	String className = dataLinkType + "DataLinkLayer";
	Class<?> dataLinkClass = null;
	try {
	    dataLinkClass = Class.forName(className);
	} catch (ClassNotFoundException e) {
	    throw new RuntimeException("Unknown data link subclass" +
				       className);
	}

	// Create one data link layer for each physical layer already
	// created.
	DataLinkLayer[] dataLinkLayers =
	    new DataLinkLayer[physicalLayers.length];
	for (int i = 0; i < dataLinkLayers.length; i++) {

	    // Lookup the constructor needed to make a new data link
	    // layer.
	    Class<?>[] parameters = { PhysicalLayer.class };
	    Constructor dataLinkConstructor = null;
	    try {
		dataLinkConstructor =
		    dataLinkClass.getConstructor(parameters);
	    } catch (Exception e) {
		throw new RuntimeException("No (PhysicalLayer) constructor in " +
					   className);
	    }

	    // Make the requested kind of data link layer.
	    DataLinkLayer dataLinkLayer = null;
	    try {
		dataLinkLayers[i] =
		    (DataLinkLayer)dataLinkConstructor.newInstance(physicalLayers[i]);
	    } catch (ClassCastException e) {
		throw new RuntimeException(className +
					   " is not a subclass of DataLinkLayer");
	    } catch (IllegalAccessException e) {
		throw new RuntimeException("Cannot access " + className);
	    } catch (InstantiationException e) {
		throw new RuntimeException("Cannot instantiate " + className);
	    } catch (InvocationTargetException e) {
		throw new RuntimeException("Cannot invoke constructor for " +
					   className);
	    }

	}

	return dataLinkLayers;

    } // createDataLinkLayer
    // ===============================================================



    // ===============================================================
    // Create the requested data link layer type for each of the two
    // hosts.
    protected static NetworkLayer[]
	createNetworkLayers (DataLinkLayer[] dataLinkLayers) {

	NetworkLayer[] networkLayers =
	    new NetworkLayer[dataLinkLayers.length];

	for (int i = 0; i < networkLayers.length; i++) {

	    networkLayers[i] = new NetworkLayer(dataLinkLayers[i]);

	}

	return networkLayers;

    } // createNetworkLayers
    // ===============================================================



    // ===============================================================
    // Perform the simulation by selecting the 0th host as the
    // ``sender'' and the 1st as the ``receiver'', causing the former
    // to send messages to the latter.
    protected static void simulate (NetworkLayer[] networkLayers) {

        // Initiate the sender.
	    networkLayers[0].send();

    }
    // ===============================================================



// ===================================================================
} // class Simulator
// ===================================================================
