package info.jupiter.util.test;

public class SimpleTimer {

	private long cachedTime;

	public SimpleTimer() {
		reset();
	}

	
	public void reset() {
		//cachedTime = System.currentTimeMillis();
		cachedTime = System.nanoTime();
	}

	public long elapsed() {
		//return System.currentTimeMillis() - cachedTime;
		return System.nanoTime() - cachedTime;
	}
}
