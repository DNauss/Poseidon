package info.jupiter.packet;

import info.jupiter.Player;
import info.jupiter.net.Packet;

public class PacketManager {

	public static void handlePacket(Player player, Packet packet) {

		try {
			switch (packet.getOpcode()) {

			default:
				System.out.println("Message Recieved: " + packet.getOpcode());
				break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
