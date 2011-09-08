package info.jupiter;

import info.jupiter.net.Packet.Type;
import info.jupiter.net.PacketBuilder;

public class RunnablePlayer implements Runnable {
	Stage stage;
	Player player;
	
	public RunnablePlayer(Player player, Stage stage){
		this.player = player;
		this.stage = stage;
	}
	
	@Override
	public void run() {
		switch (stage) {
		case PRE_UPDATE:
			//Calculate walking path
			System.out.println("walking for a player;");
			break;
		case UPDATE:	
			//Update
			/* a dummy encoder for the packet */
			PacketBuilder builder = new PacketBuilder(81, Type.VARIABLE_SHORT);
			builder.startBitAccess();
			builder.putBits(1, 1); /* this player has updated */
			builder.putBits(2, 3); /* indicates we teleported */
			builder.putBits(2, 0); /* height level */
			builder.putBits(1, 1); /* should the walk queue be discarded? */
			builder.putBits(1, 1); /* is there a block update? */
			builder.putBits(7, 48); /* local Y coordinate */
			builder.putBits(7, 48); /* local X coordinate */
			builder.putBits(8, 0); /* number of other players */
			builder.putBits(11, 2047); /* magic id to indicate blocks follow */
			builder.finishBitAccess();
			builder.put((byte)0); /* empty mask - no blocks for this player */
			player.write(builder.toPacket());
			System.out.println("updating for a player;");
			break;
		case POST_UPDATE:
			//reset update flags
			System.out.println("reseting for a player;");
			break;
		}		
	}

}
