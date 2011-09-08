package info.jupiter.net;

import info.Constants;
import info.jupiter.Player;
import info.jupiter.packet.PacketManager;

import java.nio.ByteBuffer;

public class GameDecoder implements AbstractDecoder {

	private Session session;
	private int opcode = -1;
	private int size = -1;

	public GameDecoder(Session player) {
		this.session = player;
	}
	
	public boolean decode(ByteBuffer buffer) {
		if (opcode == -1) {
			opcode = buffer.get() & 0xff;
			//TODO: opcode - the next isaac key from the incoming cipher.
			opcode = (opcode - ((Player) session.getAttachment()).getInCipher().getNextValue()) & 0xFF;
		}
		if (size == -1) {
			size = Constants.PACKET_SIZES[opcode];
			if (size == -1) {
				if (!buffer.hasRemaining()) {
					buffer.flip();
					buffer.compact();
					return false;
				}
				size = buffer.get() & 0xff;
			}
		}
		if (buffer.remaining() >= size) {
			Packet packet = new Packet(opcode, new byte[size]);
			buffer.get(packet.getData(), 0, size);
			opcode = -1;
			size = -1;
			PacketManager.handlePacket((Player) session.getAttachment(), packet);
			return true;
		} else {
			buffer.flip();
			buffer.compact();
			return false;
		}
	}

}
