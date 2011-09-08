package info.jupiter;

import java.util.ArrayDeque;
import java.util.Queue;

import info.jupiter.net.ActionSender;
import info.jupiter.net.GameDecoder;
import info.jupiter.net.ISAACCipher;
import info.jupiter.net.Packet;
import info.jupiter.net.Session;

/**
 * Represents a non AI Player in the game world.
 * @author Advocatus <davidcntt@hotmail.com>
 *
 */
public class Player extends Entity {

	private ActionSender actionSender = new ActionSender(this);
	private String password;
	private Session session;
	private String username;
	private final ISAACCipher inCipher;
	private final ISAACCipher outCipher;
	private boolean members = true;


	public Player(PlayerDetails details) {
		super();
		this.session = details.getSession();
		this.username = details.getUsername();
		this.password = details.getPassword();
		this.inCipher = details.getInCipher();
		this.outCipher = details.getOutCipher();
	}

	public ActionSender getActionSender() {
		return actionSender;
	}

	public String getPassword() {
		return password;
	}

	public Session getSession() {
		return session;
	}

	public String getUsername() {
		return username;
	}
	
	public ISAACCipher getInCipher() {
		return inCipher;
	}

	public ISAACCipher getOutCipher() {
		return outCipher;
	}
	
	public boolean isLoggedIn() {
		return getSession().getDecoder() instanceof GameDecoder;
	}

	public void logout() throws Exception {
		World.getWorld().unregister(this);
		System.out.println(this + " has logged out.");
		// TODO: save this player
		
	}

	@Override
	public String toString() {
		return getUsername() == null ? "Player(" + getSession().getHost() + ")" : "Player(" + getUsername() + ":" + getPassword() + " - " + getSession().getHost() + ")";
	}

	public void write(Packet p) {
		if (getSession() != null)
			getSession().write(p);
	}

	public boolean isMembers() {
		return members;
	}	
	
	private Queue<Packet> packetQueue = new ArrayDeque<Packet>();
	private boolean[] packetReceived = new boolean[256];
	
	public boolean isPacketReceived(int opcode) {
		return packetReceived[opcode];
	}
	
	public void setPacketReceived(int opcode, boolean value) {
		packetReceived[opcode] = value;
	}
	public Queue<Packet> getPacketQueue() {
		return packetQueue;
	}
}
