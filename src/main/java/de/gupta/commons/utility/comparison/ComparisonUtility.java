package de.gupta.commons.utility.comparison;

import java.util.Comparator;
import java.util.Optional;

public final class ComparisonUtility
{
	public static <T> boolean doesThisValueSatisfyTheComparison(final T value, final T reference,
																final ComparisonType comparison,
																final Comparator<T> comparator
	)
	{
		return Optional.ofNullable(value)
					   .filter(_ -> reference != null)
					   .map(v -> comparator.compare(v, reference))
					   .map(r -> comparisonResult(comparison, r))
					   .orElse(false);
	}

	private static boolean comparisonResult(final ComparisonType comparison, final int comparisonResult)
	{
		return switch (comparison)
		{
			case EQUAL -> comparisonResult == 0;
			case GREATER_THAN -> comparisonResult > 0;
			case GREATER_THAN_OR_EQUAL -> comparisonResult >= 0;
			case LESS_THAN -> comparisonResult < 0;
			case LESS_THAN_OR_EQUAL -> comparisonResult <= 0;
			case NOT_EQUAL -> comparisonResult != 0;
		};
	}

	private ComparisonUtility()
	{
	}
}