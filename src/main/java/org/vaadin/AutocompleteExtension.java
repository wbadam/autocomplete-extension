package org.vaadin;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.vaadin.client.AutocompleteExtensionClientRpc;
import org.vaadin.client.AutocompleteExtensionServerRpc;
import org.vaadin.client.AutocompleteExtensionState;

import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.TextField;

public class AutocompleteExtension<T> extends AbstractExtension {

    private SuggestionGenerator<T> suggestionGenerator;
    private CaptionGenerator<T> captionGenerator;

    private AutocompleteExtensionServerRpc rpc = query -> {

        // TODO: 04/02/2017 Register RPC only when generator set
        Optional.ofNullable(suggestionGenerator).ifPresent(generator -> {
            List<T> suggestions = generator
                    .apply(query, getState().suggestionListSize);

            // Send results back
            getRpcProxy(AutocompleteExtensionClientRpc.class).showSuggestions(
                    suggestions.stream()
                            .map(s -> captionGenerator.apply(s, query))
                            .collect(Collectors.toList()));
        });
    };

    public AutocompleteExtension() {
        registerRpc(rpc);
    }

    public void extend(TextField textField) {
        super.extend(textField);
    }

    public void setSuggestionGenerator(
            SuggestionGenerator<T> suggestionGenerator) {
        setSuggestionGenerator(suggestionGenerator, null);
    }

    public void setSuggestionGenerator(
            SuggestionGenerator<T> suggestionGenerator,
            CaptionGenerator<T> captionGenerator) {
        this.suggestionGenerator = suggestionGenerator;
        this.captionGenerator = captionGenerator;
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
