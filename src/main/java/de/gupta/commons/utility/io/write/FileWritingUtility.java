package de.gupta.commons.utility.io.write;

import de.gupta.commons.utility.io.read.FileReaderUtility;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

public final class FileWritingUtility
{
	public static WriteResult writeFileWithForceAndCreateDirectory(final Path filePath, final String content)
	{
		return writeFile(filePath, content, WriteOption.OVERWRITE_EXISTING, WriteOption.CREATE_DIRECTORIES);
	}

	public static WriteResult writeFile(Path filePath, String content, WriteOption... options)
	{
		try
		{
			if (filePath == null)
			{
				return new WriteResult.Error(null, new IllegalArgumentException("Path cannot be null"));
			}

			// Check for null content
			if (content == null)
			{
				return new WriteResult.Error(filePath, new IllegalArgumentException("Content cannot be null"));
			}

			boolean overwriteExisting = Arrays.asList(options).contains(WriteOption.OVERWRITE_EXISTING);
			boolean createDirectories = Arrays.asList(options).contains(WriteOption.CREATE_DIRECTORIES);
			boolean merge = Arrays.asList(options).contains(WriteOption.MERGE);

			if (Files.exists(filePath) && !overwriteExisting && !merge)
			{
				return new WriteResult.FileAlreadyExists(filePath);
			}

			Path parent = filePath.getParent();
			if (parent != null && !FileReaderUtility.pathExists(parent))
			{
				if (createDirectories)
				{
					Files.createDirectories(parent);
				}
				else
				{
					return new WriteResult.DirectoryMissing(parent);
				}
			}

			if (merge && Files.exists(filePath))
			{
				String existingContent = Files.readString(filePath);
				Files.writeString(filePath, existingContent + content);
			}
			else
			{
				Files.writeString(filePath, content);
			}
			return new WriteResult.Success();

		}
		catch (Exception e)
		{
			return new WriteResult.Error(filePath, e);
		}
	}

	public static WriteResult writeFileAndCreateDirectory(final Path filePath, final String content,
														  final boolean force)
	{
		return Optional.of(force)
					   .filter(f -> f)
					   .map(_ -> writeFile(filePath, content, WriteOption.OVERWRITE_EXISTING,
							   WriteOption.CREATE_DIRECTORIES))
					   .orElseGet(
							   () -> writeFile(filePath, content, WriteOption.CREATE_DIRECTORIES)
					   );
	}

	public static WriteResult writeFileWithMergeAndCreateDirectory(final Path filePath, final String content)
	{
		return writeFile(filePath, content, WriteOption.MERGE, WriteOption.CREATE_DIRECTORIES);
	}

	public static WriteResult writeFileWithMerge(final Path filePath, final String content)
	{
		return writeFile(filePath, content, WriteOption.MERGE);
	}

	private FileWritingUtility()
	{
	}

	public sealed interface WriteResult
	{
		record Success() implements WriteResult
		{
		}

		record FileAlreadyExists(Path path) implements WriteResult
		{
		}

		record DirectoryMissing(Path directory) implements WriteResult
		{
		}

		record Error(Path path, Exception cause) implements WriteResult
		{
		}
	}

	public enum WriteOption
	{
		OVERWRITE_EXISTING,
		CREATE_DIRECTORIES,
		MERGE
	}
}