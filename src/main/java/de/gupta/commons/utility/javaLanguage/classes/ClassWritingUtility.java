package de.gupta.commons.utility.javaLanguage.classes;

import de.gupta.commons.utility.io.path.PathUtility;
import de.gupta.commons.utility.io.write.FileWritingUtility;
import de.gupta.commons.utility.javaLanguage.packages.PackageExtractor;

import java.util.List;

public final class ClassWritingUtility
{
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