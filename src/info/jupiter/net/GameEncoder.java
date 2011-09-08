package info.jupiter.net;

import info.jupiter.Player;

import java.io.IOException;
import java.nio.ByteBuffer;

public class GameEncoder implements AbstractEncoder {

	@Override
	public void write(Session session, Packet p) {
		try {
			if (p.isRaw()) {
				/*
				 * If the packet is raw, send its payload.
				 */
				session.getSocketChannel().write(ByteBuffer.wrap(p.getData()));
			} else {
				/*
				 * If not, get the out ISAAC cipher.
				 */
				// ISAACCipher outCipher = ((Player)
				// session.getAttribute("session")).getOutCipher();

				/*
				 * Get the packet attributes.
				 */
				int opcode = p.getOpcode();
				Packet.Type type = p.getSize();
				int length = p.getLength();

				/*
				 * Encrypt the packet opcode.
				 */
				opcode += ((Player) session.getAttachment()).getOutCipher().getNextValue();

				/*
				 * Compute the required size for the buffer.
				 */
				int finalLength = length + 1;
				switch (type) {
				case VARIABLE:
					finalLength += 1;
					break;
				case VARIABLE_SHORT:
					finalLength += 2;
					break;
				}

				/*
				 * Create the buffer and write the opcode (and length if the
				 * packet is variable-length).
				 */
				ByteBuffer buffer = ByteBuffer.allocate(finalLength);
				buffer.put((byte) opcode);
				switch (type) {
				case VARIABLE:
					buffer.put((byte) length);
					break;
				case VARIABLE_SHORT:
					buffer.putShort((short) length);
					break;
				}

				/*
				 * Write the payload itself.
				 */
				buffer.put(p.getData());
				buffer.flip();
				/*
				 * Flip and dispatch the packet.
				 */
				session.getSocketChannel().write(buffer);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
