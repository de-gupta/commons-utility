package de.gupta.commons.utility.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public final class FileUtility
{
	private FileUtility()
	{
	}

	public static String readFileContent(final String directory, final String fileName)
	{
		return Optional.ofNullable(directory)
				.map(String::trim)
				.filter(d -> !d.isEmpty())
				.filter(_ -> fileName != null && !fileName.isEmpty())
				.map(d -> Paths.get(d, fileName))
				.filter(Files::exists)
				.map(p ->
				{
					try
					{
						return Files.readString(p);
					}
					catch (IOException e)
					{
						throw new RuntimeException(e);
					}
				})
				.orElseThrow(() -> new IllegalArgumentException("File not found: " + directory + "/" + fileName));
	}

	public static boolean pathExists(final Path path)
	{
		return path != null && Files.exists(path);
	}
}