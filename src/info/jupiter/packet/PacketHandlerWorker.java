package info.jupiter.packet;

import info.jupiter.Player;
import info.jupiter.World;
import info.jupiter.net.Packet;

/**
 * Handles queued packets.
 * @author Advocatus <davidcntt@hotmail.com>
 *
 */
public class PacketHandlerWorker implements Runnable {
	
	/**
	 * Run every cycle.
	 */
	@Override
	public void run() {
		for (Player player : World.getWorld().getPlayers()) {
			try {
				if (player.getSession().getSocketChannel().isOpen()) {
					for (Packet packet = player.getPacketQueue().poll(); packet != null; packet = player.getPacketQueue().poll()) {
						PacketManager.handlePacket(player, packet);
						player.setPacketReceived(packet.getOpcode(), false);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
