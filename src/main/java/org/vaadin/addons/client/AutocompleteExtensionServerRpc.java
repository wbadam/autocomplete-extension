package org.vaadin.addons.client;

import com.vaadin.shared.communication.ServerRpc;

public interface AutocompleteExtensionServerRpc extends ServerRpc {

    public void getSuggestion(String query);

    public void suggestionSelected(String value);
}
