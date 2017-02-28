package org.vaadin.addons.converters;

import java.util.function.BiFunction;

/**
 * Converter function for converting suggestion data into caption for a
 * suggestion item. The caption will be visible for the user. Contains safe
 * HTML.
 *
 * @param <T>
 *         Type of the suggestion data, e.g. a Java Bean.
 * @see org.vaadin.addons.generators.SuggestionGenerator
 * @see SuggestionValueConverter
 */
public interface SuggestionCaptionConverter<T> extends
        BiFunction<T, String, String> {

    /**
     * Converter method. Parameters are the suggestion data object and the user
     * query. The method should return the text that will be displayed for the
     * user as suggestion.
     * <p>
     * <b>Note</b> that {@code query} string originates from the user and may
     * contain malicious content. Please make sure that your database query is
     * safe.
     * <p>
     * Return caption as safe HTML. Ensure that untrusted text is escaped so
     * that malicious script couldn't be executed.
     *
     * @param suggestion
     *         Suggestion data object of type {@code T}.
     * @param query
     *         Query that was issued by the user.
     * @return Text or safe HTML to be displayed for the user as suggestion.
     */
    @Override
    String apply(T suggestion, String query);
}
