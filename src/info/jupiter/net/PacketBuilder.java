package info.jupiter.net;

import info.jupiter.net.Packet.Type;

/**
 * A utility class for building packets.
 * 
 * @author Graham Edgecombe
 */
public class PacketBuilder {

	/**
	 * Bit mask array.
	 */
	private static final int[] BIT_MASK_OUT = new int[32];

	/**
	 * Creates the bit mask array.
	 */
	static {
		for (int i = 0; i < BIT_MASK_OUT.length; i++) {
			BIT_MASK_OUT[i] = (1 << i) - 1;
		}
	}

	/**
	 * Default capacity
	 */
	private static final int DEFAULT_SIZE = 32;

	/**
	 * The opcode.
	 */
	private int opcode;

	/**
	 * The type.
	 */
	private Type type;

	/**
	 * The payload.
	 */
	private byte[] payload;

	/**
	 * Current number of bytes used in the buffer
	 */
	private int curLength;

	/**
	 * The current bit position.
	 */
	private int bitPosition;

	/**
	 * Creates a raw packet builder.
	 */
	public PacketBuilder() {
		this(-1);
	}

	/**
	 * Creates a fixed packet builder with the specified opcode.
	 * 
	 * @param opcode
	 *            The opcode.
	 */
	public PacketBuilder(int opcode) {
		this(opcode, Type.FIXED);
	}

	/**
	 * Creates a packet builder with the specified opcode and type.
	 * 
	 * @param opcode
	 *            The opcode.
	 * @param type
	 *            The type.
	 */
	public PacketBuilder(int opcode, Type type) {
		this.opcode = opcode;
		this.type = type;
		this.payload = new byte[DEFAULT_SIZE];
	}

	/**
	 * Ensures that the buffer is at least <code>minimumBytes</code> bytes.
	 * 
	 * @param minimumCapacity
	 *            The size needed
	 */
	private void ensureCapacity(int minimumCapacity) {
		if (minimumCapacity >= payload.length)
			expandCapacity(minimumCapacity);
	}

	/**
	 * Expands the buffer to the specified size.
	 * 
	 * @param minimumCapacity
	 *            The minimum capacity to which to expand
	 * @see java.lang.AbstractStringBuilder#expandCapacity(int)
	 */
	private void expandCapacity(int minimumCapacity) {
		int newCapacity = (payload.length + 1) * 2;
		if (newCapacity < 0) {
			newCapacity = Integer.MAX_VALUE;
		} else if (minimumCapacity > newCapacity) {
			newCapacity = minimumCapacity;
		}
		byte[] newPayload = new byte[newCapacity];
		try {
			while (curLength > payload.length)
				curLength--;
			System.arraycopy(payload, 0, newPayload, 0, curLength);
		} catch (Exception e) {

		}
		payload = newPayload;
	}

	/**
	 * Finishes bit access.
	 * 
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder finishBitAccess() {
		curLength = (bitPosition + 7) / 8;
		return this;
	}

	public PacketBuilder startBitAccess() {
		bitPosition = curLength * 8;
		return this;
	}

	/**
	 * TODO needs a proper description.
	 */
	public PacketBuilder putBits(int numBits, int value) {
		int bytePos = bitPosition >> 3;
		int bitOffset = 8 - (bitPosition & 7);
		bitPosition += numBits;
		curLength = (bitPosition + 7) / 8;
		ensureCapacity(curLength);
		for (; numBits > bitOffset; bitOffset = 8) {
			payload[bytePos] &= ~BIT_MASK_OUT[bitOffset]; // mask out the
															// desired area
			payload[bytePos++] |= (value >> (numBits - bitOffset)) & BIT_MASK_OUT[bitOffset];

			numBits -= bitOffset;
		}
		if (numBits == bitOffset) {
			payload[bytePos] &= ~BIT_MASK_OUT[bitOffset];
			payload[bytePos] |= value & BIT_MASK_OUT[bitOffset];
		} else {
			payload[bytePos] &= ~(BIT_MASK_OUT[numBits] << (bitOffset - numBits));
			payload[bytePos] |= (value & BIT_MASK_OUT[numBits]) << (bitOffset - numBits);
		}
		return this;
	}

	/**
	 * Adds the contents of <code>byte</code> array <code>data</code> to the
	 * packet. The size of this packet will grow by the length of the provided
	 * array.
	 * 
	 * @param data
	 *            The bytes to add to this packet
	 * @return A reference to this object
	 */
	public PacketBuilder addBytes(byte[] data) {
		return addBytes(data, 0, data.length);
	}

	/**
	 * Adds the contents of <code>byte</code> array <code>data</code>, starting
	 * at index <code>offset</code>. The size of this packet will grow by
	 * <code>len</code> bytes.
	 * 
	 * @param data
	 *            The bytes to add to this packet
	 * @param offset
	 *            The index of the first byte to append
	 * @param len
	 *            The number of bytes to append
	 * @return A reference to this object
	 */
	public PacketBuilder addBytes(byte[] data, int offset, int len) {
		int newLength = curLength + len;
		ensureCapacity(newLength);
		System.arraycopy(data, offset, payload, curLength, len);
		curLength = newLength;
		return this;
	}

	public PacketBuilder putLEShortA(int i) {
		ensureCapacity(curLength + 2);
		addByte((byte) (i + 128), false);
		addByte((byte) (i >> 8), false);
		return this;
	}

	public PacketBuilder putShortA(int i) {
		ensureCapacity(curLength + 2);
		addByte((byte) (i >> 8), false);
		addByte((byte) (i + 128), false);
		return this;
	}

	/**
	 * Adds a <code>byte</code> to the data buffer. The size of this packet will
	 * grow by one byte.
	 * 
	 * @param val
	 *            The <code>byte</code> value to add
	 * @return A reference to this object
	 */
	public PacketBuilder put(byte val) {
		return addByte(val, true);
	}

	public PacketBuilder putByteA(int i) {
		return addByte((byte) (i + 128), true);
	}

	/**
	 * Adds a <code>byte</code> to the data buffer. The size of this packet will
	 * grow by one byte.
	 * 
	 * @param val
	 *            The <code>byte</code> value to add
	 * @param checkCapacity
	 *            Whether the buffer capacity should be checked
	 * @return A reference to this object
	 */
	private PacketBuilder addByte(byte val, boolean checkCapacity) {
		if (checkCapacity)
			ensureCapacity(curLength + 1);
		payload[curLength++] = val;
		return this;
	}

	/**
	 * Adds a <code>short</code> to the data stream. The size of this packet
	 * will grow by two bytes.
	 * 
	 * @param val
	 *            The <code>short</code> value to add
	 * @return A reference to this object
	 */
	public PacketBuilder putShort(int val) {
		ensureCapacity(curLength + 2);
		addByte((byte) (val >> 8), false);
		addByte((byte) val, false);
		return this;
	}

	public PacketBuilder putLEShort(int val) {
		ensureCapacity(curLength + 2);
		addByte((byte) val, false);
		addByte((byte) (val >> 8), false);
		return this;
	}

	/**
	 * Adds a <code>int</code> to the data stream. The size of this packet will
	 * grow by four bytes.
	 * 
	 * @param val
	 *            The <code>int</code> value to add
	 * @return A reference to this object
	 */
	public PacketBuilder putInt(int val) {
		ensureCapacity(curLength + 4);
		addByte((byte) (val >> 24), false);
		addByte((byte) (val >> 16), false);
		addByte((byte) (val >> 8), false);
		addByte((byte) val, false);
		return this;
	}

	public PacketBuilder putInt1(int val) {
		ensureCapacity(curLength + 4);
		addByte((byte) (val >> 8), false);
		addByte((byte) val, false);
		addByte((byte) (val >> 24), false);
		addByte((byte) (val >> 16), false);
		return this;
	}

	public PacketBuilder putInt2(int val) {
		ensureCapacity(curLength + 4);
		addByte((byte) (val >> 16), false);
		addByte((byte) (val >> 24), false);
		addByte((byte) val, false);
		addByte((byte) (val >> 8), false);
		return this;
	}

	public PacketBuilder putLEInt(int val) {
		ensureCapacity(curLength + 4);
		addByte((byte) val, false);
		addByte((byte) (val >> 8), false);
		addByte((byte) (val >> 16), false);
		addByte((byte) (val >> 24), false);
		return this;
	}

	public PacketBuilder putTriByte(int i) {
		ensureCapacity(3);
		payload[curLength++] = (byte) (i >> 16);
		payload[curLength++] = (byte) (i >> 8);
		payload[curLength++] = (byte) i;
		return this;
	}

	/**
	 * Adds a <code>long</code> to the data stream. The size of this packet will
	 * grow by eight bytes.
	 * 
	 * @param val
	 *            The <code>long</code> value to add
	 * @return A reference to this object
	 */
	public PacketBuilder putLong(long val) {
		putInt((int) (val >> 32));
		putInt((int) (val & -1L));
		return this;
	}

	public PacketBuilder addLELong(long val) {
		putLEInt((int) (val & -1L));
		putLEInt((int) (val >> 32));
		return this;
	}

	public PacketBuilder putRS2String(String s) {
		ensureCapacity(s.length());
		System.arraycopy(s.getBytes(), 0, payload, curLength, s.length());
		curLength += s.length();
		payload[curLength++] = 10;
		return this;
	}

	public int getLength() {
		return curLength;
	}

	/**
	 * Returns a <code>StaticPacket</code> object for the data contained in this
	 * builder.
	 * 
	 * @return A <code>StaticPacket</code> object
	 */
	public Packet toPacket() {
		byte[] data = new byte[curLength];
		System.arraycopy(payload, 0, data, 0, curLength);
		return new Packet(opcode, data, type);
	}

	public PacketBuilder putByteC(int val) {
		put((byte) -val);
		return this;
	}

	public PacketBuilder putByteS(int val) {
		put((byte) (128 - val));
		return this;
	}
}
