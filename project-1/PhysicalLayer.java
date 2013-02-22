// ===================================================================
// PhysicalLayer
// Scott F. H. Kaplan -- http://www.cs.amherst.edu/~sfkaplan
// September 2004
// ===================================================================



// ===================================================================
// A physical layer transmits an arbitrary string of bytes.  All
// objects of this class share a single medium.
class PhysicalLayer {
// ===================================================================



    // ===============================================================
    // PUBLIC METHODS
    // ===============================================================



    // ===============================================================
    public PhysicalLayer (Medium medium) {

	// Attempt to register with the medium as a client.
	medium.register(this);

	// Keep a pointer to the medium (if one isn't already held
	// thanks to another PhysicalLayer object).
	if (PhysicalLayer.medium == null) {

	    PhysicalLayer.medium = medium;

	}

	// Sanity check
	if (PhysicalLayer.medium != medium) {

	    throw new RuntimeException();

	}

	// Initialize the incoming buffer.
	bitsReceived = 0;

    } // PhysicalLayer
    // ===============================================================



    // ===============================================================
    // Allow a data link layer to register as the client of this
    // physical layer.
    public void register (DataLinkLayer client) {

	// Is there already a client registered?
	if (this.client != null) {

	    throw new RuntimeException();

	}

	// Hold a pointer to the client.
	this.client = client;

    } // register
    // ===============================================================



    // ===============================================================
    // Allow a client to send a string of bytes on the medium.
    void send (byte[] outgoingBuffer) {

	// Send each byte.
	for (int i = 0; i < outgoingBuffer.length; i++) {

	    // Send each bit from the current byte.
	    int currentByte = outgoingBuffer[i];

	    for (int j = 0; j < 8; j++) {

		byte value = (byte)((currentByte >>> j) & 0x1);
		boolean bit = (value == 0x1);
		medium.send(this, bit);

	    }

	}

    } // send
    // ===============================================================



    // ===============================================================
    // Allow the medium to deliver a bit into this layer's buffer.
    void receive (boolean bit) {

	// Buffer the bit.
	int value = (bit ? 0x1 : 0x0);
	int shiftedBuffer = (incomingBuffer >> 1);
	int shiftedValue = (value << (bufferSize - 1));
	incomingBuffer = (shiftedBuffer | shiftedValue);
	bitsReceived++;

	// If the buffer is full, deliver it to the client.
	if (bitsReceived == bufferSize) {

	    client.receive((byte)incomingBuffer);
	    bitsReceived = 0;

	}

    }
    // ===============================================================



    // ===============================================================
    // DATA MEMBERS
    // ===============================================================



    // ===============================================================
    // The medium to which this layer is connected.
    static Medium medium;

    // The data link layer above this physical layer.
    DataLinkLayer client;

    // A single byte into which received bits are buffered.  When the
    // byte it full, it is delivered to the data link layer.  We use
    // an int as the actual buffer space to avoid casting when doing
    // int-based bit manipulations.
    int incomingBuffer;

    // The number of bits received so far into the incoming buffer.
    int bitsReceived;

    // The size of the buffer (in bits).
    final int bufferSize = 8;
    // ===============================================================



// ===================================================================
} // class PhysicalLayer
// ===================================================================
