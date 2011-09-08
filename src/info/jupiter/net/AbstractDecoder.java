package info.jupiter.net;

import java.nio.ByteBuffer;

/**
 * Basic decoding interface.
 * @author Advocatus <davidcntt@hotmail.com>
 *
 */
public interface AbstractDecoder {
	
	/**
	 * Decodes the data in the buffer that is read from the socket this cycle. 
	 * @param data The data
	 * @return whether the decoder is able to decode again this cycle.
	 */
	boolean decode(ByteBuffer data);
}
