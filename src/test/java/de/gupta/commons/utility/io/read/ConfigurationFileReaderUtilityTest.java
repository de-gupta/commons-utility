package de.gupta.commons.utility.io.read;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayName("Configuration File Reader Utility")
class ConfigurationFileReaderUtilityTest
{
	@Nested
	@DisplayName("Reading configuration files")
	class ReadConfigurationFileTests
	{

		@Test
		@DisplayName("should read all properties from a valid configuration file")
		void shouldReadPropertiesFromConfigFile(@TempDir Path tempDir) throws IOException
		{
			File configFile = tempDir.resolve("test.properties").toFile();
			try (FileWriter writer = new FileWriter(configFile))
			{
				writer.write("force=true\n");
				writer.write("dryRun=false\n");
				writer.write("verbose=true\n");
				writer.write("regex=.*Model.java\n");
				writer.write("templates=template1,template2\n");
				writer.write("outputDirectory=/output/dir\n");
				writer.write("domainTypes=String,Integer\n");
				writer.write("persistenceTypes=Entity,Repository\n");
				writer.write("apiTypes=DTO,Controller\n");
				writer.write("springProfile=dev\n");
			}

			// Act
			Map<String, String> properties =
					ConfigurationFileReaderUtility.readConfigurationFile(configFile.getAbsolutePath());

			// Assert - using soft assertions to report all failures at once
			assertSoftly(softly ->
			{
				softly.assertThat(properties)
					  .as("The map should contain all properties from the file")
					  .hasSize(10);
				softly.assertThat(properties.get("force"))
					  .as("Boolean property 'force' should be read correctly")
					  .isEqualTo("true");
				softly.assertThat(properties.get("dryRun"))
					  .as("Boolean property 'dryRun' should be read correctly")
					  .isEqualTo("false");
				softly.assertThat(properties.get("verbose"))
					  .as("Boolean property 'verbose' should be read correctly")
					  .isEqualTo("true");
				softly.assertThat(properties.get("regex"))
					  .as("Regex pattern should be read correctly")
					  .isEqualTo(".*Model.java");
				softly.assertThat(properties.get("templates"))
					  .as("Comma-separated list should be read correctly")
					  .isEqualTo("template1,template2");
				softly.assertThat(properties.get("outputDirectory"))
					  .as("Path property should be read correctly")
					  .isEqualTo("/output/dir");
				softly.assertThat(properties.get("domainTypes"))
					  .as("Domain types should be read correctly")
					  .isEqualTo("String,Integer");
				softly.assertThat(properties.get("persistenceTypes"))
					  .as("Persistence types should be read correctly")
					  .isEqualTo("Entity,Repository");
				softly.assertThat(properties.get("apiTypes"))
					  .as("API types should be read correctly")
					  .isEqualTo("DTO,Controller");
				softly.assertThat(properties.get("springProfile"))
					  .as("Spring profile should be read correctly")
					  .isEqualTo("dev");
			});
		}

		@Test
		@DisplayName("should throw IOException when file does not exist")
		void shouldThrowIOExceptionWhenFileDoesNotExist()
		{
			// Arrange
			String nonExistentFilePath = "non-existent-file.properties";

			// Act & Assert
			assertThatThrownBy(() -> ConfigurationFileReaderUtility.readConfigurationFile(nonExistentFilePath))
					.as("Reading a non-existent file should throw IOException")
					.isInstanceOf(IOException.class);
		}

		@Test
		@DisplayName("should return empty map for empty file")
		void shouldReturnEmptyMapForEmptyFile(@TempDir Path tempDir) throws IOException
		{
			// Arrange
			File emptyFile = tempDir.resolve("empty.properties").toFile();
			emptyFile.createNewFile();

			// Act
			Map<String, String> properties =
					ConfigurationFileReaderUtility.readConfigurationFile(emptyFile.getAbsolutePath());

			// Assert
			assertThat(properties)
					.as("An empty properties file should result in an empty map")
					.isEmpty();
		}

		@Test
		@DisplayName("should handle properties file with comments")
		void shouldHandlePropertiesFileWithComments(@TempDir Path tempDir) throws IOException
		{
			// Arrange
			File configFile = tempDir.resolve("commented.properties").toFile();
			try (FileWriter writer = new FileWriter(configFile))
			{
				writer.write("# This is a comment\n");
				writer.write("property1=value1\n");
				writer.write("# Another comment\n");
				writer.write("property2=value2\n");
				writer.write("! Old style comment\n");
				writer.write("property3=value3\n");
			}

			// Act
			Map<String, String> properties =
					ConfigurationFileReaderUtility.readConfigurationFile(configFile.getAbsolutePath());

			// Assert
			assertSoftly(softly ->
			{
				softly.assertThat(properties)
					  .as("The map should contain only the actual properties, not comments")
					  .hasSize(3);
				softly.assertThat(properties.get("property1"))
					  .as("First property should be read correctly")
					  .isEqualTo("value1");
				softly.assertThat(properties.get("property2"))
					  .as("Second property should be read correctly")
					  .isEqualTo("value2");
				softly.assertThat(properties.get("property3"))
					  .as("Third property should be read correctly")
					  .isEqualTo("value3");
			});
		}

		@Test
		@DisplayName("should handle properties with special characters")
		void shouldHandlePropertiesWithSpecialCharacters(@TempDir Path tempDir) throws IOException
		{
			// Arrange
			File configFile = tempDir.resolve("special.properties").toFile();
			try (FileWriter writer = new FileWriter(configFile))
			{
				writer.write("property\\ with\\ spaces=value with spaces\n");
				writer.write("unicode\\u003D=equals sign\n");
				writer.write("escaped\\:colon=value\n");
				writer.write("path=C\\:\\\\Program Files\\\\App\n");
			}

			// Act
			Map<String, String> properties =
					ConfigurationFileReaderUtility.readConfigurationFile(configFile.getAbsolutePath());

			// Assert
			assertSoftly(softly ->
			{
				softly.assertThat(properties)
					  .as("The map should contain all properties with special characters")
					  .hasSize(4);
				softly.assertThat(properties.get("property with spaces"))
					  .as("Property with escaped spaces in key should be read correctly")
					  .isEqualTo("value with spaces");
				softly.assertThat(properties.get("unicode="))
					  .as("Property with Unicode escape should be read correctly")
					  .isEqualTo("equals sign");
				softly.assertThat(properties.get("escaped:colon"))
					  .as("Property with escaped colon should be read correctly")
					  .isEqualTo("value");
				softly.assertThat(properties.get("path"))
					  .as("Property with Windows path should be read correctly")
					  .isEqualTo("C:\\Program Files\\App");
			});
		}

		@Test
		@DisplayName("should handle malformed properties file")
		void shouldHandleMalformedPropertiesFile(@TempDir Path tempDir) throws IOException
		{
			// Arrange
			File malformedFile = tempDir.resolve("malformed.properties").toFile();
			try (FileWriter writer = new FileWriter(malformedFile))
			{
				writer.write("valid.property=value\n");
				writer.write("malformed:property=value\n");  // Missing escape for colon
				writer.write("another.valid.property=value\n");
				writer.write("property without equals sign\n");  // Missing equals sign
				writer.write("=value without key\n");  // Missing key
			}

			// Act
			Map<String, String> properties =
					ConfigurationFileReaderUtility.readConfigurationFile(malformedFile.getAbsolutePath());

			// Assert
			assertSoftly(softly ->
			{
				softly.assertThat(properties)
					  .as("The map should contain only valid properties")
					  .hasSize(4);
				softly.assertThat(properties.get("valid.property"))
					  .as("Valid property should be read correctly")
					  .isEqualTo("value");
				softly.assertThat(properties.get("malformed"))
					  .as("Malformed property with unescaped colon should be read as is")
					  .isEqualTo("property=value");
				softly.assertThat(properties.get("another.valid.property"))
					  .as("Another valid property should be read correctly")
					  .isEqualTo("value");
			});
		}
	}

	@Nested
	@DisplayName("Checking if configuration files are readable")
	class IsConfigurationFileReadableTests
	{

		@ParameterizedTest(name = "{0} should be identified as not readable")
		@DisplayName("should return false for invalid file paths")
		@NullAndEmptySource
		@ValueSource(strings = {"non-existent-file.properties", "  ", "\t"})
		void shouldReturnFalseForInvalidFilePaths(String invalidPath)
		{
			assertThat(ConfigurationFileReaderUtility.isConfigurationFileReadable(invalidPath))
					.as("Invalid path '%s' should not be identified as readable", invalidPath)
					.isFalse();
		}

		@Test
		@DisplayName("should return true for existing readable file")
		void shouldReturnTrueForExistingReadableFile(@TempDir Path tempDir) throws IOException
		{
			// Arrange
			File configFile = tempDir.resolve("test.properties").toFile();
			configFile.createNewFile();

			// Act & Assert
			assertThat(ConfigurationFileReaderUtility.isConfigurationFileReadable(configFile.getAbsolutePath()))
					.as("Existing readable file should be identified as readable")
					.isTrue();
		}

		@Test
		@DisplayName("should handle existing files in different locations")
		void shouldHandleExistingFilesInDifferentLocations(@TempDir Path tempDir) throws IOException
		{
			// Arrange - file in root directory
			File rootFile = tempDir.resolve("config.properties").toFile();
			rootFile.createNewFile();

			// Arrange - file in subdirectory
			Path subDir = tempDir.resolve("subdir");
			Files.createDirectories(subDir);
			File subDirFile = subDir.resolve("config.properties").toFile();
			subDirFile.createNewFile();

			// Act & Assert
			assertThat(ConfigurationFileReaderUtility.isConfigurationFileReadable(rootFile.getAbsolutePath()))
					.as("Existing file in root directory should be readable")
					.isTrue();

			assertThat(ConfigurationFileReaderUtility.isConfigurationFileReadable(subDirFile.getAbsolutePath()))
					.as("Existing file in subdirectory should be readable")
					.isTrue();
		}

		@Test
		@DisplayName("should handle non-existing files")
		void shouldHandleNonExistingFiles(@TempDir Path tempDir) throws IOException
		{
			// Arrange - existing directory with non-existing file
			Path existingDir = tempDir.resolve("existing-dir");
			Files.createDirectories(existingDir);
			String nonExistingFilePath = existingDir.resolve("non-existing.properties").toString();

			// Arrange - non-existing directory with non-existing file
			String nonExistingDirPath = tempDir.resolve("non-existing-dir/config.properties").toString();

			// Act & Assert
			assertThat(ConfigurationFileReaderUtility.isConfigurationFileReadable(nonExistingFilePath))
					.as("Non-existing file in existing directory should not be readable")
					.isFalse();

			assertThat(ConfigurationFileReaderUtility.isConfigurationFileReadable(nonExistingDirPath))
					.as("File in non-existing directory should not be readable")
					.isFalse();
		}
	}
}