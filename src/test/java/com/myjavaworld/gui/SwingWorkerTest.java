package com.myjavaworld.gui;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SwingWorkerTest {

	@Test
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

		assertTrue(completed.await(5, TimeUnit.SECONDS), "Completion callback did not run");
		assertFalse(constructOnEdt.get(), "Work should be off the event dispatch thread");
		assertTrue(finishedOnEdt.get(), "Completion should run on the event dispatch thread");
		assertSame(result, valueAtCompletion.get());
	}
}
