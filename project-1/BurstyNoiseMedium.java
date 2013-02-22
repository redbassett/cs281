// ===================================================================
// BurstyNoiseMedium
// Scott F. H. Kaplan -- http://www.cs.amherst.edu/~sfkaplan
// September 2004
// ===================================================================



// ===================================================================
// A point-to-point medium that, with low probability, will flip some
// number of bits within a given sequence.
class BurstyNoiseMedium extends Medium {
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

	// Are we currently in burst mode, or should we randomly enter
	// it?  Note that if we just exited burst mode, we cannot
	// re-enter it for at least one bit transmission.
	if ((burstCount > 0) ||
	    (Math.random() < burstProbability) && (burstCount != -1)) {

	    // We're in burst mode. Advance the count of bits that
	    // could contribute to the burst and, with a given
	    // probability, flip this bit.
	    burstCount++;
	    if (Math.random() < errorProbability) {

		bit = !bit;

	    }

	    // Have we reached the maximum length for this burst?
	    if (burstCount >= maxBurstLength) {

		// We have.  End the burst by setting the count to -1,
		// thus marking the burst as having just ended.
		burstCount = -1;

	    }

	} else {

	    // We are not in burst mode.  This bit will be sent
	    // normally.  Thus, we can make the wire eligible to burst
	    // another error on the next bit.
	    burstCount = 0;

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

    // The probablity that a burst will occur, for what length, and
    // with what probability of flipping the bits.  Finally, a counter
    // to keep track of the number of bits since we entered burst
    // error mode.  Thus, the number of bits that flip in a birst is
    // at most maxBurstLength (if the first and last bits of the
    // sequence do flip).
    final double burstProbability = 0.005;
    final int maxBurstLength = 15;
    final double errorProbability = 0.25;
    int burstCount;
    // ===============================================================



// ===================================================================
} // class PerfectWire
// ===================================================================
