package org.vaadin.addons.generators;

import java.util.List;
import java.util.function.Consumer;

/**
 * Implementation of this interface generates suggestions for user input. The
 * result list is to be converted to caption-value pairs before sent to the
 * client.
 * <p>
 * Use this for synchronous queries. Return the generated suggestions as the
 * {@code generate()} method's return value.
 *
 * @param <T>
 *         Type of data to be generated.
 * @see org.vaadin.addons.converters.SuggestionCaptionConverter
 * @see org.vaadin.addons.converters.SuggestionValueConverter
 */
public interface SyncSuggestionGenerator<T> extends SuggestionGenerator<T> {

    /**
     * Generates list of objects of type {@code T} to be used as the basis of
     * suggestions for user input.
     *
     * @param query
     *         User input to be used for generate suggestions.
     * @param limit
     *         Number of suggestions to be generated. This is the maximum number
     *         of suggestions that will be displayed on the user interface.
     * @return Generated list of type {@code T} objects to be used as the basis
     * of suggestions.
     */
    List<T> generate(String query, Integer limit);

    @Override
    default void generate(String query, Integer limit,
            Consumer<List<T>> callback) {
        callback.accept(generate(query, limit));
    }
}
