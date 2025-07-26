package de.gupta.commons.utility.javaLanguage.code.type;

import de.gupta.commons.utility.javaLanguage.classes.ClassNameUtility;

public record TypeDeclaration(String name, boolean isPublic)
{
	static TypeDeclaration of(final String name, final boolean isPublic)
	{
		return new TypeDeclaration(name, isPublic);
	}

	boolean isTypeNameValid()
	{
		return ClassNameUtility.hasValidJavaClassNameFormat(name());
	}
}