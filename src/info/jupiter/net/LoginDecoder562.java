package info.jupiter.net;

import info.Constants;
import info.jupiter.PlayerDetails;
import info.jupiter.World;
import info.jupiter.util.Misc;

import java.nio.ByteBuffer;

public class LoginDecoder562 implements AbstractDecoder {

	private static final int UKEYS = -2;
	private static final int INITIAL = -1;
	private static final int CONNECTED = 0;
	private static final int LOGGING_IN = 1;

	private Session session;

	public LoginDecoder562(Session session) {
		this.session = session;
	}

	private int stage = CONNECTED;
	private int NAME_HASH;

	@Override
	public boolean decode(ByteBuffer data) {
		try {
			switch (stage) {
			case UKEYS:
				if (data.remaining() >= 8) {
					for (int i = 0; i < 8; i++) {
						data.get();
					}
					PacketBuilder ukeys = new PacketBuilder();
					for (int key : Constants.UPDATE_KEYS) {
						ukeys.put((byte) key);
					}
					session.write(ukeys.toPacket());
					session.disconnect();
					return true;
				}
				data.rewind();
				return false;
			case INITIAL:
				if (data.remaining() >= 3) {
					data.get();
					int clientVersion = data.getShort();
					if (clientVersion == 562) {
						PacketBuilder u1Response = new PacketBuilder();
						u1Response.put((byte) 0);
						session.write(u1Response.toPacket());
						this.stage = UKEYS;
					} else {
						PacketBuilder u1Response = new PacketBuilder();
						u1Response.put((byte) 6);
						session.write(u1Response.toPacket());
						session.disconnect();
					}
					return true;
				}
				data.rewind();
				return false;
			case CONNECTED: //first login packets
				if (data.remaining() >= 2) {
					int protocolId = data.get() & 0xff;
					int nameHash = data.get() & 0xff;
					System.out.println(protocolId);
					if (protocolId == 15) {
						this.stage = INITIAL;
					} else {
						long serverSessionKey = ((long) (java.lang.Math.random() * 99999999D) << 32) + (long) (java.lang.Math.random() * 99999999D);
						PacketBuilder s1Response = new PacketBuilder();
						s1Response.put((byte) 0).putLong(serverSessionKey);
						session.write(s1Response.toPacket());
						this.stage = LOGGING_IN;
						this.NAME_HASH = nameHash;
					}
					return true;
				} else {
					data.rewind();
					return false;
				}
			case LOGGING_IN:
				@SuppressWarnings("unused")
				int loginType = -1, blockLength = -1;
				if (data.remaining() >= 3) {
					loginType = data.get() & 0xff;
					blockLength = data.getShort() & 0xff;
				} else {
					data.rewind();
					return false;
				}
				if (data.remaining() >= blockLength) {
					Packet in = new Packet(-1, new byte[blockLength]);
					data.get(in.getData(), 0, blockLength);
					@SuppressWarnings("unused")
					int loginEncryptPacketSize = blockLength - (36 + 1 + 1 + 2); // can't be negative
					int clientVersion = in.getInt();
					if (clientVersion != 562) {
						System.err.println("Invalid client version: " + clientVersion);
						session.disconnect();
						return false;
					}
					in.skip(30);
					in.readRS2String(); // settings string?
					in.skip(127);
					long clientHalf = in.getLong();
					long serverHalf = in.getLong();
					long l = in.getLong();
					int hash = (int) (31 & l >> 16);
					if (hash != NAME_HASH) {
						session.disconnect();
						return true;
					}
					String username = Misc.longToPlayerName(l);
					String password = in.readRS2String();
					int[] sessionKey = { (int) (clientHalf >> 32), (int) clientHalf, (int) (serverHalf >> 32), (int) serverHalf };			
					ISAACCipher inCipher = new ISAACCipher(sessionKey);
					for(int i = 0; i < 4; i++) {
						sessionKey[i] += 50;
					}
					ISAACCipher outCipher = new ISAACCipher(sessionKey);
					System.out.println("Login request: [username=" + username + ",password=" + password + "].");	
					try {
						World.getWorld().login(new PlayerDetails(session, username, password, inCipher, outCipher));
					} catch (Exception e) {
						e.printStackTrace();
					}
					session.setDecoder(new GameDecoder(session));
					return true;
				} else {
					data.rewind();
					return false;
				}
			}
		} catch (Exception e) {
			//logger.stackTrace(e);
		}
		return false;
	}
}
