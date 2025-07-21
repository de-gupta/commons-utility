package de.gupta.commons.utility.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public final class ConfigurationFileReaderUtility
{
	public static Map<String, String> readConfigurationFile(String configFilePath) throws IOException
	{
		Properties properties = new Properties();

		try (InputStream input = Files.newInputStream(Path.of(configFilePath)))
		{
			properties.load(input);
		}

		return properties.stringPropertyNames()
						 .stream()
						 .filter(key -> !key.isEmpty())
						 .collect(Collectors.toMap(key -> key, properties::getProperty));
	}

	public static boolean isConfigurationFileReadable(String configFilePath)
	{
		return Optional.ofNullable(configFilePath)
					   .map(String::trim)
					   .map(Path::of)
					   .filter(FileUtility::pathExists)
					   .filter(Files::isRegularFile)
					   .filter(Files::isReadable)
					   .isPresent();
	}
}