package info.jupiter;

/**
 * Contains player or npc related update flags.
 * @author Advocatus <davidcntt@hotmail.com>
 *
 */
public class UpdateFlags {
	
	/**
	 * A static String array of the valid update flags.
	 */
	private static String[] updateFlag = new String[] {
		"APPEARANCE",
		"CHAT",
		"GRAPHICS",
		"ANIMATION",
		"FORCED_CHAT",
		"FACE_ENTITY",
		"FACE_COORDINATE",
		"HIT",
		"HIT_2",
		"TRANSFORM"
	};

	/**
	 * The boolean array that contains updating flags.
	 */
	private boolean[] flags = new boolean[updateFlag.length];
	
	/**
	 * Checks whether an update is required.
	 * @return true if a single flag is set as true.
	 */
	public boolean isUpdateRequired() {
		for (int i = 0; i < updateFlag.length; i++)
			if (flags[i])
				return true;
		return false;
	}
	
	/**
	 * Flags a specified flag as being true.
	 * @param flag the flag to set to true.
	 */
	public void flag(String flag) {
		for (int i = 0; i < updateFlag.length; i++)
			if (updateFlag[i].equalsIgnoreCase(flag)) {
				flags[i] = true;
				return;
			}
		System.out.println("Error flagging flag: "+flag);
	}

	/**
	 * Gets the boolean value of a specified flag.
	 * @param flag the flag to get the boolean value for.
	 * @return the flag's boolean value.
	 */
	public boolean get(String flag) {
		for (int i = 0; i < updateFlag.length; i++)
			if (updateFlag[i].equalsIgnoreCase(flag))
				return flags[i];
		System.out.println("Error getting flag: "+flag);
		return false;
	}

	/**
	 * Resets all update flags to false.
	 */
	public void reset() {
		for (int i = 0; i < updateFlag.length; i++)
			flags[i] = false;
	}
}