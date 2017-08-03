package org.vaadin.addons.autocomplete.client;

import java.util.List;

import com.vaadin.shared.communication.ClientRpc;

public interface AutocompleteExtensionClientRpc extends ClientRpc {

    /**
     * Sends suggestions for a query text.
     *
     * @param suggestions
     *         List of suggestion to be displayed on the client.
     * @param query
     *         Text received from the client. It is sent back from the server so
     *         that the client could verify if it is still relevant.
     */
    public void showSuggestions(List<SuggestionData> suggestions, String query);

    /**
     * Requests the client to show suggestions for the text field with current
     * value. The call will result in a server round trip to request suggestion
     * items.
     */
    public void triggerShowSuggestions();

    /**
     * Requests the client to hide suggestion list.
     */
    public void triggerHideSuggestions();
}