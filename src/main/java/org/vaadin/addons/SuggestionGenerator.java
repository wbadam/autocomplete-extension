package org.vaadin.addons;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Implementation of this interface generates suggestions for user input. The
 * result list is to be converted to caption-value pairs before sent to the
 * client.
 *
 * @param <T>
 *         Type of data to be generated.
 * @see SuggestionCaptionConverter
 * @see SuggestionValueConverter
 */
public interface SuggestionGenerator<T> extends
        BiFunction<String, Integer, List<T>> {

    /**
     * Generates list of objects of type {@code T} to be used as the basis of
     * suggestions for user input.
     *
     * @param query
     *         User input to be used for generate suggestions.
     * @param numberOfSuggestions
     *         Number of suggestions to be generated. This is the maximum number
     *         of suggestions that will be displayed on the user interface.
     * @return Generated list of type {@code T} objects to be used as the basis
     * of suggestions.
     */
    @Override
    public List<T> apply(String query, Integer numberOfSuggestions);
}
