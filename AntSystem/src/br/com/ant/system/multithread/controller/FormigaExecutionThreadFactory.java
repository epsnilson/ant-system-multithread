package br.com.ant.system.multithread.controller;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class FormigaExecutionThreadFactory implements ThreadFactory {

	AtomicInteger	poolNumber	= new AtomicInteger(0);

	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r);

		poolNumber.set(poolNumber.get() + 1);
		thread.setName("MultiThreadFormigas " + poolNumber.get());
		thread.setPriority(Thread.MAX_PRIORITY);

		return thread;
	}

}
