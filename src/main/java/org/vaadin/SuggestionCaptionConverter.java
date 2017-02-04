package org.vaadin;

import java.util.function.BiFunction;

/**
 * Converter function for converting suggestion data into caption for a
 * suggestion item. The caption will be visible for the user. Can contain HTML.
 *
 * @param <T>
 *         Type of the suggestion data, e.g. a Java Bean.
 */
public interface SuggestionCaptionConverter<T> extends
        BiFunction<T, String, String> {

    /**
     * Converter method. Parameters are the suggestion data object and the user
     * query. The method should return the text that should be displayed for the
     * user as suggestion.
     *
     * @param suggestion
     *         Suggestion data object of type {@code T}.
     * @param query
     *         Query that was issued by the user.
     * @return Text to be displayed for the user as suggestion. Can be HTML.
     */
    @Override
    String apply(T suggestion, String query);
}
