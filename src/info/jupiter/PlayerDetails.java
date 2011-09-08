package info.jupiter;

import info.jupiter.net.ISAACCipher;
import info.jupiter.net.Session;

/**
 * Contains details about a {@link Player} that has not logged in yet. 
 * @author Advocatus <davidcntt@hotmail.com>
 *
 */
public class PlayerDetails {
	
	/**
	 * The session.
	 */
	private Session session;
	
	/**
	 * The username.
	 */
	private String username;
	
	/**
	 * The password.
	 */
	private String password;
	
	/**
	 * The incoming ISAAC cipher.
	 */
	private ISAACCipher inCipher;
	
	/**
	 * The outgoing ISAAC cipher.
	 */
	private ISAACCipher outCipher;

	/**
	 * Creates a new PlayerDetails object which will (hopefully) later be a {@link Player}.
	 * @param session the session to set.
	 * @param username the username to set.
	 * @param password the password to set.
	 * @param inCipher the incoming ISAAC cipher to set.
	 * @param outCipher the outgoing ISAAC cipher to set.
	 */
	public PlayerDetails(Session session, String username, String password, ISAACCipher inCipher, ISAACCipher outCipher) {
		this.session = session;
		this.username = username;
		this.password = password;
		this.inCipher = inCipher;
		this.outCipher = outCipher;
	}

	/**
	 * Gets the session.
	 * @return the session.
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Gets the username.
	 * @return the username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Gets the password.
	 * @return the password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Gets the incoming ISAAC cipher.
	 * @return the incoming ISAAC cipher.
	 */
	public ISAACCipher getInCipher() {
		return inCipher;
	}

	/**
	 * Gets the outgoing ISAAC cipher.
	 * @return the outgoing ISAAC cipher.
	 */
	public ISAACCipher getOutCipher() {
		return outCipher;
	}
}