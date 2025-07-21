package de.gupta.commons.utility.map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("MapSorter Tests")
final class MapSorterTest
{
	private static Stream<Arguments> localDateMapProviderSorting()
	{
		LocalDate firstDate = LocalDate.of(2020, 1, 1);
		LocalDate secondDate = LocalDate.of(2020, 1, 2);
		LocalDate thirdDate = LocalDate.of(2020, 1, 3);
		LocalDate fourthDate = LocalDate.of(2020, 1, 4);
		LocalDate fifthDate = LocalDate.of(2020, 1, 5);

		return Stream.of(
				Arguments.of(
						Map.of(secondDate, 1, firstDate, 2),
						Map.of(firstDate, 2, secondDate, 1),
						"Unsorted map should be sorted by date in ascending order"
				),
				Arguments.of(
						Map.of(firstDate, 1, secondDate, 2),
						Map.of(firstDate, 1, secondDate, 2),
						"Already sorted map should remain in the same order"
				),
				Arguments.of(
						Map.of(thirdDate, 3, secondDate, 2, firstDate, 1),
						Map.of(firstDate, 1, secondDate, 2, thirdDate, 3),
						"Map with multiple entries should be sorted by date in ascending order"
				),
				Arguments.of(
						Collections.emptyMap(),
						Collections.emptyMap(),
						"Empty map should remain empty after sorting"
				),
				Arguments.of(
						createMapWithNullValues(List.of(firstDate)),
						createMapWithNullValues(List.of(firstDate)),
						"Map with single null value should be sorted correctly"
				),
				Arguments.of(
						createMapWithNullValues(List.of(thirdDate, firstDate, secondDate)),
						createMapWithNullValues(List.of(firstDate, secondDate, thirdDate)),
						"Map with multiple null values should be sorted correctly by key"
				),
				Arguments.of(
						Map.of(fifthDate, "z", fourthDate, "y", thirdDate, "x", secondDate, "b", firstDate, "a"),
						Map.of(firstDate, "a", secondDate, "b", thirdDate, "x", fourthDate, "y", fifthDate, "z"),
						"Map with string values should be sorted by date key in ascending order"
				),
				Arguments.of(
						createSingleEntryMap(firstDate, 100),
						createSingleEntryMap(firstDate, 100),
						"Single entry map should remain unchanged after sorting"
				)
		);
	}

	private static Stream<Arguments> localDateMapProviderReverseSorting()
	{
		LocalDate firstDate = LocalDate.of(2020, 1, 1);
		LocalDate secondDate = LocalDate.of(2020, 1, 2);
		LocalDate thirdDate = LocalDate.of(2020, 1, 3);
		LocalDate fourthDate = LocalDate.of(2020, 1, 4);
		LocalDate fifthDate = LocalDate.of(2020, 1, 5);

		return Stream.of(
				Arguments.of(
						Map.of(firstDate, 1, secondDate, 2),
						Map.of(secondDate, 2, firstDate, 1),
						"Map should be sorted by date in descending order"
				),
				Arguments.of(
						Map.of(thirdDate, 3, secondDate, 2, firstDate, 1),
						Map.of(thirdDate, 3, secondDate, 2, firstDate, 1),
						"Map with multiple entries should be sorted by date in descending order"
				),
				Arguments.of(
						Collections.emptyMap(),
						Collections.emptyMap(),
						"Empty map should remain empty after reverse sorting"
				),
				Arguments.of(
						createMapWithNullValues(List.of(firstDate, secondDate, thirdDate)),
						createMapWithNullValues(List.of(thirdDate, secondDate, firstDate)),
						"Map with null values should be sorted by date in descending order"
				),
				Arguments.of(
						Map.of(firstDate, "a", secondDate, "b", thirdDate, "c", fourthDate, "d", fifthDate, "e"),
						Map.of(fifthDate, "e", fourthDate, "d", thirdDate, "c", secondDate, "b", firstDate, "a"),
						"Map with string values should be sorted by date in descending order"
				),
				Arguments.of(
						createSingleEntryMap(firstDate, 100),
						createSingleEntryMap(firstDate, 100),
						"Single entry map should remain unchanged after reverse sorting"
				),
				Arguments.of(
						Map.of(secondDate, 2, firstDate, 1),
						Map.of(secondDate, 2, firstDate, 1),
						"Already reverse-sorted map should remain in the same order"
				)
		);
	}

	private static Stream<Arguments> genericMapSortingProvider()
	{
		return Stream.of(
				Arguments.of(
						Map.of("banana", 2, "apple", 1, "cherry", 3),
						Map.of("apple", 1, "banana", 2, "cherry", 3),
						Comparator.naturalOrder(),
						"String keys should be sorted alphabetically"
				),
				Arguments.of(
						Map.of("banana", 2, "apple", 1, "cherry", 3),
						Map.of("cherry", 3, "banana", 2, "apple", 1),
						Comparator.reverseOrder(),
						"String keys should be sorted in reverse alphabetical order"
				),
				Arguments.of(
						Map.of(3, "c", 1, "a", 2, "b"),
						Map.of(1, "a", 2, "b", 3, "c"),
						Comparator.naturalOrder(),
						"Integer keys should be sorted numerically"
				),
				Arguments.of(
						Map.of(3, "c", 1, "a", 2, "b"),
						Map.of(3, "c", 2, "b", 1, "a"),
						Comparator.reverseOrder(),
						"Integer keys should be sorted in reverse numerical order"
				),
				Arguments.of(
						Collections.emptyMap(),
						Collections.emptyMap(),
						Comparator.naturalOrder(),
						"Empty map should remain empty regardless of comparator"
				),
				Arguments.of(
						Collections.emptyMap(),
						Collections.emptyMap(),
						Comparator.reverseOrder(),
						"Empty map should remain empty with reverse comparator"
				),
				Arguments.of(
						createMapWithNullValues(List.of(2, 1)),
						createMapWithNullValues(List.of(1, 2)),
						Comparator.naturalOrder(),
						"Map with null values should be sorted correctly in natural order"
				),
				Arguments.of(
						createMapWithNullValues(List.of(1, 2, 3)),
						createMapWithNullValues(List.of(3, 2, 1)),
						Comparator.reverseOrder(),
						"Map with null values should be sorted correctly in reverse order"
				),
				Arguments.of(
						createSingleEntryMap("key", "value"),
						createSingleEntryMap("key", "value"),
						Comparator.naturalOrder(),
						"Single entry map should remain unchanged regardless of comparator"
				),
				Arguments.of(
						Map.of("z", 26, "a", 1, "m", 13),
						Map.of("a", 1, "m", 13, "z", 26),
						Comparator.naturalOrder(),
						"Map with mixed order should be sorted alphabetically"
				),
				Arguments.of(
						Map.of(5.5, "e", 1.1, "a", 3.3, "c"),
						Map.of(1.1, "a", 3.3, "c", 5.5, "e"),
						Comparator.naturalOrder(),
						"Map with double keys should be sorted numerically"
				)
		);
	}

	private static <K, V> Map<K, V> createMapWithNullValues(final List<K> keys)
	{
		Map<K, V> map = new HashMap<>();
		keys.forEach(key -> map.put(key, null));
		return map;
	}

	private static <K, V> Map<K, V> createSingleEntryMap(final K key, final V value)
	{
		Map<K, V> map = new HashMap<>();
		map.put(key, value);
		return map;
	}

	@ParameterizedTest(name = "{2}")
	@MethodSource("localDateMapProviderSorting")
	@DisplayName("Sort map with LocalDate key in natural order")
	<T> void sortMapWithLocalDateKey(final Map<LocalDate, T> originalMap, final Map<LocalDate, T> expectedMap,
									 String testDescription)
	{
		SortedMap<LocalDate, T> sortedMap = MapSorter.sortMapWithLocalDateKey(originalMap);
		assertThat(sortedMap).as(testDescription).isEqualTo(expectedMap);
	}

	@ParameterizedTest(name = "{2}")
	@MethodSource("localDateMapProviderReverseSorting")
	@DisplayName("Sort map with LocalDate key in reverse order")
	<T> void sortMapInReverseWithLocalDateKey(final Map<LocalDate, T> originalMap, final Map<LocalDate, T> expectedMap,
											  String testDescription)
	{
		SortedMap<LocalDate, T> sortedMap = MapSorter.sortMapInReverseWithLocalDateKey(originalMap);
		assertThat(sortedMap).as(testDescription).isEqualTo(expectedMap);
	}

	@ParameterizedTest(name = "{3}")
	@MethodSource("genericMapSortingProvider")
	@DisplayName("Sort map by key using custom comparator")
	<K, V> void sortMapByKey(final Map<K, V> originalMap, final Map<K, V> expectedMap,
							 final Comparator<? super K> comparator, String testDescription)
	{
		SortedMap<K, V> sortedMap = MapSorter.sortMapByKey(originalMap, comparator);
		assertThat(sortedMap).as(testDescription).isEqualTo(expectedMap);
	}
}