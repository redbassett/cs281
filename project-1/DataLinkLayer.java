// ===================================================================
// DataLinkLayer
// Scott F. H. Kaplan -- http://www.cs.amherst.edu/~sfkaplan
// September 2004
// ===================================================================



// ===================================================================
// A data link layer accepts a string of bytes, divides it into
// frames, adds some metadata, and sends the frame via its physical
// layer.  Upon receiving a frame, the data link layer removes the
// metadata, potentially performs some checks on the data, and
// delivers the data to its client network layer.
abstract class DataLinkLayer {
// ===================================================================



    // ===============================================================
    // The constructor.
    public void initialize (PhysicalLayer physicalLayer) {

	// Sanity check
	if (physicalLayer == null) {

	    throw new RuntimeException("No physical layer provided");

	}

	// Attempt to register with the physical layer.
	physicalLayer.register(this);

	// Keep a pointer to the physical layer.
	this.physicalLayer = physicalLayer;

	// Create incoming buffer space.
	incomingBuffer = new byte[bufferSize];
	bufferIndex = 0;

    } // DataLinkLayer
    // ===============================================================



    // ===============================================================
    // Allow a network layer to register as the client of this
    // data link layer.
    public void register (NetworkLayer client) {

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
    abstract public void send (byte[] data);
    // ===============================================================



    // ===============================================================
    // Allow the physical layer to deliver a byte into this layer's
    // buffer.
    void receive (byte data) {

	// Add the new byte to the buffer of bytes.
	incomingBuffer[bufferIndex] = data;
	bufferIndex++;

	// If this byte completes a frame, then process the frame,
	// obtaining the original data (stripped of metadata).
	byte[] originalData = null;
	if (receivedCompleteFrame()) {

	    originalData = processFrame();
	    bufferIndex = 0;

	}

	// If the frame was processed successfully, deliver the
	// processed frame to the client.
	if (originalData != null) {

	    client.receive(originalData);

	}

    } // receive
    // ===============================================================



    // ===============================================================
    // Determine whether the buffered data forms a complete frame.
    abstract protected boolean receivedCompleteFrame ();
    // ===============================================================



    // ===============================================================
    // Given a complete frame, process its contents, extracting
    // metadata and performing any error checking, then delivering (if
    // possible) the original data.  (Return a null pointer if the
    // data cannot be recovered.)
    abstract protected byte[] processFrame ();
    // ===============================================================



    // ===============================================================
    // DATA MEMBERS
    // ===============================================================



    // ===============================================================
    // The medium to which this layer is connected.
    PhysicalLayer physicalLayer;

    // The data link layer above this physical layer.
    NetworkLayer client;

    // A buffer of bytes for data received from the physical layer.
    // When a full frame is received, it is processed and the buffer
    // is emptied.
    byte[] incomingBuffer;

    // The number of bytes received so far into the incoming buffer.
    int bufferIndex;

    // The incoming buffer size.
    final int bufferSize = 32768;
    // ===============================================================



// ===================================================================
} // class DataLinkLayer
// ===================================================================
