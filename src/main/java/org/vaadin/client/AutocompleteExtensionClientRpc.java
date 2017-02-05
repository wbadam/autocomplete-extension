package org.vaadin.client;

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
}