package de.gupta.commons.utility.javaLanguage.classes;

import de.gupta.aletheia.functional.Unfolding;
import de.gupta.commons.utility.io.write.FileWritingUtility;
import de.gupta.commons.utility.javaLanguage.packages.PackageExtractor;

import java.nio.file.Path;
import java.nio.file.Paths;
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
		Unfolding.of(classContent)
				 .metamorphose(PackageExtractor::extractPackageName)
				 .metamorphose(p -> p.split("\\."))
				 .metamorphose(segments -> Paths.get(contentRootPath, segments))
				 .metamorphose(packagePath -> packagePath.resolve(fileName))
				 .unlace(path -> FileWritingUtility.writeFileAndCreateDirectory(path, classContent, overwrite));
	}

	private ClassWritingUtility()
	{
	}
}