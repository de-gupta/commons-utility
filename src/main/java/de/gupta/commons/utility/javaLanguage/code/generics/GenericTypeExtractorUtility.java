package de.gupta.commons.utility.javaLanguage.code.generics;

import de.gupta.aletheia.core.Unfolding;
import de.gupta.commons.utility.javaLanguage.code.type.TypeDeclaration;

import java.util.ArrayList;
import java.util.List;

public final class GenericTypeExtractorUtility
{
	public static List<String> extractGenericTypes(final TypeDeclaration typeDeclaration)
	{
		return Unfolding.of(typeDeclaration)
						.refold(TypeDeclaration::name)
						.refold(GenericTypeExtractorUtility::parseGenericParameters)
						.reveal();
	}

	private static List<String> parseGenericParameters(String content)
	{
		List<String> genericTypeParams = new ArrayList<>();
		int bracketCount = 1;
		int currentPos = content.indexOf('<');
		StringBuilder paramBuilder = new StringBuilder();

		while (currentPos < content.length() && bracketCount > 0)
		{
			char c = content.charAt(currentPos++);

			if (c == '<')
			{
				bracketCount++;
				paramBuilder.append(c);
			}
			else if (c == '>')
			{
				bracketCount--;
				if (bracketCount > 0)
				{
					paramBuilder.append(c);
				}
			}
			else if (c == ',' && bracketCount == 1)
			{
				genericTypeParams.add(extractTypeParameterName(paramBuilder.toString().trim()));
				paramBuilder = new StringBuilder();
			}
			else
			{
				paramBuilder.append(c);
			}
		}

		if (!paramBuilder.isEmpty())
		{
			genericTypeParams.add(extractTypeParameterName(paramBuilder.toString().trim()));
		}

//		List<String> cleanedParams = new ArrayList<>(genericTypeParams.size());
//		genericTypeParams.forEach(t -> cleanedParams.add(cleanSymbol(t)));

		return genericTypeParams;
	}

	private static String extractTypeParameterName(String fullTypeParam)
	{
		// Extract just the type parameter name (before any extends/super clause)
		int extendsIndex = fullTypeParam.indexOf(" extends ");
		int superIndex = fullTypeParam.indexOf(" super ");

		if (extendsIndex > 0)
		{
			return fullTypeParam.substring(0, extendsIndex).trim();
		}
		else if (superIndex > 0)
		{
			return fullTypeParam.substring(0, superIndex).trim();
		}
		else
		{
			return fullTypeParam.trim();
		}
	}

	private static String cleanSymbol(String input)
	{
		return input.replaceAll("[<>]", "");
	}

	private GenericTypeExtractorUtility()
	{
	}
}