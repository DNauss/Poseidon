package info.jupiter.net;

import info.jupiter.PlayerDetails;
import info.jupiter.World;

import java.nio.ByteBuffer;
import java.security.SecureRandom;

public class LoginDecoder317 implements AbstractDecoder {
	private static final int CONNECTED = 0;
	private static final int LOGGING_IN = 1;

	private Session session;

	public LoginDecoder317(Session session) {
		this.session = session;
	}

	private int stage = CONNECTED;

	public boolean decode(ByteBuffer data) {
		switch (stage) {	
		case CONNECTED:
			if (data.remaining() < 2) {
				data.compact();
				return false;
			}

			// Validate the request.
			int request = data.get() & 0xff;
			data.get(); // Name hash.
			if (request != 14) {
				System.err.println("Invalid login request: " + request);
				session.disconnect();
				return false;
			}

			// Write the response.
			PacketBuilder out = new PacketBuilder();
			out.putLong(0); // First 8 bytes are ignored by the client.
			out.put((byte) 0); // The response opcode, 0 for logging in.
			out.putLong(new SecureRandom().nextLong()); // SSK.
			session.write(out.toPacket());
			stage = LOGGING_IN;
			break;
		case LOGGING_IN:
			if (data.remaining() < 2) {
				data.compact();
				return false;
			}

			// Validate the login type.
			int loginType = data.get();
			if (loginType != 16 && loginType != 18) {
				System.err.println("Invalid login type: " + loginType);
				session.disconnect();
				return false;
			}

			// Ensure that we can read all of the login block.
			int blockLength = data.get() & 0xff;
			if (data.remaining() < blockLength) {
				data.flip();
				data.compact();
				return false;
			}

			// Read the login block.
			Packet in = new Packet(-1, new byte[blockLength]);
			data.get(in.getData(), 0, blockLength);
			in.getByte(); // Skip the magic ID value 255.

			// Validate the client version.
			int clientVersion = in.getShort();
			if (clientVersion != 317) {
				System.err.println("Invalid client version: " + clientVersion);
				session.disconnect();
				return false;
			}

			in.getByte(); // Skip the high/low memory version.

			// Skip the CRC keys.
			for (int i = 0; i < 9; i++) {
				in.getInt();
			}

			in.getByte(); // Skip RSA block length.
			// If we wanted to, we would decode RSA at this point.

			// Validate that the RSA block was decoded properly.
			int rsaOpcode = in.getByte();
			if (rsaOpcode != 10) {
				System.err.println("Unable to decode RSA block properly!");
				session.disconnect();
				return false;
			}

			// TODO: Set up the ISAAC ciphers.
			long clientHalf = in.getLong();
			long serverHalf = in.getLong();
			int[] sessionKey = { (int) (clientHalf >> 32), (int) clientHalf, (int) (serverHalf >> 32), (int) serverHalf };			
			ISAACCipher inCipher = new ISAACCipher(sessionKey);
			for(int i = 0; i < 4; i++) {
				sessionKey[i] += 50;
			}
			ISAACCipher outCipher = new ISAACCipher(sessionKey);

			// Read the user authentication.
			in.getInt(); // Skip the user ID.
			String username = in.getRS2String();
			String password = in.getRS2String();

			try {
				World.getWorld().login(new PlayerDetails(session, username, password, inCipher, outCipher));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			session.setDecoder(new GameDecoder(session));
			break;
		}
		return false;
	}
}
