package info.jupiter.net;

import info.Constants;
import info.jupiter.Player;
import info.jupiter.net.Packet.Type;

/**
 * Utility class that deals with building packets to send packets from the server -> client.
 * @author Advocatus <davidcntt@hotmail.com>
 *
 */
public class ActionSender {
	
	/**
	 * The player to instance this class for.
	 */
	private Player player;

	/**
	 * Sets the player object for this instance
	 * @param player the player to set.
	 */
	public ActionSender(Player player) {
		this.player = player;
	}

	/**
	 * Sends login from the server to the client.
	 * @return the instance for chaining.
	 */
	public ActionSender sendLogin() {
		sendDetails();
		sendMessage("Welcome to RuneScape.");
		sendMapRegion();
		sendSidebarInterfaces();
		return this;
	}
	
	/**
	 * Sends all the sidebar interfaces.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendSidebarInterfaces() {
		final int[] icons = Constants.SIDEBAR_INTERFACES[0];
		final int[] interfaces = Constants.SIDEBAR_INTERFACES[1];
		for(int i = 0; i < icons.length; i++) {
			sendSidebarInterface(icons[i], interfaces[i]);
		}
		return this;
	}
	
	/**
	 * Sends a specific sidebar interface.
	 * @param icon The sidebar icon.
	 * @param interfaceId The interface id.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendSidebarInterface(int icon, int interfaceId) {
		player.write(new PacketBuilder(71).putShort(interfaceId).putByteA(icon).toPacket());
		return this;
	}
	
	/**
	 * Sends a message.
	 * @param message The message to send.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendMessage(String message) {
		player.write(new PacketBuilder(253, Type.VARIABLE).putRS2String(message).toPacket());
		return this;
	}
	
	/**
	 * Sends the map region load command.
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendMapRegion() {
		player.setLastKnownRegion(player.getLocation());
		player.write(new PacketBuilder(73).putShortA(player.getLocation().getRegionX() + 6).putShort(player.getLocation().getRegionY() + 6).toPacket());
		return this;
	}
	
	/**
	 * Sends the initial login packet (e.g. members, player id).
	 * @return The action sender instance, for chaining.
	 */
	public ActionSender sendDetails() {
		player.write(new PacketBuilder(249).putByteA(player.isMembers() ? 1 : 0).putLEShortA(player.getIndex()).toPacket());
		player.write(new PacketBuilder(107).toPacket());
		return this;
	}

}
