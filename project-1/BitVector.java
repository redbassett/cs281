// =============================================================================
/**
 * A <tt>BitVector</tt> allows you to read and write a indexed sequence of bit
 * values.  Methods are provided to allow you to obtain a compact byte-array
 * representation of the bits, or to construct a <tt>BitVector</tt> from an
 * existing, compact byte-array.
 *
 * @author Scott F. H. Kaplan -- sfkaplan@cs.amherst.edu
 * @date 2008 March 03
 * @version %I% %G%
 **/
public class BitVector {
// =============================================================================



    // =========================================================================
    /**
     * Default constructor.  Make an empty vector of bits.
     **/
    public BitVector () {

	_array = new boolean[1];
	_length = 0;

    } // BitVector ()
    // =========================================================================



    // =========================================================================
    /**
     * Construct a bit vector from an array of bytes, taking the bits of those
     * bytes in order as the values with which to initialize the new bit vector.
     *
     * @param byteArray The array of bytes whose bit values are copied.
     * @param begin The starting index of <tt>byteArray</tt> to use.
     * @param end The ending index of <tt>byteArray</tt> to use.
     **/
    public BitVector (byte[] byteArray, int begin, int end) {

	// Create an internal array of the necessary size.
	_array = new boolean[byteArray.length * _bitsPerByte];

	// Copy the bits from each byte, from most to least significant, into
	// the internal array.
	_length = 0;
	for (int byteIndex = begin; byteIndex < end; byteIndex++) {
	    for (int mask = 0x80; mask != 0; mask >>= 1) {

		_array[_length++] = ((byteArray[byteIndex] & mask) != 0);

	    }
	}

    } // BitVector (byte[] byteArray, int begin, int end)
    // =========================================================================



    // =========================================================================
    /**
     * Set a particular bit in the vector to the desired value.
     *
     * @param index The index into the vector (0-based).
     * @param value The bit value to set, using <tt>true</tt> for 1 and
     *              <tt>false</tt> for 0.
     **/
    public void setBit (int index, boolean value) {

	// No negative indices.
	if (index < 0) {
	    throw new RuntimeException("Negative BitVector index " + index);
	}

	// If this index is beyond the current array size, then expand the array
	// to twice the size that this index suggests is needed.
	if (index >= _array.length) {
	    expandArray(index * 2);
	}

	// If this index is beyond the known length of the vector, then update
	// that length.
	if (index >= _length) {
	    _length = index + 1;
	}

	_array[index] = value;

    } // setBit (int index, boolean value)
    // =========================================================================



    // =========================================================================
    /**
     * Return the value of a particular bit in the vector.
     *
     * @param index The index into the vector (0-based).
     * @return The value of the bit at the given index: <tt>true</tt> for 1,
     *         <tt>false</tt> for 0.
     **/
    public boolean getBit (int index) {

	// No negative indices.
	if (index < 0) {
	    throw new RuntimeException("Negative BitVector index " + index);
	}

	// If this index is beyond those stored, return an implicit 0 (the
	// vector is conceptually infinite).  Otherwise, return its actual
	// value.
	if (index >= _array.length) {
	    return false;
	} else {
	    return _array[index];
	}

    } // getBit (int index)
    // =========================================================================



    // =========================================================================
    /**
     * Provide the known length of the vector.  That is, although any positive
     * index can be querried, only some portion of the array has had bit values
     * set.  Return the length of the vector that contains set bits.
     *
     * @return The length of the vector that contains set bits.
     **/
    public int length () {

	return _length;

    } // length ()
    // =========================================================================



    // =========================================================================
    /**
     * Return the known portion of the vector (up to the highest set index) as a
     * compact byte array.  The bits are placed into this array in order -- that
     * is, the first byte will contain bits 0 to 7 from the vector, with the
     * 0<sup>th</sup> bit being in the most significant position of the byte,
     * and the 7<sup>th</sup> bit being in the least significant bit of the
     * byte.
     *
     * @return A byte array containing a compact representation of the bits in
     *         this vector.
     **/
    public byte[] toByteArray () {

	// Create an array of bytes large enough to hold the entire vector.
	// "Round up" to have a sufficient, integral number of bytes.
	int size = (int)Math.ceil((double)_length / _bitsPerByte);
	byte[] byteArray = new byte[size];

	// Copy the bits into each byte, from most to least significant, from
	// the internal array.
	int vectorIndex = 0;
	for (int byteIndex = 0; byteIndex < byteArray.length; byteIndex++) {

	    byte currentByte = 0;
	    for (int bitsInThisByte = 0;
		 bitsInThisByte < _bitsPerByte;
		 bitsInThisByte++) {

		// Add bits into the bottom and shift them up (left).
		currentByte <<= 1;
		currentByte |= (_array[vectorIndex++] ? 1 : 0);

	    }
	    byteArray[byteIndex] = currentByte;

	}

	return byteArray;

    }
    // =========================================================================



    // =========================================================================
    /**
     * Expand the internal array used to store bit values by doubling its size.
     *
     * @param size The new size to which to expand.
     **/
    private void expandArray (int size) {

	// Ensure that the size is an increase.
	if (size > _array.length) {

	    boolean[] oldArray = _array;
	    _array = new boolean[size];
	    for (int index = 0; index < oldArray.length; index++) {

		_array[index] = oldArray[index];

	    }

	}

    } // expandArray (int size)
    // =========================================================================



    // =========================================================================
    // DATA MEMBERS

    /**
     * An array that stores the values of the known portion of the vector of
     * bits.  Store the values as booleans.  The array may be larger than the
     * known portion of the vector, but it does fully contain it at all times.
     **/
    private boolean[] _array;

    /**
     * The length of the known portion of the vector, where the <i>known</i>
     * portion begins at index 0 and ends at the highest index for which the
     * vaule has been set.  There may be intermediate bits within this range
     * that never have been set, but they are all conidered <i>known</i> and
     * have a default value of <tt>0</tt>.
     **/
    private int _length;

    /**
     * The number of bits in a byte.
     **/
    private final static int _bitsPerByte = 8;
    // =========================================================================



// =============================================================================
} // class BitVector
// =============================================================================
