package com.myjavaworld.gui;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

import junit.framework.TestCase;

public class SwingWorkerTest extends TestCase {

	public void testBackgroundResultIsAvailableWhenCompletionRunsOnEdt() throws Exception {
		final CountDownLatch completed = new CountDownLatch(1);
		final AtomicBoolean constructOnEdt = new AtomicBoolean();
		final AtomicBoolean finishedOnEdt = new AtomicBoolean();
		final AtomicReference<Object> valueAtCompletion = new AtomicReference<Object>();
		final Object result = new Object();
		final SwingWorker worker = new SwingWorker() {
			public Object construct() {
				constructOnEdt.set(SwingUtilities.isEventDispatchThread());
				return result;
			}

			public void finished() {
				finishedOnEdt.set(SwingUtilities.isEventDispatchThread());
				valueAtCompletion.set(getValue());
				completed.countDown();
			}
		};

		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				worker.start();
			}
		});

		assertTrue("Completion callback did not run", completed.await(5, TimeUnit.SECONDS));
		assertFalse("Work should be off the event dispatch thread", constructOnEdt.get());
		assertTrue("Completion should run on the event dispatch thread", finishedOnEdt.get());
		assertSame(result, valueAtCompletion.get());
	}
}
