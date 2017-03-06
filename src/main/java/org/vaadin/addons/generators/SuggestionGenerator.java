package org.vaadin.addons.generators;

import java.util.List;
import java.util.function.Consumer;

/**
 * Implementation of this interface generates suggestions for user input. The
 * result list is to be converted to caption-value pairs before sent to the
 * client.
 * <p>
 * Use this for asynchronous queries. Return the generated suggestions through
 * the callback function asynchronously.
 *
 * @param <T>
 *         Type of data to be generated.
 * @see org.vaadin.addons.converters.SuggestionCaptionConverter
 * @see org.vaadin.addons.converters.SuggestionValueConverter
 */
@FunctionalInterface
public interface SuggestionGenerator<T> {

    /**
     * Generates list of objects of type {@code T} to be used as the basis of
     * suggestions for user input. The list of suggestions should be returned
     * calling the {@code callback.accept(suggestions)} method
     *
     * @param query
     *         User input to be used for generate suggestions.
     * @param limit
     *         Number of suggestions to be generated. This is the maximum number
     *         of suggestions that will be displayed on the user interface.
     * @param callback
     *         Function for returning the generated suggestions.
     */
    void generate(String query, Integer limit, Consumer<List<T>> callback);
}
