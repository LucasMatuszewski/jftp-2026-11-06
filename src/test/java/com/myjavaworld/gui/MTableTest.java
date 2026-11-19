package com.myjavaworld.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

import junit.framework.TestCase;

public class MTableTest extends TestCase {

	public void testPopupOnSelectedRowPreservesMultipleSelection() throws Exception {
		assertEquals("[0, 1]", selectionSeenByPopupListener(1));
	}

	public void testPopupOnUnselectedRowReplacesSelection() throws Exception {
		assertEquals("[2]", selectionSeenByPopupListener(2));
	}

	public void testPopupBelowRowsClearsSelection() throws Exception {
		assertEquals("[]", selectionSeenByPopupListener(3));
	}

	private String selectionSeenByPopupListener(final int popupRow) throws Exception {
		final AtomicReference<int[]> selection = new AtomicReference<int[]>();
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				final MTable table = new MTable(3, 1);
				int rowHeight = table.getRowHeight();
				table.setSize(100, 4 * rowHeight);
				table.setRowSelectionInterval(0, 1);
				table.addMouseListener(new MouseAdapter() {
					public void mouseReleased(MouseEvent event) {
						selection.set(table.getSelectedRows());
					}
				});
				table.dispatchEvent(new MouseEvent(table, MouseEvent.MOUSE_RELEASED,
						0L, 0, 1, popupRow * rowHeight + 1, 1, true, MouseEvent.BUTTON3));
			}
		});
		assertNotNull("The popup event should reach the listener", selection.get());
		return Arrays.toString(selection.get());
	}
}
