package com.myjavaworld.zip;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.myjavaworld.jftp.LocalFileFilter;
import com.myjavaworld.util.RegexFilter;

import junit.framework.TestCase;

public class ZipRoundTripTest extends TestCase {

	private Path root;

	protected void setUp() throws IOException {
		root = Files.createTempDirectory("jftp-zip-");
	}

	protected void tearDown() throws IOException {
		if (root == null) {
			return;
		}
		Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
			public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			public FileVisitResult postVisitDirectory(Path directory, IOException error) throws IOException {
				if (error != null) {
					throw error;
				}
				Files.delete(directory);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	public void testFilteredDirectoryRestoresRelativePathsAndFileBytes() throws IOException {
		Path source = Files.createDirectory(root.resolve("source"));
		Path nested = Files.createDirectory(source.resolve("nested"));
		Files.createDirectory(nested.resolve("empty"));
		byte[] payload = { 0, 10, 13, (byte) 255 };
		Files.write(nested.resolve("payload.bin"), payload);
		Files.write(nested.resolve("ignore.txt"), new byte[] { 1 });
		File archive = root.resolve("files.zip").toFile();

		Zip zip = new Zip(archive);
		zip.setRelativeTo(source.toFile());
		zip.setFilter(new LocalFileFilter(new RegexFilter(".*\\.bin", 0), null));
		zip.open();
		try {
			zip.addEntry(nested.toFile());
		} finally {
			zip.close();
		}

		ZipFile entries = new ZipFile(archive);
		try {
			assertNotNull(entries.getEntry("nested/payload.bin"));
			ZipEntry emptyDirectory = entries.getEntry("nested/empty/");
			assertNotNull("The archive should preserve empty directories", emptyDirectory);
			assertTrue(emptyDirectory.isDirectory());
			assertNull(entries.getEntry("nested/ignore.txt"));
		} finally {
			entries.close();
		}

		Path destination = Files.createDirectory(root.resolve("restored"));
		Unzip unzip = new Unzip(archive);
		unzip.setTargetDirectory(destination.toFile());
		unzip.open();
		try {
			unzip.unzip();
		} finally {
			unzip.close();
		}

		assertTrue(Arrays.equals(payload, Files.readAllBytes(destination.resolve("nested/payload.bin"))));
		assertTrue(Files.isDirectory(destination.resolve("nested/empty")));
		assertFalse(Files.exists(destination.resolve("nested/ignore.txt")));
	}
}
