package info;

import info.jupiter.WorldUpdater;
import info.jupiter.net.Session;
import info.jupiter.util.WaitOnCompletionExecutor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RS2Server implements Runnable {

	private static RS2Server rs2Server;
	private Selector selector;
	private InetSocketAddress address;
	private ServerSocketChannel serverChannel;
	private Map<SelectionKey, Session> sessionMap;
	private static final ScheduledExecutorService logicService = Executors.newSingleThreadScheduledExecutor();

	private WaitOnCompletionExecutor threadPool = new WaitOnCompletionExecutor(Runtime.getRuntime().availableProcessors());

	public WaitOnCompletionExecutor getThreadPool() {
		return threadPool;
	}

	public static void main(String[] args) {
		rs2Server = new RS2Server();
		logicService.scheduleAtFixedRate(rs2Server, 0, Constants.CYCLE_TIME, TimeUnit.MILLISECONDS);
	}

	private RS2Server() {
		try {
			address = new InetSocketAddress(Constants.PORT);
			System.out.println("Initializing " + Constants.NAME + " on " + address + "!");
			selector = Selector.open();
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			serverChannel.socket().bind(address);
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
			sessionMap = new HashMap<SelectionKey, Session>();
			System.out.println("Online!");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void run() {
		try {
			selector.selectNow();
			for (SelectionKey selectionKey : selector.selectedKeys()) {
				if (selectionKey.isAcceptable()) {
					accept(); // Accept a new connection.
				}
				if (selectionKey.isReadable()) {
					sessionMap.get(selectionKey).incomingCycle();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			WorldUpdater.getWorldUpdater().run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void accept() throws IOException {
		SocketChannel socket;

		for (int i = 0; i < 10; i++) {
			socket = serverChannel.accept();
			if (socket == null) {
				break;
			}
			if (!HostGateway.enter(socket.socket().getInetAddress().getHostAddress())) {
				socket.close();
				continue;
			}
			socket.configureBlocking(false);
			SelectionKey selectionKey = socket.register(selector, SelectionKey.OP_READ);
			Session session = new Session(selectionKey);
			System.out.println("Accepted " + session + ".");
			getSessionMap().put(selectionKey, session);
		}
	}

	public Map<SelectionKey, Session> getSessionMap() {
		return sessionMap;
	}

	public static RS2Server getInstance() {
		return rs2Server;
	}

}
