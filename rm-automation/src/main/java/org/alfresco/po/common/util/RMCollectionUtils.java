/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.common.util;

import static org.springframework.util.ObjectUtils.nullSafeEquals;

import java.util.List;
import java.util.Map;

/**
 * Various common helper methods for Collections. This class is probably only appropriate for use with relatively
 * small collections as it has not been optimised for dealing with large collections.
 *
 * @author Neil Mc Erlean
 * @since 3.0.a
 */
// This class should all be moved to core Alfresco whenever possible and reused from there.
public final class RMCollectionUtils
{
    private RMCollectionUtils() { /* Intentionally empty. */}

    /** Returns the head (element at index 0) of the provided List.
     *
     * @param l the list whose head is sought.
     * @param <T> the type of the List.
     * @return the head element or {@code null} for the empty list.
     * @throws NullPointerException if l is {@code null}
     */
    public static <T> T head(List<T> l)
    {
        return l.isEmpty() ? null : l.get(0);
    }

    /**
     * Returns the tail of the provided List i&#46;e&#46; the sublist which contains
     * all elements of the given list except the {@link #head(List) head}.
     *
     * @param l the list whose tail is sought.
     * @param <T> the type of the List.
     * @return the tail sublist, which will be an empty list if the provided list had only a single element.
     * @throws NullPointerException if l is {@code null}
     * @throws UnsupportedOperationException if the provided list was empty.
     */
    public static <T> List<T> tail(List<T> l)
    {
        if (l.isEmpty())
        {
            throw new UnsupportedOperationException("Cannot get tail of empty list.");
        }
        else
        {
            return l.subList(1, l.size());
        }
    }

    /**
     * This enum represents a change in an entry between 2 collections.
     */
    public enum Difference
    {
        ADDED, REMOVED, CHANGED, UNCHANGED
    }

    /**
     * Determines the change in a Map entry between two Maps.
     * Note that both maps must have the same types of key-value pair.
     *
     * @param from the first collection.
     * @param to   the second collection.
     * @param key  the key identifying the entry.
     * @param <K>  the type of the key.
     * @param <V>  the type of the value.
     * @return the {@link Difference}.
     *
     * @throws IllegalArgumentException if {@code key} is {@code null}.
     */
    public static <K, V> Difference diffKey(Map<K, V> from, Map<K, V> to, K key)
    {
        if (key == null) { throw new IllegalArgumentException("Key cannot be null."); }

        if (from.containsKey(key))
        {
            if (to.containsKey(key))
            {
                if (nullSafeEquals(from.get(key), to.get(key))) { return Difference.UNCHANGED; }
                else                                            { return Difference.CHANGED; }
            }
            else { return Difference.REMOVED; }
        }
        else
        {
            if (to.containsKey(key)) { return Difference.ADDED; }
            else                     { return Difference.UNCHANGED; }
        }
    }
}
