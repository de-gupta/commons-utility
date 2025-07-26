package de.gupta.commons.utility.javaLanguage.classes;

import de.gupta.commons.utility.io.path.PathUtility;
import de.gupta.commons.utility.io.write.FileWritingUtility;
import de.gupta.commons.utility.javaLanguage.packages.PackageExtractor;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public final class ClassWritingUtility
{
	public static void writeClass(final String fileName, final String classContent, final Path contentRootPath,
								  boolean overwrite)
	{
		Optional.ofNullable(contentRootPath)
				.ifPresentOrElse(p -> writeClass(fileName, classContent, p.toString(), overwrite),
						() ->
						{
							throw new IllegalArgumentException("Content root path cannot be null");
						}
				);
	}

	public static void writeClass(final String fileName, final String classContent, final String contentRootPath,
								  boolean overwrite)
	{
		var path = PathUtility.fullPath(List.of(contentRootPath, PackageExtractor.extractPackageName(classContent)),
				fileName);
		FileWritingUtility.writeFileAndCreateDirectory(path, classContent, overwrite);
	}

	private ClassWritingUtility()
	{
	}
}