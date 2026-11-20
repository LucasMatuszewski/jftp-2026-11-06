package com.myjavaworld.jftp;

import java.io.File;
import java.nio.file.Files;
import java.util.Date;
import java.util.regex.Pattern;

import com.myjavaworld.util.DateFilter;
import com.myjavaworld.util.RegexFilter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LocalFileFilterTest {

	private static final long DAY = 24L * 60 * 60 * 1000;
	private File matching;
	private File otherName;
	private File older;
	private File directory;

	@BeforeEach
	protected void setUp() throws Exception {
		long now = System.currentTimeMillis();
		matching = Files.createTempFile("jftp-filter-", ".TXT").toFile();
		otherName = Files.createTempFile("jftp-filter-", ".bin").toFile();
		older = Files.createTempFile("jftp-filter-", ".txt").toFile();
		directory = Files.createTempDirectory("jftp-filter-").toFile();
		assertTrue(older.setLastModified(now - 3 * DAY));
	}

	@AfterEach
	protected void tearDown() throws Exception {
		for (File path : new File[] { matching, otherName, older, directory }) {
			if (path != null) {
				Files.deleteIfExists(path.toPath());
			}
		}
	}

	@Test
	public void testInclusionRequiresBothNameAndDate() {
		LocalFileFilter filter = filter(false);
		assertTrue(filter.accept(new LocalFile(matching)));
		assertFalse(filter.accept(new LocalFile(otherName)));
		assertFalse(filter.accept(new LocalFile(older)));
	}

	@Test
	public void testExclusionRetainsFilesThatFailEitherCriterion() {
		LocalFileFilter filter = filter(true);
		assertFalse(filter.accept(new LocalFile(matching)));
		assertTrue(filter.accept(new LocalFile(otherName)));
		assertTrue(filter.accept(new LocalFile(older)));
	}

	@Test
	public void testDirectoriesRemainBrowsableWithEitherFilterMode() {
		LocalFile child = new LocalFile(directory);
		assertTrue(filter(false).accept(child));
		assertTrue(filter(true).accept(child));
	}

	private LocalFileFilter filter(boolean exclusion) {
		long timestamp = matching.lastModified();
		return new LocalFileFilter(new RegexFilter(".*\\.txt", Pattern.CASE_INSENSITIVE),
				new DateFilter(new Date(timestamp - DAY), new Date(timestamp + DAY)),
				true, exclusion);
	}
}
