// ===================================================================
// DumbDataLinkLayer
// Scott F. H. Kaplan -- http://www.cs.amherst.edu/~sfkaplan
// September 2004
// ===================================================================



// ===================================================================
// A data link layer that uses start/stop tags and byte packing to
// frame the data, and that performs no error management.
class DumbDataLinkLayer extends DataLinkLayer {
// ===================================================================



    // ===============================================================
    // The constructor.
    public DumbDataLinkLayer (PhysicalLayer physicalLayer) {

	// Initialize the layer.
	initialize(physicalLayer);

    } // DumbDataLinkLayer
    // ===============================================================



    // ===============================================================
    // Accept a buffer of data to send.  Send it as a single frame
    // with no error management redundancy.
    public void send (byte[] data) {

	// Allocate space sufficient to hold the data, including
	// possible byte packing, with start and stop tags.
	byte[] framedData = new byte[(data.length * 2) + 2];

	// Begin with the start tag.
	int frameIndex = 0;
	framedData[frameIndex++] = startTag;

	// Add each byte of original data.
	for (int dataIndex = 0; dataIndex < data.length; dataIndex++) {

	    // If the current data byte is itself a metadata tag, then
	    // preceed it with an escape tag.
	    byte currentByte = data[dataIndex];
	    if ((currentByte == startTag) ||
		(currentByte == stopTag) ||
		(currentByte == escapeTag)) {

		framedData[frameIndex++] = escapeTag;

	    }

	    // Add the data byte itself.
	    framedData[frameIndex++] = currentByte;

	}

	// End with a stop tag.
	framedData[frameIndex++] = stopTag;

	// Copy the complete frame into a buffer of the exact desired
	// size.
	byte[] finalFrame = new byte[frameIndex];
	for (int i = 0; i < frameIndex; i++) {
	    finalFrame[i] = framedData[i];
	}

	// Call on the underlying physical layer to send the data.
	physicalLayer.send(finalFrame);

    } // send
    // ===============================================================



    // ===============================================================
    // Determine whether the buffered data forms a complete frame.
    protected boolean receivedCompleteFrame () {

	// Any frame with less than two bytes cannot be complete,
	// since even the empty frame contains a start and a stop tag.
	if (bufferIndex < 2) {

	    return false;

	}

	// A frame is complete iff the byte received is an non-escaped
	// stop tag.
	return ((incomingBuffer[bufferIndex - 1] == stopTag) &&
		(incomingBuffer[bufferIndex - 2] != escapeTag));

    } // receivedCompleteFrame
    // ===============================================================



    // ===============================================================
    // Remove the framing metadata and return the original data.
    protected byte[] processFrame () {

	// Allocate sufficient space to hold the original data, which
	// does not need space for the start/stop tags.
	byte[] originalData = new byte[bufferIndex - 2];

	// Check the start tag.
	int frameIndex = 0;
	if (incomingBuffer[frameIndex++] != startTag) {

	    throw new RuntimeException("Missing start tag");

	}

	// Loop through the frame, extracting the bytes.
	int originalIndex = 0;
	while (incomingBuffer[frameIndex] != stopTag) {

	    // If the next original byte is escape-tagged, then skip
	    // the tag so that only the real data is extracted.
	    if (incomingBuffer[frameIndex] == escapeTag) {

		frameIndex++;

	    }

	    // Copy the original byte.
	    originalData[originalIndex] = incomingBuffer[frameIndex];
	    originalIndex++;
	    frameIndex++;

	}

	// Allocate a space that is only as large as the original
	// message and then copy the original data into it.
	byte[] finalData = new byte[originalIndex];
	for (int i = 0; i < originalIndex; i++) {
	    finalData[i] = originalData[i];
	}

	return finalData;

    } // processFrame
    // ===============================================================



    // ===============================================================
    // DATA MEMBERS
    // ===============================================================



    // ===============================================================
    // The start tag, stopf tag, and the escape tag.
    final byte startTag = (byte)'{';
    final byte stopTag = (byte)'}';
    final byte escapeTag = (byte)'\\';
    // ===============================================================



// ===================================================================
} // class DumbDataLinkLayer
// ===================================================================
