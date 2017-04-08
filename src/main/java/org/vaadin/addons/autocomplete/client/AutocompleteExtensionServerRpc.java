package org.vaadin.addons.autocomplete.client;

import com.vaadin.shared.communication.ServerRpc;

public interface AutocompleteExtensionServerRpc extends ServerRpc {

    public void getSuggestion(String query, String previousQuery);

    public void suggestionSelected(String key, String value);
}
