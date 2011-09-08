package info.jupiter.net;

import info.HostGateway;
import info.RS2Server;
import info.jupiter.Player;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Session {
//	private Map<String, Object> attribute = new HashMap<String, Object>();

	private Object attachment;
	private final ByteBuffer buffer;
	private AbstractDecoder decoder = new LoginDecoder317(this);
	private AbstractEncoder encoder = GameCodecManager.getGameEncoder();
	private final SelectionKey key;
	private SocketChannel socketChannel;

	public Session(SelectionKey key) {
		this.key = key;
		if (key != null) {
			socketChannel = (SocketChannel) key.channel();
		}
		buffer = ByteBuffer.allocateDirect(512);
	}

	public void disconnect() {
		System.out.println(getHost() + " disconnecting.");
		try {
			if (attachment != null && attachment instanceof Player)
				((Player) attachment).logout();
			socketChannel.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			HostGateway.exit(getSocketChannel().socket().getInetAddress().getHostAddress());
			RS2Server.getInstance().getSessionMap().remove(key);
			key.cancel();
		}
	}

	public String getHost() {
		return getSocketChannel().socket().getInetAddress().getHostAddress();
	}

	public final void incomingCycle() {
		try {
			if (socketChannel.read(buffer) == -1) {
				disconnect();
				return;
			}
			buffer.flip();
			while (buffer.hasRemaining()) {
				if (!decoder.decode(buffer))
					break;
			}
			buffer.clear();
		} catch (Exception ex) {
			ex.printStackTrace();
			disconnect();
		}
	}

	public final void write(Packet packet) {
		if (packet != null)
			encoder.write(this, packet);
	}

	public Object getAttachment() {
		return attachment;
	}

	public AbstractDecoder getDecoder() {
		return decoder;
	}

	public AbstractEncoder getEncoder() {
		return encoder;
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

	public void setDecoder(AbstractDecoder decoder) {
		this.decoder = decoder;
	}

	public void setEncoder(AbstractEncoder encoder) {
		this.encoder = encoder;
	}

}
