package hx.data.mybatis.reflect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author 李岩飞
 * @email eliyanfei@126.com
 * 2016年11月2日 下午4:06:47
 *
 */
public final class ThreadPool<T extends Runnable> extends Thread {
	public static final int DEFAULT_SIZE = 50;
	private final ExecutorService services;
	private final List<T> threadRunables;
	protected final boolean useThread;

	public ThreadPool() {
		this(10);
	}

	/**
	 * 可以指定是否采用多线程执行工作,默认线程池大小为50
	 */
	public ThreadPool(final boolean useThread) {
		this(useThread, DEFAULT_SIZE);
	}

	public ThreadPool(final int size) {
		this(true, size);
	}

	/**
	 * 可以指定是否采用多线程执行工作,并且需要指定线程池大小
	 */
	public ThreadPool(final boolean useThread, final int size) {
		super();
		this.useThread = useThread;
		this.services = Executors.newFixedThreadPool(size);
		this.threadRunables = new ArrayList<T>(size);
	}

	public void execute(final T threadRunable) {
		this.threadRunables.add(threadRunable);
		if (useThread)
			this.services.execute(threadRunable);
		else {
			final Thread innerThread = new Thread(threadRunable);
			innerThread.start();
			try {
				innerThread.join();
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return the threads
	 */
	public List<T> getThreadRunables() {
		return Collections.unmodifiableList(threadRunables);
	}

	public void shutdown() {
		this.services.shutdown();
	}

	public boolean shutdown(final long timeOut) throws InterruptedException {
		return shutdown(timeOut, TimeUnit.SECONDS);
	}

	public boolean shutdown(final long timeOut, final TimeUnit timeUnit) throws InterruptedException {
		this.services.shutdown();
		return this.services.awaitTermination(timeOut, timeUnit);
	}

	/**
	 * 等待多少秒后结束所有线程
	 */
	public boolean awaitTermination(final long timeOut) throws InterruptedException {
		return awaitTermination(timeOut, TimeUnit.SECONDS);
	}

	/**
	 * 等待多少单位时间后结束所有线程
	 */
	public boolean awaitTermination(final long timeOut, final TimeUnit timeUnit) throws InterruptedException {
		return this.services.awaitTermination(timeOut, timeUnit);
	}
}
