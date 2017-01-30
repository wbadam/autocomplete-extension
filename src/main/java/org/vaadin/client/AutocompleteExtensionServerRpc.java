package org.vaadin.client;

import com.vaadin.shared.communication.ServerRpc;

public interface AutocompleteExtensionServerRpc extends ServerRpc {

    public void getSuggestion(String query);
}
