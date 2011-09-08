package info.jupiter.net;

/**
 * Immutable packet object.
 * 
 * @author Graham
 */
public final class Packet {

	/**
	 * The type of packet.
	 * 
	 * @author Graham Edgecombe
	 */
	public enum Type {

		/**
		 * A fixed size packet where the size never changes.
		 */
		FIXED,

		/**
		 * A variable packet where the size is described by a byte.
		 */
		VARIABLE,

		/**
		 * A variable packet where the size is described by a word.
		 */
		VARIABLE_SHORT;

	}

	/**
	 * The ID of the packet
	 */
	private int pID;
	/**
	 * The length of the payload
	 */
	private int pLength;
	/**
	 * The payload
	 */
	private byte[] pData;
	/**
	 * The current index into the payload buffer for reading
	 */
	private int caret = 0;

	private Type size = Type.FIXED;

	public Packet(int pID, byte[] pData, Type s) {
		this.pID = pID;
		this.pData = pData;
		this.pLength = pData.length;

		this.size = s;
	}

	/**
	 * Creates a new packet with the specified parameters.
	 * 
	 * @param session
	 *            The session to associate with the packet
	 * @param pID
	 *            The ID of the packet
	 * @param pData
	 *            The payload of the packet
	 */
	public Packet(int pID, byte[] pData) {
		this(pID, pData, Type.FIXED);
	}

	/**
	 * Checks if this packet is considered to be a bare packet, which means that
	 * it does not include the standard packet header (ID and length values).
	 * 
	 * @return Whether this packet is a bare packet
	 */
	public boolean isRaw() {
		return pID == -1;
	}

	public Type getSize() {
		return size;
	}

	/**
	 * Returns the packet ID.
	 * 
	 * @return The packet ID
	 */
	public int getOpcode() {
		return pID;
	}

	/**
	 * Returns the length of the payload of this packet.
	 * 
	 * @return The length of the packet's payload
	 */
	public int getLength() {
		return pLength;
	}

	/**
	 * Returns the entire payload data of this packet.
	 * 
	 * @return The payload <code>byte</code> array
	 */
	public byte[] getData() {
		return pData;
	}

	/**
	 * Returns the remaining payload data of this packet.
	 * 
	 * @return The payload <code>byte</code> array
	 */
	public byte[] getRemainingData() {
		byte[] data = new byte[pLength - caret];
		for (int i = 0; i < data.length; i++) {
			data[i] = pData[i + caret];
		}
		caret += data.length;
		return data;

	}

	public void get(byte abyte0[], int i, int j) {
		for (int k = j; k < j + i; k++)
			abyte0[k] = pData[caret++];

	}

	public byte getByte() {
		return pData[caret++];
	}

	public byte getByteA() {
		return (byte) (pData[caret++] - 128);
	}

	public byte getByteC() {
		return (byte) (-pData[caret++]);
	}

	public byte getByteS() {
		return (byte) (128 - pData[caret++]);
	}

	public int getInt() {
		caret += 4;
		return ((pData[caret - 4] & 0xff) << 24) + ((pData[caret - 3] & 0xff) << 16) + ((pData[caret - 2] & 0xff) << 8) + (pData[caret - 1] & 0xff);
	}

	public int getInt1() {
		caret += 4;
		return ((pData[caret - 2] & 0xff) << 24) + ((pData[caret - 1] & 0xff) << 16) + ((pData[caret - 4] & 0xff) << 8) + (pData[caret - 3] & 0xff);
	}

	public int getInt2() {
		caret += 4;
		return ((pData[caret - 3] & 0xff) << 24) + ((pData[caret - 4] & 0xff) << 16) + ((pData[caret - 1] & 0xff) << 8) + (pData[caret - 2] & 0xff);
	}

	public int getLEShort() {
		caret += 2;
		int i = ((pData[caret - 1] & 0xff) << 8) + (pData[caret - 2] & 0xff);
		if (i > 32767)
			i -= 0x10000;
		return i;
	}

	public int getLEShortA() {
		caret += 2;
		int i = ((pData[caret - 1] & 0xff) << 8) + (pData[caret - 2] - 128 & 0xff);
		if (i > 32767)
			i -= 0x10000;
		return i;
	}

	public long getLong() {
		long l = (long) getInt() & 0xffffffffL;
		long l1 = (long) getInt() & 0xffffffffL;
		return (l << 32) + l1;
	}

	public void getReverse(byte abyte0[], int i, int j) {
		for (int k = (j + i) - 1; k >= j; k--)
			abyte0[k] = pData[caret++];

	}

	public void getReverseA(byte abyte0[], int i, int j) {
		for (int k = (j + i) - 1; k >= j; k--)
			abyte0[k] = (byte) (pData[caret++] - 128);

	}

	public String getRS2String() {
		int i = caret;
		while (pData[caret++] != 10)
			;
		return new String(pData, i, caret - i - 1);
	}

    public String readRS2String() {
        int start = caret;
        while (pData[caret++] != 0) ;
        return new String(pData, start, caret - start - 1);
    }
    
	public int getShort() {
		caret += 2;
		int i = ((pData[caret - 2] & 0xff) << 8) + (pData[caret - 1] & 0xff);
		if (i > 32767)
			i -= 0x10000;
		return i;
	}

	public int getShortA() {
		caret += 2;
		int i = ((pData[caret - 2] & 0xff) << 8) + (pData[caret - 1] - 128 & 0xff);
		if (i > 32767)
			i -= 0x10000;
		return i;
	}

	public void skip(int x) {
		caret += x;
	}
	
	public int getUnsignedByte() {
		return pData[caret++] & 0xff;
	}

	public int getUnsignedShort() {
		caret += 2;
		return ((pData[caret - 2] & 0xff) << 8) + (pData[caret - 1] & 0xff);
	}

}
