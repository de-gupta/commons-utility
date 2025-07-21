package de.gupta.commons.utility.map;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public final class MapSorter
{
	public static <V> SortedMap<LocalDate, V> sortMapWithLocalDateKey(final Map<LocalDate, V> map)
	{
		return sortMapByKey(map, Comparator.naturalOrder());
	}

	public static <V> SortedMap<LocalDate, V> sortMapInReverseWithLocalDateKey(final Map<LocalDate, V> map)
	{
		return sortMapByKey(map, Comparator.reverseOrder());
	}

	public static <K, V> SortedMap<K, V> sortMapByKey(final Map<K, V> map, final Comparator<? super K> keyComparator)
	{
		SortedMap<K, V> sortedMap = new TreeMap<>(keyComparator);
		sortedMap.putAll(map);
		return sortedMap;
	}

	private MapSorter()
	{
	}
}