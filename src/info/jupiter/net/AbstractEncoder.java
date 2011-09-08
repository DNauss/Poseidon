package info.jupiter.net;

/**
 * Basic encoding interface.
 * @author Advocatus <davidcntt@hotmail.com>
 *
 */
public interface AbstractEncoder {
	
	/**
	 * Writes a packet object to the socket.
	 * @param session The session which writes to the associated socket.
	 * @param packet The packet to be written.
	 */
	void write(Session session, Packet packet);
}
