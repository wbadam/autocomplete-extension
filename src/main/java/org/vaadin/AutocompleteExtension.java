package org.vaadin;

import java.util.List;
import java.util.Objects;

import org.vaadin.client.AutocompleteExtensionClientRpc;
import org.vaadin.client.AutocompleteExtensionServerRpc;
import org.vaadin.client.AutocompleteExtensionState;

import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.TextField;

public class AutocompleteExtension extends AbstractExtension {

    private SuggestionGenerator suggestionGenerator;

    private AutocompleteExtensionServerRpc rpc = query -> {

        List<String> suggestionList = suggestionGenerator
                .generateSuggestions(query, getState().suggestionListSize);

        // Send result back
        getRpcProxy(AutocompleteExtensionClientRpc.class)
                .showSuggestions(suggestionList);
    };

    public AutocompleteExtension() {
        registerRpc(rpc);
    }

    public void extend(TextField textField) {
        super.extend(textField);
    }

    public void setSuggestionGenerator(SuggestionGenerator generator) {
        this.suggestionGenerator = generator;
    }

    /**
     * Delay server callback for suggestions {@code delayMillis} milliseconds.
     *
     * @param delayMillis
     *         Delay in milliseconds. Must not be negative.
     */
    public void setSuggestionDelay(int delayMillis) {
        if (delayMillis < 0) {
            throw new IllegalArgumentException("Delay must be positive.");
        }

        if (!Objects.equals(getState(false).suggestionDelay, delayMillis)) {
            getState().suggestionDelay = delayMillis;
        }
    }

    public int getSuggestionDelay() {
        return getState(false).suggestionDelay;
    }

    @Override
    protected AutocompleteExtensionState getState() {
        return (AutocompleteExtensionState) super.getState();
    }

    @Override
    protected AutocompleteExtensionState getState(boolean markAsDirty) {
        return (AutocompleteExtensionState) super.getState(markAsDirty);
    }
}
