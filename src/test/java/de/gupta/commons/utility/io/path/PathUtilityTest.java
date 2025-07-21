package de.gupta.commons.utility.io.path;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PathUtility Tests")
final class PathUtilityTest
{

	@Nested
	@DisplayName("convertToPath Tests")
	class ConvertToPathTests
	{

		@ParameterizedTest(name = "{1}")
		@MethodSource("convertToPathValidCasesProvider")
		@DisplayName("Test convertToPath with valid inputs")
		void convertToPath_validInputs(String input, String displayName)
		{
			Path result = PathUtility.convertToPath(input);

			assertThat(result)
					.as("Path should be created from valid input: %s", input)
					.isNotNull()
					.isEqualTo(Path.of(input));
		}

		@ParameterizedTest(name = "{1}")
		@MethodSource("convertToPathInvalidCasesProvider")
		@DisplayName("Test convertToPath with invalid inputs")
		void convertToPath_invalidInputs(String input, String displayName)
		{
			assertThatThrownBy(() -> PathUtility.convertToPath(input))
					.as("Should throw IllegalArgumentException for invalid input: %s", input)
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("Path cannot be null or blank");
		}

		private static Stream<Arguments> convertToPathValidCasesProvider()
		{
			return Stream.of(
					Arguments.of("file.txt", "Simple filename"),
					Arguments.of("dir/file.txt", "Path with directory"),
					Arguments.of("C:/Users/file.txt", "Absolute path (Windows style)"),
					Arguments.of("/home/user/file.txt", "Absolute path (Unix style)"),
					Arguments.of("dir with spaces/file.txt", "Path with spaces"),
					Arguments.of("dir/file with spaces.txt", "Filename with spaces"),
					Arguments.of("dir/file-with-hyphens.txt", "Path with hyphens"),
					Arguments.of("dir/file_with_underscores.txt", "Path with underscores"),
					Arguments.of("dir/file.with.dots.txt", "Path with multiple dots"),
					Arguments.of("dir/file+with+plus.txt", "Path with plus signs"),
					Arguments.of("dir/file(with)parentheses.txt", "Path with parentheses")
			);
		}

		private static Stream<Arguments> convertToPathInvalidCasesProvider()
		{
			return Stream.of(
					Arguments.of(null, "Null path"),
					Arguments.of("", "Empty path"),
					Arguments.of("   ", "Blank path"),
					Arguments.of("\t", "Tab character"),
					Arguments.of("\n", "Newline character"),
					Arguments.of("\r\n", "Carriage return and newline")
			);
		}
	}

	@Nested
	@DisplayName("fullPath(String, String) Tests")
	class FullPathStringStringTests
	{

		@ParameterizedTest(name = "{2}")
		@MethodSource("fullPathStringStringValidCasesProvider")
		@DisplayName("Test fullPath(String, String) with valid inputs")
		void fullPath_stringString_validInputs(String directory, String fileName, String displayName)
		{
			Path result = PathUtility.fullPath(directory, fileName);

			Path expected = Path.of(directory).resolve(fileName);

			assertThat(result)
					.as("Full path should be correctly constructed from directory '%s' and filename '%s'", directory,
							fileName)
					.isNotNull()
					.isEqualTo(expected);
		}

		@ParameterizedTest(name = "{2}")
		@MethodSource("fullPathStringStringInvalidCasesProvider")
		@DisplayName("Test fullPath(String, String) with invalid inputs")
		void fullPath_stringString_invalidInputs(String directory, String fileName, String displayName)
		{
			assertThatThrownBy(() -> PathUtility.fullPath(directory, fileName))
					.as("Should throw IllegalArgumentException for invalid directory '%s'", directory)
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("Path cannot be null or blank");
		}

		private static Stream<Arguments> fullPathStringStringValidCasesProvider()
		{
			return Stream.of(
					Arguments.of("dir", "file.txt", "Simple directory and filename"),
					Arguments.of("parent/dir", "file.txt", "Nested directory and filename"),
					Arguments.of("C:/Users", "file.txt", "Absolute Windows path and filename"),
					Arguments.of("/home/user", "file.txt", "Absolute Unix path and filename"),
					Arguments.of("dir with spaces", "file.txt", "Directory with spaces"),
					Arguments.of("dir", "file with spaces.txt", "Filename with spaces"),
					Arguments.of("dir/subdir", "../file.txt", "Filename with parent directory reference"),
					Arguments.of("dir", "./file.txt", "Filename with current directory reference")
			);
		}

		private static Stream<Arguments> fullPathStringStringInvalidCasesProvider()
		{
			return Stream.of(
					Arguments.of(null, "file.txt", "Null directory"),
					Arguments.of("", "file.txt", "Empty directory"),
					Arguments.of("   ", "file.txt", "Blank directory"),
					Arguments.of("\t", "file.txt", "Tab character directory"),
					Arguments.of("\n", "file.txt", "Newline character directory"),
					Arguments.of("\r\n", "file.txt", "Carriage return and newline directory")
			);
		}
	}

	@Nested
	@DisplayName("fullPath(SequencedCollection, String) Tests")
	class FullPathSequencedCollectionStringTests
	{

		@ParameterizedTest(name = "{2}")
		@MethodSource("fullPathSequencedCollectionStringValidCasesProvider")
		@DisplayName("Test fullPath(SequencedCollection, String) with valid inputs")
		void fullPath_sequencedCollection_validInputs(List<String> directories, String fileName, String displayName)
		{
			Path result = PathUtility.fullPath(directories, fileName);

			Path expected = directories.stream()
									   .map(Path::of)
									   .reduce(Path::resolve)
									   .orElseThrow()
									   .resolve(fileName);

			assertThat(result)
					.as("Full path should be correctly constructed from directories %s and filename '%s'", directories,
							fileName)
					.isNotNull()
					.isEqualTo(expected);
		}

		@ParameterizedTest(name = "{2}")
		@MethodSource("fullPathSequencedCollectionStringInvalidCasesProvider")
		@DisplayName("Test fullPath(SequencedCollection, String) with invalid inputs")
		void fullPath_sequencedCollection_invalidInputs(List<String> directories, String fileName, String displayName)
		{
			assertThatThrownBy(() -> PathUtility.fullPath(directories, fileName))
					.as("Should throw IllegalArgumentException for invalid directories %s", directories)
					.isInstanceOf(IllegalArgumentException.class)
					.hasMessageContaining("Path cannot be null or blank");
		}

		@Test
		@DisplayName("Test fullPath with null collection")
		void fullPath_nullCollection()
		{
			List<String> nullList = null;
			assertThatThrownBy(() -> PathUtility.fullPath(nullList, "file.txt"))
					.as("Should throw NullPointerException for null collection")
					.isInstanceOf(NullPointerException.class);
		}

		private static Stream<Arguments> fullPathSequencedCollectionStringValidCasesProvider()
		{
			return Stream.of(
					Arguments.of(
							new ArrayList<>(List.of("dir1", "dir2")),
							"file.txt",
							"ArrayList with two directories"
					),
					Arguments.of(
							new LinkedList<>(List.of("parent", "child")),
							"file.txt",
							"LinkedList with two directories"
					),
					Arguments.of(
							new ArrayList<>(List.of("single")),
							"file.txt",
							"Single directory in collection"
					),
					Arguments.of(
							new ArrayList<>(List.of("dir1", "dir2", "dir3", "dir4")),
							"file.txt",
							"Multiple nested directories"
					),
					Arguments.of(
							new ArrayList<>(List.of("C:", "Users", "Documents")),
							"file.txt",
							"Windows-style path components"
					),
					Arguments.of(
							new ArrayList<>(List.of("home", "user", "documents")),
							"file.txt",
							"Unix-style path components"
					),
					Arguments.of(
							new ArrayList<>(List.of("dir with spaces", "subdir")),
							"file.txt",
							"Directory with spaces"
					),
					Arguments.of(
							new ArrayList<>(List.of("dir", "subdir")),
							"file with spaces.txt",
							"Filename with spaces"
					)
			);
		}

		private static Stream<Arguments> fullPathSequencedCollectionStringInvalidCasesProvider()
		{
			List<String> listWithNulls = new ArrayList<>();
			listWithNulls.add(null);
			listWithNulls.add(null);

			return Stream.of(
					Arguments.of(
							new ArrayList<String>(),
							"file.txt",
							"Empty collection"
					),
					Arguments.of(
							new ArrayList<>(Arrays.asList(null, "dir2")),
							"file.txt",
							"Collection with null element"
					),
					Arguments.of(
							new ArrayList<>(Arrays.asList("dir1", "")),
							"file.txt",
							"Collection with empty string element"
					),
					Arguments.of(
							new ArrayList<>(Arrays.asList("dir1", "   ")),
							"file.txt",
							"Collection with blank string element"
					),
					Arguments.of(
							listWithNulls,
							"file.txt",
							"Collection with all null elements"
					),
					Arguments.of(
							new ArrayList<>(List.of("", "")),
							"file.txt",
							"Collection with all empty elements"
					),
					Arguments.of(
							new ArrayList<>(List.of("   ", "\t")),
							"file.txt",
							"Collection with all blank elements"
					)
			);
		}
	}
}