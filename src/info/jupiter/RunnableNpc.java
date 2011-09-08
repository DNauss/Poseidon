package info.jupiter;

public class RunnableNpc implements Runnable {
	Stage stage;
	Npc npc;
	
	public RunnableNpc(Npc npc, Stage stage){
		this.npc = npc;
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
			System.out.println("updating for a player;");
			break;
		case POST_UPDATE:
			//reset update flags
			System.out.println("reseting for a player;");
			break;
		}		
	}

}
