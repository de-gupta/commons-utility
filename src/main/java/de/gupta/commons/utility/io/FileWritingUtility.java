package de.gupta.commons.utility.io;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

public final class FileWritingUtility
{
	public static WriteResult writeFileWithForceAndCreateDirectory(final String filePath, final String content)
	{
		return writeFile(filePath, content, WriteOption.OVERWRITE_EXISTING, WriteOption.CREATE_DIRECTORIES);
	}

	public static WriteResult writeFileAndCreateDirectory(final String filePath, final String content,
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

	public static WriteResult writeFileWithMergeAndCreateDirectory(final String filePath, final String content)
	{
		return writeFile(filePath, content, WriteOption.MERGE, WriteOption.CREATE_DIRECTORIES);
	}

	public static WriteResult writeFileWithMerge(final String filePath, final String content)
	{
		return writeFile(filePath, content, WriteOption.MERGE);
	}


	public static WriteResult writeFile(String filePath, String content, WriteOption... options)
	{
		if (filePath == null || filePath.isBlank())
		{
			return new WriteResult.Error(null, new IllegalArgumentException("File path cannot be null or empty"));
		}

		final var path = Paths.get(filePath);
		try
		{
			boolean overwriteExisting = Arrays.asList(options).contains(WriteOption.OVERWRITE_EXISTING);
			boolean createDirectories = Arrays.asList(options).contains(WriteOption.CREATE_DIRECTORIES);
			boolean merge = Arrays.asList(options).contains(WriteOption.MERGE);

			if (Files.exists(path) && !overwriteExisting && !merge)
			{
				return new WriteResult.FileAlreadyExists(path);
			}

			Path parent = path.getParent();
			if (parent != null && !FileUtility.pathExists(parent))
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

			if (merge && Files.exists(path))
			{
				String existingContent = Files.readString(path);
				Files.writeString(path, existingContent + content);
			}
			else
			{
				Files.writeString(path, content);
			}
			return new WriteResult.Success();

		}
		catch (Exception e)
		{
			return new WriteResult.Error(path, e);
		}
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