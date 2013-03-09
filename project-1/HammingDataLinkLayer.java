// =============================================================================
/**
 *  A data link layer that uses start/stop tags and byte packing to frame the
 *  data, and that uses a single parity bit to perform error detection.
 *
 * @author Scott F. H. Kaplan -- http://www.cs.amherst.edu/~sfkaplan
 * @date 2008 March 03
 * @version %I% %G%
 **/
public class HammingDataLinkLayer extends DataLinkLayer {
// =============================================================================



    // =========================================================================
    /**
     * The constructor.  Make a new parity-checking data link layer.
     *
     * @param physicalLayer The physical layer through which this data link
     * layer should communicate.
     **/
    public HammingDataLinkLayer (PhysicalLayer physicalLayer) {

	// Initialize the layer.
	initialize(physicalLayer);

    } // ParityDataLinkLayer
    // =========================================================================



    // =========================================================================
    /**
     * Accept a buffer of data to send.  Send it as divided into multiple frames
     * of a fixed, maximum size.  Add a parity bit for error checking to each
     * frame.  Call the physical layer to actually send each frame.
     *
     * @param data An array of bytes to be framed and transmitted.
     **/
    public void send (byte[] data) {

	// Calculate the number of frames needed to transmit this data.
	int numberFrames = (int)Math.ceil((double)data.length / _maxFrameSize);

	// Construct each frame and send it.
	for (int frameNumber = 0; frameNumber < numberFrames; frameNumber++) {

	    int beginIndex = _maxFrameSize * frameNumber;
	    int endIndex = _maxFrameSize * (frameNumber + 1);
	    if (endIndex > data.length) {
		endIndex = data.length;
	    }
	    byte[] frame = constructFrame(data, beginIndex, endIndex);
	    physicalLayer.send(frame);

	}

    } // send (byte[] data)
    // =========================================================================



    // =========================================================================
    /**
     * Create a single frame to be transmitted.
     *
     * @param data The original buffer of data from which to extract a frame's
     *             worth.
     * @param begin The starting index from the original data buffer.
     * @param end The ending index from the original frame buffer.
     * @return A byte array that contains an entirely constructed frame.
     **/
    private byte[] constructFrame (byte[] data, int begin, int end) {

	// Allocate an array of bytes large enough to hold the largest possible
	// frame (tags and parity byte included).
	byte[] framedData = new byte[(_maxFrameSize * 2) + 3];

	// Begin with the start tag.
	int frameIndex = 0;
	framedData[frameIndex++] = _startTag;

	// Add each byte of original data.
	for (int dataIndex = begin; dataIndex < end; dataIndex++) {

	    // If the current data byte is itself a metadata tag, then preceed
	    // it with an escape tag.
	    byte currentByte = data[dataIndex];
	    if ((currentByte == _startTag) ||
		(currentByte == _stopTag) ||
		(currentByte == _escapeTag)) {

		framedData[frameIndex++] = _escapeTag;

	    }

	    // Add the data byte itself.
	    framedData[frameIndex++] = currentByte;

	}

	// Calculate the parity bit (which is placed in its own byte).
	framedData[frameIndex++] = calculateParity(data, begin, end);
	
	calculateHamming(data, begin, end);

	// End with a stop tag.
	framedData[frameIndex++] = _stopTag;

	// Copy the complete frame into a buffer of the exact desired
	// size.
	byte[] finalFrame = new byte[frameIndex];
	for (int i = 0; i < frameIndex; i++) {
	    finalFrame[i] = framedData[i];
	}

	return finalFrame;

    } // constructFrame (byte[] data, int begin, int end)
    // =========================================================================
    
    
    private byte[] calculateHamming(byte[] data, int begin, int end) {
    	int numPowers = 6;
    	
    	BitVector bits = new BitVector(data, begin, end);
        BitVector bits2 = new BitVector(new byte[0], 0, 0);
        
        for (int i = 0; i < bits.length(); i++) {
            if (bits.getBit(i) == false) {
                System.out.print(0);
            } else {
                System.out.print(1);
            }
        }
        System.out.print(' ');

        
    	for (int i = 1; i < bits.length(); i++) {
    		Double logged = Math.log(i)/Math.log(2);
    		if ((logged == Math.floor(logged)) && !Double.isInfinite(logged)) {
                System.out.print('-');
                bits2.setBit(bits2.length()+1, false);
    			/*boolean[] copy = new boolean[bits.length()-i];
    			for (int j = i; j < bits.length(); j++) {
    				copy[j-i] = bits.getBit(j);
    			}
    			for (int j = i; j < bits.length()-i; j++) {
    				bits.setBit(j+1, copy[j-i]);
    			}
    			bits.setBit(i, false);*/
    		} else {
                System.out.print(i);
            }
            System.out.print(' ');
            bits2.setBit(bits2.length(), bits.getBit(i));
    	}
    	
    	
    	for (int i = 0; i < bits2.length(); i++) {
    		if (bits2.getBit(i) == false) {
    			System.out.print(0);
    		} else {
    			System.out.print(1);
    		}
    	}
    	System.out.println();
    	
    	int[] checkPositions = new int[0];
    	
    	return data;
    }



    // =========================================================================
    /**
     * Calculate the parity of the sequence of bytes.
     *
     * @param data A buffer of bytes.
     * @param begin The starting index of the bytes to examine.
     * @param end The ending index of the bytes to examine.
     * @return The parity (0 or 1) for this group of bytes.
     **/
    private byte calculateParity (byte[] data, int begin, int end) {

	// Create a bit vector from the bytes specified.
	BitVector bits = new BitVector(data, begin, end);

	// Iterate over the bit vector, counting the bits whose value is 1.
	int ones = 0;
	for (int i = 0; i < bits.length(); i++) {

	    ones += (bits.getBit(i) ? 1 : 0);

	}

	// Return the parity.
	return (byte)(ones % 2);

    } // calculateParity (byte[] data, int begin, int end)
    // =========================================================================


    // =========================================================================
    /**
     * Determine whether the buffered data forms a complete frame.
     *
     * @return Whether a complete buffer has arrived.
     **/
    protected boolean receivedCompleteFrame () {

	// Any frame with less than two bytes cannot be complete, since even the
	// empty frame contains a start and a stop tag.
	if (bufferIndex < 2) {

	    return false;

	}

	// A frame is complete iff the byte received is an non-escaped stop tag.
	return ((incomingBuffer[bufferIndex - 1] == _stopTag) &&
		(incomingBuffer[bufferIndex - 2] != _escapeTag));

    } // receivedCompleteFrame
    // =========================================================================



    // =========================================================================
    /**
     *  Remove the framing metadata and return the original data.
     *
     * @return The data carried in this frame; <tt>null</tt> if the data was not
     *         successfully received.
     **/
    protected byte[] processFrame () {

	// Allocate sufficient space to hold the original data, which
	// does not need space for the start/stop tags.
	byte[] originalData = new byte[bufferIndex - 3];

	// Check the start tag.
	int frameIndex = 0;
	if (incomingBuffer[frameIndex++] != _startTag) {

	    System.err.println("ParityDLL: Missing start tag!");
	    return null;

	}

	// Loop through the frame, extracting the bytes.  Look ahead to find the
	// stop tag (making sure it is not escaped), because the byte before
	// that is the parity byte.
	int originalIndex = 0;
	while ((incomingBuffer[frameIndex + 1] != _stopTag) ||
	       (incomingBuffer[frameIndex] == _escapeTag)) {

	    // If the next original byte is escape-tagged, then skip
	    // the tag so that only the real data is extracted.
	    if (incomingBuffer[frameIndex] == _escapeTag) {

		frameIndex++;

	    }

	    // Copy the original byte.
	    originalData[originalIndex++] = incomingBuffer[frameIndex++];

	}

	// Allocate a space that is only as large as the original
	// message and then copy the original data into it.
	byte[] finalData = new byte[originalIndex];
	for (int i = 0; i < originalIndex; i++) {
	    finalData[i] = originalData[i];
	}

	// Calculate the parity of the extracted data and compare it to the
	// received parity bit.  If there's a mismatch, return null.
	byte parity = calculateParity(originalData, 0, originalIndex);
	if (parity != incomingBuffer[frameIndex]) {

	    System.err.print("ParityDLL message: ");
	    for (int i = 0; i < finalData.length; i++) {
		System.err.print((char)finalData[i]);
	    }
	    System.err.println(" <= Parity mismatch!");
	    finalData = null;

	}

	return finalData;

    } // processFrame
    // =========================================================================



    // =========================================================================
    // DATA MEMBERS

    /**
     * The tag that marks the beginning of a frame.
     **/
    final byte _startTag = (byte)'{';

    /**
     * The tag that marks the end of a frame.
     **/
    final byte _stopTag = (byte)'}';

    /**
     * The tag that marks the following byte as data (and not metadata).
     **/
    final byte _escapeTag = (byte)'\\';

    /**
     * The maximum number of data (not metadata) bytes in a frame.
     **/
    final int _maxFrameSize = 8;
    // =========================================================================



// =============================================================================
} // class ParityDataLinkLayer
// =============================================================================
