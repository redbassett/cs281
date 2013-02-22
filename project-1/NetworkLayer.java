// ===================================================================
// NetworkLayer
// Scott F. H. Kaplan -- http://www.cs.amherst.edu/~sfkaplan
// September 2004
// ===================================================================



// ===================================================================
// Currently, we use a network layer simply as a client of a data link
// layer.  Thus, it exists simply to send and receive sample data and
// display the results to the user.
class NetworkLayer {
// ===================================================================



    // ===============================================================
    // PUBLIC METHODS
    // ===============================================================



    // ===============================================================
    // The constructor.
    public NetworkLayer (DataLinkLayer dataLinkLayer) {

    	// Register with the data link layer.
	   dataLinkLayer.register(this);

	   // Keep a pointer to the data link layer.
	this.dataLinkLayer = dataLinkLayer;

    } // NetworkLayer
    // ===============================================================



    // ===============================================================
    // Allow a client to send a string of bytes on the medium.
    public void send () {

	   String[] messages = { "abc",
		              	     "abd",
			                 "The quick brown fox...",
			                 "Does {}{} byte packing \\ work?" };

	   for (int i = 0; i < messages.length; i++) {

	       byte[] data = messages[i].getBytes();
	       System.out.print("Network.send() message:    ");
	       System.out.println(messages[i]);
	       dataLinkLayer.send(data);

    	}

    } // send
    // ===============================================================



    // ===============================================================
    // Allow the data link layer to deliver an array of bytes to this
    // layer.
    void receive (byte[] data) {

	   String message = new String(data);

	   System.out.print("Network.receive() message: ");
	   System.out.println(message);

    } // receive
    // ===============================================================



    // ===============================================================
    // DATA MEMBERS
    // ===============================================================



    // ===============================================================
    // The medium to which this layer is connected.
    DataLinkLayer dataLinkLayer;
    // ===============================================================



// ===================================================================
} // class NetworkLayer
// ===================================================================
