package info.jupiter;

import info.Constants;
import info.jupiter.net.PacketBuilder;
import info.jupiter.util.EntityList;

public class World {

//	public static final RegionManager reg = new RegionManager();

	/**
	 * World instance.
	 */
	private static final World world = new World();

	/**
	 * Gets the world instance.
	 * 
	 * @return The world instance.
	 */
	public static World getWorld() {
		return world;
	}

	/**
	 * All registered players.
	 */
	private EntityList<Player> players = new EntityList<Player>(2048);

	/**
	 * All registered NPCs.
	 */
	private EntityList<Npc> npcs = new EntityList<Npc>(8192);

	/**
	 * Registers a player for processing.
	 * 
	 * @param player
	 *            the player
	 */
	public void register(Player player) {
		if (!players.add(player)) // TODO
			throw new IllegalStateException("RS2Server is full!");
	}

	/**
	 * Registers an NPC for processing.
	 * 
	 * @param npc
	 *            the npc
	 */
	public void register(Npc npc) {
		if (!npcs.add(npc)) // TODO
			throw new IllegalStateException("RS2Server is full!");

	}

	/**
	 * Unregisters a player from processing.
	 * 
	 * @param player
	 *            the player
	 */
	public void unregister(Player player) {
		players.remove(player);
	}

	/**
	 * Unregisters an NPC from processing.
	 * 
	 * @param npc
	 *            the npc
	 */
	public void unregister(Npc npc) {
		npcs.remove(npc);
	}

	/**
	 * Gets all registered players.
	 * 
	 * @return the players
	 */
	public EntityList<Player> getPlayers() {
		return players;
	}

	/**
	 * Gets all registered NPCs.
	 * 
	 * @return the npcs
	 */
	public EntityList<Npc> getNpcs() {
		return npcs;
	}

	public void login(PlayerDetails details) throws Exception {
		int response = Constants.LOGIN_RESPONSE_OK;

		// Check if the player is already logged in.
		for (Player player : World.getWorld().getPlayers()) {
			if (player == null) {
				continue;
			}
			if (player.getUsername().equals(details.getUsername())) {
				response = Constants.LOGIN_RESPONSE_ACCOUNT_ONLINE;
			}
		}

		// Load the player and send the login response.
		int status = 1 /* load this player bitch!*/;
		if (status == 2) { // Invalid username/password.
			response = Constants.LOGIN_RESPONSE_INVALID_CREDENTIALS;
		}

		PacketBuilder resp = new PacketBuilder();
		resp.put((byte) response);
		resp.put((byte) /* login.getStaffRights() */0);
		resp.put((byte) 0);
		details.getSession().write(resp.toPacket());
		if (response != 2) {
			details.getSession().disconnect();
			return;
		}

		Player login = new Player(details);
		details.getSession().setAttachment(login);

		World.getWorld().register(login);
		login.getActionSender().sendLogin();

		System.out.println(login + " has logged in.");
	}
}
