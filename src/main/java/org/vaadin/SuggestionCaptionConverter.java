package org.vaadin;

import java.util.function.BiFunction;

import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * Converter function for converting suggestion data into caption for a
 * suggestion item. The caption will be visible for the user. Contains safe
 * HTML.
 *
 * @param <T>
 *         Type of the suggestion data, e.g. a Java Bean.
 */
public interface SuggestionCaptionConverter<T> extends
        BiFunction<T, String, SafeHtml> {

    /**
     * Converter method. Parameters are the suggestion data object and the user
     * query. The method should return the text that should be displayed for the
     * user as suggestion.
     * <p>
     * Example 1 returns the query in bold text:
     * <pre> {@code
     * SafeHtmlBuilder builder = new SafeHtmlBuilder();
     * builder.appendHtmlConstant("<b>");
     * builder.appendEscaped(query);
     * builder.appendHtmlConstant("<b/>");
     * return builder.toSafeHtml();
     * }</pre>
     * <p>
     * Example 2 returns the query as is:
     * <pre> {@code
     * return SafeHtmlUtils.fromString(query);
     * }
     * </pre>
     *
     * @param suggestion
     *         Suggestion data object of type {@code T}.
     * @param query
     *         Query that was issued by the user.
     * @return Text to be displayed for the user as suggestion as safe HTML.
     */
    @Override
    SafeHtml apply(T suggestion, String query);
}
