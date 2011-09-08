package info.jupiter.util.test;

import info.jupiter.util.LinkedNodeList;

import java.util.LinkedList;

public class NodeTest {
	private static LinkedNodeList<Integer> nodeList = new LinkedNodeList<Integer>();
	//private static LinkedList<Integer> nodeList = new LinkedList<Integer>();

	private static SimpleTimer timer = new SimpleTimer();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		timer.reset();
		for (int i =0; i < 2000; i++)
			nodeList.add(i);
		System.out.println("elapsed: "+timer.elapsed());
		timer.reset();
		 nodeList.contains(1000);
		 System.out.println("elapsed: "+timer.elapsed());
		 timer.reset();
		System.out.println("size:" + nodeList.size());
		System.out.println("elapsed: "+timer.elapsed());
		// TODO Auto-generated method stub

	}

}
