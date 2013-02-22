// ===================================================================
// PerfectMedium
// Scott F. H. Kaplan -- http://www.cs.amherst.edu/~sfkaplan
// September 2004
// ===================================================================



// ===================================================================
// A point-to-point medium that, like a hypothetical ``perfect wire'',
// introduces no error.
class PerfectMedium extends Medium {
// ===================================================================



    // ===============================================================
    // PUBLIC METHODS
    // ===============================================================



    // ===============================================================
    // Register one of the two allowed clients as connected to an end
    // of the medium.
    public void register (PhysicalLayer client) {

	// If there is an end of the wire available, then assign this
	// client to it.
	if (client1 == null) {

	    client1 = client;

	} else if (client2 == null) {

	    client2 = client;

	} else {

	    throw new RuntimeException();

	}

    } // register
    // ===============================================================



    // ===============================================================
    // Allow a client to send a bit to the other client.
    public void send (PhysicalLayer sender, boolean bit) {

	// Determine who the receiver is.  Send only if the sender is
	// a known client.
	PhysicalLayer receiver = null;
	if (client1 == sender) {

	    receiver = client2;

	} else if (client2 == sender) {

	    receiver = client1;

	} else {

	    throw new RuntimeException();

	}

	// Deliver the bit to the receiver by performing an upcall to
	// it.
	receiver.receive(bit);

    } // send
    // ===============================================================



    // ===============================================================
    // DATA MEMBERS
    // ===============================================================


    // ===============================================================
    // The two physical layer clients on either end of the wire.
    PhysicalLayer client1;
    PhysicalLayer client2;
    // ===============================================================



// ===================================================================
} // class PointToPointPhysicalLayer
// ===================================================================
