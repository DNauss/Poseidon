package info.jupiter.net;

import info.RS2Server;

/**
 * Manages all the encoders and decoders associated with this {@link RS2Server}.
 * @author Advocatus <davidcntt@hotmail.com>
 *
 */
public class GameCodecManager {
	
	/**
	 * The global login decoder instance.
	 */
	private static final AbstractDecoder loginDecoder = new LoginDecoder317(null);
	
	/**
	 * The global packet decoder instance.
	 */
	private static final AbstractDecoder gameDecoder = new GameDecoder(null);

	/**
	 * The global packet encoder instance.
	 */
	private static final AbstractEncoder gameEncoder = new GameEncoder();
	
	/**
	 * Gets the global login decoder instance.
	 * @return the global login decoder instance.
	 */
	public static AbstractDecoder getLoginDecoder() {
		return loginDecoder;
	}

	/**
	 * Gets the global packet decoder instance.
	 * @return the global packet decoder instance.
	 */
	public static AbstractDecoder getGameDecoder() {
		return gameDecoder;
	}
	
	/**
	 * Gets the global packet encoder instance.
	 * @return the global packet encoder instance.
	 */
	public static AbstractEncoder getGameEncoder() {
		return gameEncoder;
	}
}
