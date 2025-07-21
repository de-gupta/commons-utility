package de.gupta.commons.utility.javaLanguage.packages;

import java.io.File;
import java.util.Optional;

public final class PackagePathManager
{
	private static final String subPackageSeparator = ".";
	private static final String pathSeparator = File.separator;

	public static String packagePath(final String packageName)
	{
		return Optional.ofNullable(packageName)
					   .filter(PackageNameValidator::isValidJavaPackageName)
					   .map(p -> p.replace(subPackageSeparator, pathSeparator))
					   .orElseThrow(() -> new IllegalArgumentException("Invalid package name"));
	}

	private PackagePathManager()
	{
	}
}