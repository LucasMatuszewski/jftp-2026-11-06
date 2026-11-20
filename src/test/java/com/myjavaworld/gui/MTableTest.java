package com.myjavaworld.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class MTableTest {

	@Test
	public void testPopupOnSelectedRowPreservesMultipleSelection() throws Exception {
		assertEquals("[0, 1]", selectionSeenByPopupListener(1));
	}

	@Test
	public void testPopupOnUnselectedRowReplacesSelection() throws Exception {
		assertEquals("[2]", selectionSeenByPopupListener(2));
	}

	@Test
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
		assertNotNull(selection.get(), "The popup event should reach the listener");
		return Arrays.toString(selection.get());
	}
}
