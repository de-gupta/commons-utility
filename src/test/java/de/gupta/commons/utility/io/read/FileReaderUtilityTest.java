package de.gupta.commons.utility.io.read;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FileReaderUtility Tests")
final class FileReaderUtilityTest
{
	private static final String TEST_FILE = "testFile.txt";
	private static final String EMPTY_FILE = "emptyFile.txt";
	private static final String NON_EXISTENT_FILE = "nonExistentFile.txt";
	private static final String EXPECTED_CONTENT =
			"This is a test file content.\nIt has multiple lines.\nFor testing the FileReaderUtility class.";
	@TempDir
	Path tempDir;

	@BeforeEach
	void setup() throws IOException
	{
		final Path testFilePath = tempDir.resolve(TEST_FILE);
		Files.writeString(testFilePath, EXPECTED_CONTENT);

		final Path emptyFilePath = tempDir.resolve(EMPTY_FILE);
		Files.createFile(emptyFilePath);
	}

	@Test
	@DisplayName("Valid file path should return correct content")
	void validFilePathShouldReturnCorrectContent()
	{
		String content = FileReaderUtility.readFileContent(tempDir.toString(), TEST_FILE);
		assertThat(content)
				.as("Valid file path should return correct content")
				.isEqualTo(EXPECTED_CONTENT);
	}

	@Test
	@DisplayName("Empty file should return empty string")
	void emptyFileShouldReturnEmptyString()
	{
		String content = FileReaderUtility.readFileContent(tempDir.toString(), EMPTY_FILE);
		assertThat(content)
				.as("Empty file should return empty string")
				.isEqualTo("");
	}

	@Test
	@DisplayName("Directory path with trailing slash should work correctly")
	void directoryPathWithTrailingSlashShouldWorkCorrectly()
	{
		String content = FileReaderUtility.readFileContent(tempDir.toString() + "\\", TEST_FILE);
		assertThat(content)
				.as("Directory path with trailing slash should work correctly")
				.isEqualTo(EXPECTED_CONTENT);
	}

	@Test
	@DisplayName("Absolute path should work correctly")
	void absolutePathShouldWorkCorrectly()
	{
		String content = FileReaderUtility.readFileContent(tempDir.toAbsolutePath().toString(), TEST_FILE);
		assertThat(content)
				.as("Absolute path should work correctly")
				.isEqualTo(EXPECTED_CONTENT);
	}

	@Test
	@DisplayName("Null directory should throw IllegalArgumentException")
	void nullDirectoryShouldThrowIllegalArgumentException()
	{
		assertThatThrownBy(() -> FileReaderUtility.readFileContent(null, TEST_FILE))
				.as("Null directory should throw IllegalArgumentException")
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("File not found: null/" + TEST_FILE);
	}

	@Test
	@DisplayName("Empty directory should throw IllegalArgumentException")
	void emptyDirectoryShouldThrowIllegalArgumentException()
	{
		assertThatThrownBy(() -> FileReaderUtility.readFileContent("", TEST_FILE))
				.as("Empty directory should throw IllegalArgumentException")
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("File not found: /" + TEST_FILE);
	}

	@Test
	@DisplayName("Whitespace directory should throw IllegalArgumentException")
	void whitespaceDirectoryShouldThrowIllegalArgumentException()
	{
		assertThatThrownBy(() -> FileReaderUtility.readFileContent(" ", TEST_FILE))
				.as("Whitespace directory should throw IllegalArgumentException")
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("File not found:  /" + TEST_FILE);
	}

	@Test
	@DisplayName("Null filename should throw IllegalArgumentException")
	void nullFilenameShouldThrowIllegalArgumentException()
	{
		assertThatThrownBy(() -> FileReaderUtility.readFileContent(tempDir.toString(), null))
				.as("Null filename should throw IllegalArgumentException")
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("File not found: " + tempDir.toString() + "/null");
	}

	@Test
	@DisplayName("Empty filename should throw IllegalArgumentException")
	void emptyFilenameShouldThrowIllegalArgumentException()
	{
		assertThatThrownBy(() -> FileReaderUtility.readFileContent(tempDir.toString(), ""))
				.as("Empty filename should throw IllegalArgumentException")
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("File not found: " + tempDir.toString() + "/");
	}

	@Test
	@DisplayName("Whitespace filename should throw InvalidPathException")
	void whitespaceFilenameShouldThrowInvalidPathException()
	{
		assertThatThrownBy(() -> FileReaderUtility.readFileContent(tempDir.toString(), " "))
				.as("Whitespace filename should throw InvalidPathException")
				.isInstanceOf(InvalidPathException.class)
				.hasMessageContaining("Trailing char < > at index");
	}

	@Test
	@DisplayName("Non-existent file should throw IllegalArgumentException")
	void nonExistentFileShouldThrowIllegalArgumentException()
	{
		assertThatThrownBy(() -> FileReaderUtility.readFileContent(tempDir.toString(), NON_EXISTENT_FILE))
				.as("Non-existent file should throw IllegalArgumentException")
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("File not found: " + tempDir.toString() + "/" + NON_EXISTENT_FILE);
	}

	@Test
	@DisplayName("Non-existent directory should throw IllegalArgumentException")
	void nonExistentDirectoryShouldThrowIllegalArgumentException()
	{
		assertThatThrownBy(() -> FileReaderUtility.readFileContent("nonExistentDirectory", TEST_FILE))
				.as("Non-existent directory should throw IllegalArgumentException")
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("File not found: nonExistentDirectory/" + TEST_FILE);
	}
}