package org.vaadin;

import java.util.List;
import java.util.function.BiFunction;

/**
 *
 * @param <T>
 */
public interface SuggestionGenerator<T> extends BiFunction<String, Integer, List<T>> {

    /**
     *
     * @param query
     * @param numberOfSuggestions
     * @return
     */
    @Override
    public List<T> apply(String query, Integer numberOfSuggestions);
}
