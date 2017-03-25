package org.vaadin.addons.autocomplete.client;

public class AutocompleteExtensionState extends
        com.vaadin.shared.AbstractComponentState {

    /**
     * Maximum size of suggestion list.
     */
    public int suggestionListSize = 5;

    /**
     * Delay server callback for suggestions. Value is in milliseconds.
     */
    public int suggestionDelay = 300;

}