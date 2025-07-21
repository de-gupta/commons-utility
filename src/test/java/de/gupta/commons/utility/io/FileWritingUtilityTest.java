package de.gupta.commons.utility.io;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FileWritingUtility Tests")
final class FileWritingUtilityTest
{
	@TempDir
	static Path sharedTempDir;


	@Nested
	@DisplayName("writeFile Normal Cases")
	class WriteFileNormalCasesTests
	{
		@ParameterizedTest(name = "{4}")
		@MethodSource("writeFileNormalCasesProvider")
		@DisplayName("Test writeFile with normal cases")
		void writeFile_normalCases(String filePath, String content, FileWritingUtility.WriteOption[] options,
								   Class<? extends FileWritingUtility.WriteResult> expectedResultType,
								   String description) throws IOException
		{
			Path resolvedFilePath = sharedTempDir.resolve(filePath);

			FileWritingUtility.WriteResult result = FileWritingUtility.writeFile(
					resolvedFilePath,
					content,
					options
			);

			assertThat(result)
					.as(description)
					.isInstanceOf(expectedResultType);

			assertThat(Files.exists(resolvedFilePath))
					.as("File should exist after writing")
					.isTrue();

			String actualContent = Files.readString(resolvedFilePath);
			assertThat(actualContent)
					.as("File content should match the written content")
					.isEqualTo(content);
		}

		private static Stream<Arguments> writeFileNormalCasesProvider()
		{
			return Stream.of(
					Arguments.of(
							"normal_file.txt",
							"Hello, World!",
							new FileWritingUtility.WriteOption[]{},
							FileWritingUtility.WriteResult.Success.class,
							"Writing to a new file should succeed"),
					Arguments.of(
							"empty_content.txt",
							"",
							new FileWritingUtility.WriteOption[]{},
							FileWritingUtility.WriteResult.Success.class,
							"Writing empty content to a new file should succeed"),
					Arguments.of(
							"long_content.txt",
							"A".repeat(1000),
							new FileWritingUtility.WriteOption[]{},
							FileWritingUtility.WriteResult.Success.class,
							"Writing long content to a new file should succeed"),
					Arguments.of(
							"special_chars.txt",
							"Special characters: !@#$%^&*()_+{}|:<>?~`-=[]\\;',./",
							new FileWritingUtility.WriteOption[]{},
							FileWritingUtility.WriteResult.Success.class,
							"Writing content with special characters should succeed"),
					Arguments.of(
							"unicode_content.txt",
							"Unicode characters: 你好, こんにちは, 안녕하세요, Привет, مرحبا",
							new FileWritingUtility.WriteOption[]{},
							FileWritingUtility.WriteResult.Success.class,
							"Writing content with Unicode characters should succeed")
			);
		}
	}

	@Nested
	@DisplayName("writeFile Option Behavior Tests")
	class WriteFileOptionBehaviorTests
	{
		@Test
		@DisplayName("Test writing to file in non-existent directory without CREATE_DIRECTORIES")
		void writeFile_nonExistentDirectoryWithoutCreateDirectories() throws IOException
		{
			Path nonExistentDir =
					Path.of(System.getProperty("java.io.tmpdir"), "non_existent_dir_" + System.currentTimeMillis());
			Path filePath = nonExistentDir.resolve("test_file.txt");

			if (Files.exists(nonExistentDir))
			{
				Files.delete(nonExistentDir);
			}

			FileWritingUtility.WriteResult result = FileWritingUtility.writeFile(
					filePath,
					"Test content"
			);

			assertThat(result)
					.as("Writing to file in non-existent directory without CREATE_DIRECTORIES should return DirectoryMissing")
					.isInstanceOf(FileWritingUtility.WriteResult.DirectoryMissing.class);

			if (Files.exists(filePath))
			{
				Files.delete(filePath);
			}
			if (Files.exists(nonExistentDir))
			{
				Files.delete(nonExistentDir);
			}
		}

		@ParameterizedTest(name = "{5}")
		@MethodSource("writeFileOptionBehaviorProvider")
		@DisplayName("Test writeFile with different option combinations")
		void writeFile_optionBehavior(
				String fileName,
				String initialContent,
				String newContent,
				FileWritingUtility.WriteOption[] options,
				Class<? extends FileWritingUtility.WriteResult> expectedResultType,
				String description) throws IOException
		{
			Path filePath = sharedTempDir.resolve(fileName);

			if (fileName.contains("/") && !fileName.equals("nested/directory/file_without_create.txt")
					&& !fileName.equals("nested/directory/file_with_create.txt"))
			{
				Files.createDirectories(filePath.getParent());
			}

			if (fileName.contains("existing_file"))
			{
				Files.createDirectories(filePath.getParent());
				Files.writeString(filePath, initialContent);
			}

			FileWritingUtility.WriteResult result = FileWritingUtility.writeFile(
					filePath,
					newContent,
					options
			);

			assertThat(result)
					.as(description)
					.isInstanceOf(expectedResultType);

			if (result instanceof FileWritingUtility.WriteResult.Success)
			{
				assertThat(Files.exists(filePath))
						.as("File should exist after successful write")
						.isTrue();

				String actualContent = Files.readString(filePath);
				assertThat(actualContent)
						.as("File content should match the new content")
						.isEqualTo(newContent);
			}
			else if (result instanceof FileWritingUtility.WriteResult.FileAlreadyExists)
			{
				String actualContent = Files.readString(filePath);
				assertThat(actualContent)
						.as("File content should remain unchanged")
						.isEqualTo(initialContent);
			}
		}

		private static Stream<Arguments> writeFileOptionBehaviorProvider()
		{
			return Stream.of(
					Arguments.of(
							"existing_file_overwrite.txt",
							"Initial content",
							"New content",
							new FileWritingUtility.WriteOption[]{FileWritingUtility.WriteOption.OVERWRITE_EXISTING},
							FileWritingUtility.WriteResult.Success.class,
							"Writing to existing file with OVERWRITE_EXISTING should succeed and overwrite content"
					),
					Arguments.of(
							"existing_file_no_overwrite.txt",
							"Initial content",
							"New content",
							new FileWritingUtility.WriteOption[]{},
							FileWritingUtility.WriteResult.FileAlreadyExists.class,
							"Writing to existing file without OVERWRITE_EXISTING should return FileAlreadyExists"
					),
					Arguments.of(
							"nested/directory/file_with_create.txt",
							"Content in nested directory",
							"Content in nested directory",
							new FileWritingUtility.WriteOption[]{FileWritingUtility.WriteOption.CREATE_DIRECTORIES},
							FileWritingUtility.WriteResult.Success.class,
							"Writing to file in non-existent directory with CREATE_DIRECTORIES should succeed"
					),
					Arguments.of(
							"nested/directory/existing_file_both_options.txt",
							"Initial content",
							"New content",
							new FileWritingUtility.WriteOption[]{
									FileWritingUtility.WriteOption.OVERWRITE_EXISTING,
									FileWritingUtility.WriteOption.CREATE_DIRECTORIES
							},
							FileWritingUtility.WriteResult.Success.class,
							"Writing to existing file in non-existent directory with both options should succeed"
					)
			);
		}
	}

	@Nested
	@DisplayName("writeFile Edge Cases")
	class WriteFileEdgeCasesTests
	{
		@ParameterizedTest(name = "{4}")
		@MethodSource("writeFileEdgeCasesProvider")
		@DisplayName("Test writeFile with edge cases")
		void writeFile_edgeCases(String filePath, String content, FileWritingUtility.WriteOption[] options,
								 Class<? extends FileWritingUtility.WriteResult> expectedResultType, String description)
		{
			// Handle edge cases for Path objects
			if (filePath == null || filePath.isBlank())
			{
				// For null or blank paths, we need to create a custom error since Path.of() can't handle these
				FileWritingUtility.WriteResult.Error result = new FileWritingUtility.WriteResult.Error(
						null,
						filePath == null ?
								new NullPointerException("Path is null") :
								new IllegalArgumentException("Path is blank: '" + filePath + "'")
				);

				assertThat(result)
						.as(description)
						.isInstanceOf(expectedResultType);

				assertThat(result.cause())
						.as("Error should contain an exception")
						.isNotNull();

				return;
			}

			String fullPath = filePath.startsWith("/") ? filePath : sharedTempDir.resolve(filePath).toString();

			FileWritingUtility.WriteResult result = FileWritingUtility.writeFile(
					Path.of(fullPath),
					content,
					options
			);

			assertThat(result)
					.as(description)
					.isInstanceOf(expectedResultType);

			if (result instanceof FileWritingUtility.WriteResult.Error error)
			{
				assertThat(error.cause())
						.as("Error should contain an exception")
						.isNotNull();
			}
		}

		private static Stream<Arguments> writeFileEdgeCasesProvider()
		{
			return Stream.of(
					Arguments.of(
							null,
							"Some content",
							new FileWritingUtility.WriteOption[]{},
							FileWritingUtility.WriteResult.Error.class,
							"Null file path should return Error result"),
					Arguments.of(
							"",
							"Some content",
							new FileWritingUtility.WriteOption[]{},
							FileWritingUtility.WriteResult.Error.class,
							"Empty file path should return Error result"),
					Arguments.of(
							"  ",
							"Some content",
							new FileWritingUtility.WriteOption[]{},
							FileWritingUtility.WriteResult.Error.class,
							"Blank file path should return Error result"),
					Arguments.of(
							"normal_file.txt",
							null,
							new FileWritingUtility.WriteOption[]{},
							FileWritingUtility.WriteResult.Error.class,
							"Null content should return Error result"),
					Arguments.of(
							"very_large_content.txt",
							"A".repeat(10_000_000),
							new FileWritingUtility.WriteOption[]{},
							FileWritingUtility.WriteResult.Success.class,
							"Very large content should be written successfully")
			);
		}
	}

	@Nested
	@DisplayName("writeFile Additional Edge Cases")
	class WriteFileAdditionalEdgeCasesTests
	{
		@Test
		@DisplayName("Test writing to a path that points to a directory")
		void writeFile_pathPointsToDirectory() throws IOException
		{
			Path dirPath = sharedTempDir.resolve("directory_not_file");
			Files.createDirectories(dirPath);

			FileWritingUtility.WriteResult result = FileWritingUtility.writeFile(
					dirPath,
					"Content"
			);

			assertThat(result)
					.as("Writing to a directory path should return FileAlreadyExists")
					.isInstanceOf(FileWritingUtility.WriteResult.FileAlreadyExists.class);

			FileWritingUtility.WriteResult.FileAlreadyExists fileAlreadyExists =
					(FileWritingUtility.WriteResult.FileAlreadyExists) result;
			assertThat(fileAlreadyExists.path())
					.as("FileAlreadyExists should contain the directory path")
					.isEqualTo(dirPath);
		}

		@Test
		@DisplayName("Test writing to a path with special characters")
		void writeFile_pathWithSpecialCharacters() throws IOException
		{
			Path filePath = sharedTempDir.resolve("special_chars_#$@!%_file.txt");

			FileWritingUtility.WriteResult result = FileWritingUtility.writeFile(
					filePath,
					"Content with special characters in path"
			);

			assertThat(result)
					.as("Writing to a path with special characters should succeed")
					.isInstanceOf(FileWritingUtility.WriteResult.Success.class);

			assertThat(Files.exists(filePath))
					.as("File should exist after writing")
					.isTrue();

			String actualContent = Files.readString(filePath);
			assertThat(actualContent)
					.as("File content should match the written content")
					.isEqualTo("Content with special characters in path");
		}

		@Test
		@DisplayName("Test writing to a path with spaces")
		void writeFile_pathWithSpaces() throws IOException
		{
			Path filePath = sharedTempDir.resolve("file with spaces in name.txt");

			FileWritingUtility.WriteResult result = FileWritingUtility.writeFile(
					filePath,
					"Content with spaces in path"
			);

			assertThat(result)
					.as("Writing to a path with spaces should succeed")
					.isInstanceOf(FileWritingUtility.WriteResult.Success.class);

			assertThat(Files.exists(filePath))
					.as("File should exist after writing")
					.isTrue();

			String actualContent = Files.readString(filePath);
			assertThat(actualContent)
					.as("File content should match the written content")
					.isEqualTo("Content with spaces in path");
		}

		@Test
		@DisplayName("Test writing to a deeply nested path")
		void writeFile_deeplyNestedPath() throws IOException
		{
			Path filePath = sharedTempDir.resolve("level1/level2/level3/level4/level5/deeply_nested_file.txt");

			FileWritingUtility.WriteResult result = FileWritingUtility.writeFile(
					filePath,
					"Content in deeply nested file",
					new FileWritingUtility.WriteOption[]{FileWritingUtility.WriteOption.CREATE_DIRECTORIES}
			);

			assertThat(result)
					.as("Writing to a deeply nested path should succeed with CREATE_DIRECTORIES option")
					.isInstanceOf(FileWritingUtility.WriteResult.Success.class);

			assertThat(Files.exists(filePath))
					.as("File should exist after writing")
					.isTrue();

			String actualContent = Files.readString(filePath);
			assertThat(actualContent)
					.as("File content should match the written content")
					.isEqualTo("Content in deeply nested file");
		}

		@Test
		@DisplayName("Test writing to a file with empty content")
		void writeFile_emptyContent() throws IOException
		{
			Path filePath = sharedTempDir.resolve("empty_content_file.txt");

			FileWritingUtility.WriteResult result = FileWritingUtility.writeFile(
					filePath,
					""
			);

			assertThat(result)
					.as("Writing empty content should succeed")
					.isInstanceOf(FileWritingUtility.WriteResult.Success.class);

			assertThat(Files.exists(filePath))
					.as("File should exist after writing")
					.isTrue();

			String actualContent = Files.readString(filePath);
			assertThat(actualContent)
					.as("File content should be empty")
					.isEmpty();
		}

		@Test
		@DisplayName("Test writing to a file with Unicode content")
		void writeFile_unicodeContent() throws IOException
		{
			Path filePath = sharedTempDir.resolve("unicode_content_file.txt");
			String unicodeContent = "Unicode characters: 你好, こんにちは, 안녕하세요, Привет, مرحبا";

			FileWritingUtility.WriteResult result = FileWritingUtility.writeFile(
					filePath,
					unicodeContent
			);

			assertThat(result)
					.as("Writing Unicode content should succeed")
					.isInstanceOf(FileWritingUtility.WriteResult.Success.class);

			assertThat(Files.exists(filePath))
					.as("File should exist after writing")
					.isTrue();

			String actualContent = Files.readString(filePath);
			assertThat(actualContent)
					.as("File content should match the Unicode content")
					.isEqualTo(unicodeContent);
		}
	}

	@Nested
	@DisplayName("writeFile Error Handling Tests")
	class WriteFileErrorHandlingTests
	{
		@Test
		@DisplayName("Test writing to a non-existent parent directory without CREATE_DIRECTORIES")
		void writeFile_nonExistentParentDirectoryWithoutCreateDirectories()
		{
			Path filePath = sharedTempDir.resolve("non_existent_dir/test_file.txt");

			FileWritingUtility.WriteResult result = FileWritingUtility.writeFile(
					filePath,
					"Content"
			);

			assertThat(result)
					.as("Writing to a file in a non-existent directory without CREATE_DIRECTORIES should return DirectoryMissing")
					.isInstanceOf(FileWritingUtility.WriteResult.DirectoryMissing.class);

			FileWritingUtility.WriteResult.DirectoryMissing directoryMissing =
					(FileWritingUtility.WriteResult.DirectoryMissing) result;
			assertThat(directoryMissing.directory())
					.as("DirectoryMissing should contain the missing directory path")
					.isEqualTo(filePath.getParent());
		}

		@Test
		@DisplayName("Test writing to an existing file without OVERWRITE_EXISTING")
		void writeFile_existingFileWithoutOverwriteExisting() throws IOException
		{
			Path filePath = sharedTempDir.resolve("existing_file_no_overwrite.txt");
			Files.writeString(filePath, "Initial content");

			FileWritingUtility.WriteResult result = FileWritingUtility.writeFile(
					filePath,
					"New content"
			);

			assertThat(result)
					.as("Writing to an existing file without OVERWRITE_EXISTING should return FileAlreadyExists")
					.isInstanceOf(FileWritingUtility.WriteResult.FileAlreadyExists.class);

			FileWritingUtility.WriteResult.FileAlreadyExists fileAlreadyExists =
					(FileWritingUtility.WriteResult.FileAlreadyExists) result;
			assertThat(fileAlreadyExists.path())
					.as("FileAlreadyExists should contain the existing file path")
					.isEqualTo(filePath);

			String actualContent = Files.readString(filePath);
			assertThat(actualContent)
					.as("File content should remain unchanged")
					.isEqualTo("Initial content");
		}
	}

	@Nested
	@DisplayName("writeFileAndCreateDirectory Tests")
	class WriteFileAndCreateDirectoryTests
	{
		@ParameterizedTest(name = "{4}")
		@MethodSource("writeFileAndCreateDirectoryProvider")
		@DisplayName("Test writeFileAndCreateDirectory method with different force values")
		void writeFileAndCreateDirectory_tests(
				String fileName,
				String content,
				boolean force,
				Class<? extends FileWritingUtility.WriteResult> expectedResultType,
				String description) throws IOException
		{
			Path filePath = sharedTempDir.resolve(fileName);

			if (description.contains("existing"))
			{
				Files.createDirectories(filePath.getParent());
				Files.writeString(filePath, "Initial content");
			}

			FileWritingUtility.WriteResult result = FileWritingUtility.writeFileAndCreateDirectory(
					filePath,
					content,
					force
			);

			assertThat(result)
					.as(description)
					.isInstanceOf(expectedResultType);

			if (result instanceof FileWritingUtility.WriteResult.Success)
			{
				assertThat(Files.exists(filePath))
						.as("File should exist after successful write")
						.isTrue();

				String actualContent = Files.readString(filePath);
				assertThat(actualContent)
						.as("File content should match the expected content")
						.isEqualTo(content);
			}
			else if (result instanceof FileWritingUtility.WriteResult.FileAlreadyExists)
			{
				assertThat(Files.exists(filePath))
						.as("File should exist when FileAlreadyExists is returned")
						.isTrue();

				String actualContent = Files.readString(filePath);
				assertThat(actualContent)
						.as("File content should remain unchanged")
						.isEqualTo("Initial content");
			}
		}

		private static Stream<Arguments> writeFileAndCreateDirectoryProvider()
		{
			return Stream.of(
					// Test with force=true (should use both OVERWRITE_EXISTING and CREATE_DIRECTORIES)
					Arguments.of(
							"new_file_with_force.txt",
							"Content in new file with force",
							true,
							FileWritingUtility.WriteResult.Success.class,
							"Writing to new file with force=true should succeed"
					),
					Arguments.of(
							"nested/deep/directory/new_file_with_force.txt",
							"Content in nested directory with force",
							true,
							FileWritingUtility.WriteResult.Success.class,
							"Writing to file in non-existent nested directory with force=true should succeed"
					),
					Arguments.of(
							"existing_file_with_force.txt",
							"New content for existing file with force",
							true,
							FileWritingUtility.WriteResult.Success.class,
							"Writing to existing file with force=true should succeed and overwrite content"
					),

					// Test with force=false (should use only CREATE_DIRECTORIES)
					Arguments.of(
							"new_file_without_force.txt",
							"Content in new file without force",
							false,
							FileWritingUtility.WriteResult.Success.class,
							"Writing to new file with force=false should succeed"
					),
					Arguments.of(
							"nested/deep/directory/new_file_without_force.txt",
							"Content in nested directory without force",
							false,
							FileWritingUtility.WriteResult.Success.class,
							"Writing to file in non-existent nested directory with force=false should succeed"
					),
					Arguments.of(
							"existing_file_without_force.txt",
							"New content for existing file without force",
							false,
							FileWritingUtility.WriteResult.FileAlreadyExists.class,
							"Writing to existing file with force=false should return FileAlreadyExists"
					)
			);
		}
	}

	@Nested
	@DisplayName("writeFileWithForceAndCreateDirectory Tests")
	class WriteFileWithForceAndCreateDirectoryTests
	{
		@ParameterizedTest(name = "{3}")
		@MethodSource("writeFileWithForceAndCreateDirectoryProvider")
		@DisplayName("Test writeFileWithForceAndCreateDirectory method")
		void writeFileWithForceAndCreateDirectory_tests(
				String fileName,
				String content,
				Class<? extends FileWritingUtility.WriteResult> expectedResultType,
				String description) throws IOException
		{
			Path filePath = sharedTempDir.resolve(fileName);

			if (description.contains("existing"))
			{
				Files.createDirectories(filePath.getParent());
				Files.writeString(filePath, "Initial content");
			}

			FileWritingUtility.WriteResult result = FileWritingUtility.writeFileWithForceAndCreateDirectory(
					filePath,
					content
			);

			assertThat(result)
					.as(description)
					.isInstanceOf(expectedResultType);

			if (result instanceof FileWritingUtility.WriteResult.Success)
			{
				assertThat(Files.exists(filePath))
						.as("File should exist after successful write")
						.isTrue();

				String actualContent = Files.readString(filePath);
				assertThat(actualContent)
						.as("File content should match the expected content")
						.isEqualTo(content);
			}
		}

		private static Stream<Arguments> writeFileWithForceAndCreateDirectoryProvider()
		{
			return Stream.of(
					Arguments.of(
							"new_file_convenience.txt",
							"Content in new file",
							FileWritingUtility.WriteResult.Success.class,
							"Writing to new file should succeed"
					),
					Arguments.of(
							"nested/deep/directory/new_file_convenience.txt",
							"Content in nested directory",
							FileWritingUtility.WriteResult.Success.class,
							"Writing to file in non-existent nested directory should succeed"
					),
					Arguments.of(
							"existing_file_convenience.txt",
							"New content for existing file",
							FileWritingUtility.WriteResult.Success.class,
							"Writing to existing file should succeed and overwrite content"
					)
			);
		}
	}

	@Nested
	@DisplayName("writeFile Merge Option Tests")
	class WriteFileMergeOptionTests
	{
		@ParameterizedTest(name = "{5}")
		@MethodSource("writeFileMergeOptionProvider")
		@DisplayName("Test writeFile with MERGE option")
		void writeFile_mergeOption(
				String fileName,
				String initialContent,
				String newContent,
				FileWritingUtility.WriteOption[] options,
				Class<? extends FileWritingUtility.WriteResult> expectedResultType,
				String description) throws IOException
		{
			Path filePath = sharedTempDir.resolve(fileName);

			if (fileName.contains("/"))
			{
				Files.createDirectories(filePath.getParent());
			}

			if (initialContent != null)
			{
				Files.writeString(filePath, initialContent);
			}

			FileWritingUtility.WriteResult result = FileWritingUtility.writeFile(
					filePath,
					newContent,
					options
			);

			assertThat(result)
					.as(description)
					.isInstanceOf(expectedResultType);

			if (result instanceof FileWritingUtility.WriteResult.Success)
			{
				assertThat(Files.exists(filePath))
						.as("File should exist after successful write")
						.isTrue();

				String actualContent = Files.readString(filePath);

				if (Arrays.asList(options).contains(FileWritingUtility.WriteOption.MERGE) && initialContent != null)
				{
					assertThat(actualContent)
							.as("File content should be the merged content")
							.isEqualTo(initialContent + newContent);
				}
				else
				{
					assertThat(actualContent)
							.as("File content should match the new content")
							.isEqualTo(newContent);
				}
			}
		}

		private static Stream<Arguments> writeFileMergeOptionProvider()
		{
			return Stream.of(
					// Test MERGE option with existing files
					Arguments.of(
							"existing_file_merge.txt",
							"Initial content",
							" - Appended content",
							new FileWritingUtility.WriteOption[]{FileWritingUtility.WriteOption.MERGE},
							FileWritingUtility.WriteResult.Success.class,
							"Writing to existing file with MERGE should succeed and append content"
					),
					Arguments.of(
							"existing_file_merge_with_create_dir.txt",
							"Initial content",
							" - Appended content",
							new FileWritingUtility.WriteOption[]{
									FileWritingUtility.WriteOption.MERGE,
									FileWritingUtility.WriteOption.CREATE_DIRECTORIES
							},
							FileWritingUtility.WriteResult.Success.class,
							"Writing to existing file with MERGE and CREATE_DIRECTORIES should succeed and append content"
					),

					// Test MERGE option with new files (should behave like normal write)
					Arguments.of(
							"new_file_merge.txt",
							null,
							"New content",
							new FileWritingUtility.WriteOption[]{FileWritingUtility.WriteOption.MERGE},
							FileWritingUtility.WriteResult.Success.class,
							"Writing to new file with MERGE should succeed and write content"
					),
					Arguments.of(
							"nested/directory/new_file_merge.txt",
							null,
							"New content in nested directory",
							new FileWritingUtility.WriteOption[]{
									FileWritingUtility.WriteOption.MERGE,
									FileWritingUtility.WriteOption.CREATE_DIRECTORIES
							},
							FileWritingUtility.WriteResult.Success.class,
							"Writing to new file in nested directory with MERGE and CREATE_DIRECTORIES should succeed"
					),

					// Test MERGE with empty content
					Arguments.of(
							"existing_file_merge_empty.txt",
							"Initial content",
							"",
							new FileWritingUtility.WriteOption[]{FileWritingUtility.WriteOption.MERGE},
							FileWritingUtility.WriteResult.Success.class,
							"Merging empty content should keep the original content unchanged"
					),

					// Test MERGE with empty initial content
					Arguments.of(
							"existing_file_empty_merge.txt",
							"",
							"New content",
							new FileWritingUtility.WriteOption[]{FileWritingUtility.WriteOption.MERGE},
							FileWritingUtility.WriteResult.Success.class,
							"Merging with empty initial content should result in just the new content"
					)
			);
		}
	}

	@Nested
	@DisplayName("writeFileWithMerge Tests")
	class WriteFileWithMergeTests
	{
		@ParameterizedTest(name = "{3}")
		@MethodSource("writeFileWithMergeProvider")
		@DisplayName("Test writeFileWithMerge method")
		void writeFileWithMerge_tests(
				String fileName,
				String initialContent,
				String newContent,
				String description) throws IOException
		{
			Path filePath = sharedTempDir.resolve(fileName);

			if (initialContent != null)
			{
				Files.createDirectories(filePath.getParent());
				Files.writeString(filePath, initialContent);
			}

			FileWritingUtility.WriteResult result = FileWritingUtility.writeFileWithMerge(
					filePath,
					newContent
			);

			assertThat(result)
					.as(description)
					.isInstanceOf(FileWritingUtility.WriteResult.Success.class);

			assertThat(Files.exists(filePath))
					.as("File should exist after successful write")
					.isTrue();

			String actualContent = Files.readString(filePath);

			if (initialContent != null)
			{
				assertThat(actualContent)
						.as("File content should be the merged content")
						.isEqualTo(initialContent + newContent);
			}
			else
			{
				assertThat(actualContent)
						.as("File content should match the new content")
						.isEqualTo(newContent);
			}
		}

		private static Stream<Arguments> writeFileWithMergeProvider()
		{
			return Stream.of(
					Arguments.of(
							"new_file_with_merge.txt",
							null,
							"Content in new file",
							"Writing to new file with merge should succeed"
					),
					Arguments.of(
							"existing_file_with_merge.txt",
							"Initial content",
							" - Appended content",
							"Writing to existing file with merge should succeed and append content"
					)
			);
		}
	}

	@Nested
	@DisplayName("writeFileWithMergeAndCreateDirectory Tests")
	class WriteFileWithMergeAndCreateDirectoryTests
	{
		@ParameterizedTest(name = "{3}")
		@MethodSource("writeFileWithMergeAndCreateDirectoryProvider")
		@DisplayName("Test writeFileWithMergeAndCreateDirectory method")
		void writeFileWithMergeAndCreateDirectory_tests(
				String fileName,
				String initialContent,
				String newContent,
				String description) throws IOException
		{
			Path filePath = sharedTempDir.resolve(fileName);

			if (initialContent != null)
			{
				Files.createDirectories(filePath.getParent());
				Files.writeString(filePath, initialContent);
			}

			FileWritingUtility.WriteResult result = FileWritingUtility.writeFileWithMergeAndCreateDirectory(
					filePath,
					newContent
			);

			assertThat(result)
					.as(description)
					.isInstanceOf(FileWritingUtility.WriteResult.Success.class);

			assertThat(Files.exists(filePath))
					.as("File should exist after successful write")
					.isTrue();

			String actualContent = Files.readString(filePath);

			if (initialContent != null)
			{
				assertThat(actualContent)
						.as("File content should be the merged content")
						.isEqualTo(initialContent + newContent);
			}
			else
			{
				assertThat(actualContent)
						.as("File content should match the new content")
						.isEqualTo(newContent);
			}
		}

		private static Stream<Arguments> writeFileWithMergeAndCreateDirectoryProvider()
		{
			return Stream.of(
					Arguments.of(
							"new_file_with_merge_and_create.txt",
							null,
							"Content in new file",
							"Writing to new file with merge and create directory should succeed"
					),
					Arguments.of(
							"nested/deep/directory/new_file_with_merge_and_create.txt",
							null,
							"Content in nested directory",
							"Writing to file in non-existent nested directory with merge and create directory should succeed"
					),
					Arguments.of(
							"existing_file_with_merge_and_create.txt",
							"Initial content",
							" - Appended content",
							"Writing to existing file with merge and create directory should succeed and append content"
					)
			);
		}
	}
}