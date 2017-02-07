package org.vaadin.addons;

import java.util.function.BiFunction;

import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * Converter function for converting suggestion data into caption for a
 * suggestion item. The caption will be visible for the user. Contains safe
 * HTML.
 *
 * @param <T>
 *         Type of the suggestion data, e.g. a Java Bean.
 * @see SuggestionGenerator
 * @see SuggestionValueConverter
 */
public interface SuggestionCaptionConverter<T> extends
        BiFunction<T, String, SafeHtml> {

    /**
     * Converter method. Parameters are the suggestion data object and the user
     * query. The method should return the text that will be displayed for the
     * user as suggestion.
     * <p>
     * Return caption as {@link SafeHtml} to ensure that malicious script
     * couldn't be executed. Ensure that untrusted text is escaped.
     * <p>
     * Read more about SafeHtml on the GWT project's website. <a
     * href=http://www.gwtproject.org/doc/latest/DevGuideSecuritySafeHtml.html#Creating_SafeHtml_Values>
     * http://www.gwtproject.org/doc/latest/DevGuideSecuritySafeHtml.html#Creating_SafeHtml_Values</a>
     * <p>
     * Example 1: As a basic example, return a simple text as caption.
     * <pre>
     *     return SafeHtmlUtils.fromString(query);
     * </pre>
     * <p>
     * Example 2: Return the user query in bold.
     * <pre>
     *     SafeHtmlBuilder builder = new SafeHtmlBuilder();
     *     builder.appendHtmlConstant("&lt;b&gt;");
     *     builder.appendEscaped(query);
     *     builder.appendHtmlConstant("&lt;b/&gt;");
     *     return builder.toSafeHtml();
     * </pre>
     * Read more about SafeHtml on GWT project's website. <a
     * href=http://www.gwtproject.org/doc/latest/DevGuideSecuritySafeHtml.html#Creating_SafeHtml_Values>
     * http://www.gwtproject.org/doc/latest/DevGuideSecuritySafeHtml.html#Creating_SafeHtml_Values</a>
     *
     * @param suggestion
     *         Suggestion data object of type {@code T}.
     * @param query
     *         Query that was issued by the user.
     * @return Text to be displayed for the user as suggestion as safe HTML.
     * @see com.google.gwt.safehtml.shared.SafeHtmlUtils
     * @see com.google.gwt.safehtml.shared.SafeHtmlBuilder
     * @see com.google.gwt.safehtml.shared.SimpleHtmlSanitizer
     */
    @Override
    SafeHtml apply(T suggestion, String query);
}
