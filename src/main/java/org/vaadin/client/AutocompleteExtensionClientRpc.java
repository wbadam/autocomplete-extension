package org.vaadin.client;

import java.util.List;

import com.vaadin.shared.communication.ClientRpc;

public interface AutocompleteExtensionClientRpc extends ClientRpc {

    public void showSuggestions(List<String> suggestions);
}