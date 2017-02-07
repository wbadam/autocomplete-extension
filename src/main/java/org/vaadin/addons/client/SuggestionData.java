package org.vaadin.addons.client;

import java.io.Serializable;

import com.google.gwt.safehtml.shared.annotations.IsSafeHtml;

/**
 * Stores data for a suggestion item for server-client communication.
 */
public class SuggestionData implements Serializable {

    /**
     * Value of a suggestion item. This will be set as value for the text field
     * when the suggestion is selected.
     */
    private String value;

    /**
     * Caption of a suggestion item. This is visible for the user when
     * suggestion list is shown. Contains safe HTML.
     */
    private @IsSafeHtml String caption;

    public SuggestionData() {
    }

    /**
     * Creates a suggestion data for server-client communication.
     *
     * @param value
     *         Value of the suggestion item to be set for the text field when
     *         the suggestion is selected.
     * @param caption
     *         Caption of the suggestion item that is visible for the user when
     *         suggestion list is shown. Can contain HTML.
     */
    public SuggestionData(String value, String caption) {
        this.value = value;
        this.caption = caption;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }
}
