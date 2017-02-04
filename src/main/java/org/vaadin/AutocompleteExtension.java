package org.vaadin;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.vaadin.client.AutocompleteExtensionClientRpc;
import org.vaadin.client.AutocompleteExtensionServerRpc;
import org.vaadin.client.AutocompleteExtensionState;
import org.vaadin.client.SuggestionData;

import com.vaadin.server.AbstractExtension;
import com.vaadin.ui.TextField;

public class AutocompleteExtension<T> extends AbstractExtension {

    private SuggestionGenerator<T> suggestionGenerator;
    private SuggestionCaptionConverter<T> captionConverter;
    private SuggestionValueConverter<T> valueConverter;

    private final SuggestionCaptionConverter<T> defaultCaptionConverter = (s, q) -> s
            .toString();
    private final SuggestionValueConverter<T> defaultValueConverter = T::toString;

    private AutocompleteExtensionServerRpc rpc = query -> {

        // TODO: 04/02/2017 Register RPC only when generator set
        Optional.ofNullable(suggestionGenerator).ifPresent(generator -> {
            // Generate suggestion list
            List<T> suggestions = generator
                    .apply(query, getState().suggestionListSize);

            // Get converters
            SuggestionCaptionConverter<T> cConverter = Optional
                    .ofNullable(captionConverter)
                    .orElse(defaultCaptionConverter);
            SuggestionValueConverter<T> vConverter = Optional
                    .ofNullable(valueConverter).orElse(defaultValueConverter);

            // Create a list of suggestion data and send it to the client
            getRpcProxy(AutocompleteExtensionClientRpc.class)
                    .showSuggestions(suggestions.stream()
                            .map(s -> new SuggestionData(vConverter.apply(s),
                                    cConverter.apply(s, query)))
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
        setSuggestionGenerator(suggestionGenerator, null, null);
    }

    public void setSuggestionGenerator(
            SuggestionGenerator<T> suggestionGenerator,
            SuggestionValueConverter valueConverter,
            SuggestionCaptionConverter<T> captionConverter) {
        this.suggestionGenerator = suggestionGenerator;
        this.valueConverter = valueConverter;
        this.captionConverter = captionConverter;
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
