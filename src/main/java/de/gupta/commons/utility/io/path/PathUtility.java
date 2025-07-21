package de.gupta.commons.utility.io.path;

import de.gupta.commons.utility.string.StringSanitizationUtility;

import java.nio.file.Path;
import java.util.Optional;
import java.util.SequencedCollection;

public final class PathUtility
{
	public static Path fullPath(final String directory, final String fileName)
	{
		return convertToPath(directory).resolve(fileName);
	}

	public static Path convertToPath(final String path)
	{
		return Optional.ofNullable(path)
					   .filter(StringSanitizationUtility::isStringNonBlank)
					   .map(Path::of)
					   .orElseThrow(() -> new IllegalArgumentException("Path cannot be null or blank"));
	}

	public static Path fullPath(final SequencedCollection<String> partialDirectories, final String fileName)
	{
		return partialDirectories.stream()
								 .map(PathUtility::convertToPath)
								 .reduce(Path::resolve)
								 .orElseThrow(() -> new IllegalArgumentException("Path cannot be null or blank"))
								 .resolve(fileName);
	}

	private PathUtility()
	{
	}
}