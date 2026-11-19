package com.myjavaworld.jftp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import junit.framework.TestCase;

public class ConnectionProfileTest extends TestCase {

	public void testPostLoginCommandsIgnoreBlankLinesAndPreserveOrderAndSpacing() {
		RemoteHost profile = new RemoteHost();
		profile.setCommands("\r\nPWD\r\n  \n  SYST  \rTYPE I\n");

		assertTrue(Arrays.equals(new String[] { "PWD", "  SYST  ", "TYPE I" },
				profile.getCommands()));
		assertEquals("PWD\n  SYST  \nTYPE I\n", profile.getCommandsAsString());
	}

	public void testFavoriteIdentityUsesNameCaseInsensitively() {
		Favorite saved = new Favorite();
		saved.setName("Work Site");
		saved.setHostName("first.invalid");

		Favorite candidate = new Favorite();
		candidate.setName("work site");
		candidate.setHostName("second.invalid");

		assertEquals(saved, candidate);
		candidate.setName("Other Site");
		assertFalse(saved.equals(candidate));
	}

	public void testFavoriteSerializationPreservesConnectionOptions() throws Exception {
		Favorite saved = new Favorite();
		saved.setName("Test site");
		saved.setHostName("ftp.invalid");
		saved.setPort(2121);
		saved.setUser("synthetic-user");
		saved.setPassword("synthetic-password");
		saved.setInitialRemoteDirectory("/incoming");
		saved.setPassive(false);
		saved.setCommands("PWD\nSYST");

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bytes);
		out.writeObject(saved);
		out.close();
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
		Favorite restored;
		try {
			restored = (Favorite) in.readObject();
		} finally {
			in.close();
		}

		assertEquals(saved.getName(), restored.getName());
		assertEquals(saved.getHostName(), restored.getHostName());
		assertEquals(saved.getPort(), restored.getPort());
		assertEquals(saved.getUser(), restored.getUser());
		assertEquals(saved.getPassword(), restored.getPassword());
		assertEquals(saved.getInitialRemoteDirectory(), restored.getInitialRemoteDirectory());
		assertFalse(restored.isPassive());
		assertTrue(Arrays.equals(saved.getCommands(), restored.getCommands()));
	}
}
