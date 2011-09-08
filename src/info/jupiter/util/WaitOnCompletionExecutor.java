package info.jupiter.util;

import info.Constants;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Essentially a threadpool that asynchronously processes a series of tasks,
 * while simultaneously pausing the game logic and packet processing.
 * @author Advocatus <davidcntt@hotmail.com>
 *
 */
public class WaitOnCompletionExecutor extends ThreadPoolExecutor {

	/**
	 * The central latch which ensures that the game logic / packet processing
	 * waits until its completion.
	 */
	private CountDownLatch latch = null;
	
	/**
	 * Creates an asynchronous WaitOnCompletionexecutor.
	 * @param nThreads the number of threads to use.
	 */
	public WaitOnCompletionExecutor(int nThreads) {
		super(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}
	
	/**
	 * Creates an asynchronous WaitOnCompletionexecutor.
	 * @param nThreads the number of threads to use.
	 * @param threadFactory the thread factory to use.
	 */
	public WaitOnCompletionExecutor(int nThreads, ThreadFactory threadFactory) {
		super(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory);
	}
	
	/**
	 * Submits a list of tasks to the executor.
	 * @param list the list of tasks to process
	 */
	public void submit(List<Runnable> list) {
		latch = new CountDownLatch(list.size());
		for (Runnable r : list)
			execute(r);
	}
	
	/**
	 * Waits on the completion of all the tasks submitted to this executor.
	 */
	public void await() {
		try {
			latch.await(Constants.CYCLE_TIME, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Is run after every execution of a task.
	 */
	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		latch.countDown();
	}

	/**
	 * Shutdown hook for the executor, 
	 * hypothetically, you should either create a new executor, 
	 * or shutdown the rest of the server.
	 */
	@Override
	protected void terminated() {
		System.out.println("aww Damnit, we are shutting down");
	}
}
