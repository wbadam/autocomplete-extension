package org.vaadin.addons.autocomplete.client;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.vaadin.addons.autocomplete.AutocompleteExtension;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.StyleConstants;
import com.vaadin.client.annotations.OnStateChange;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.event.InputEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.VTextField;
import com.vaadin.client.ui.textfield.TextFieldConnector;
import com.vaadin.shared.ui.Connect;

@Connect(AutocompleteExtension.class)
public class AutocompleteExtensionConnector extends AbstractExtensionConnector {

    private static final String CLASS_AUTOCOMPLETE_WRAPPER = "autocomplete-textfield";

    /**
     * Timer for delaying server RPCs for requesting suggestion items.
     */
    private static class SuggestionTimer extends Timer {
        private final AutocompleteExtensionServerRpc rpc;
        private String query;
        private String previousQuery;

        SuggestionTimer(AutocompleteExtensionServerRpc rpc) {
            this.rpc = rpc;
        }

        @Override
        public void run() {
            rpc.getSuggestion(query, previousQuery);
        }

        /**
         * Schedule calling autocomplete RPC.
         *
         * @param query
         * @param previousQuery
         * @param delayMillis
         */
        void schedule(String query, String previousQuery, int delayMillis) {
            this.query = query;
            this.previousQuery = previousQuery;
            schedule(delayMillis);
        }
    }

    private final SuggestionList suggestionList;
    private final Element wrapper = createWrapper();

    private final AutocompleteExtensionServerRpc rpc = RpcProxy
            .create(AutocompleteExtensionServerRpc.class, this);

    private final SuggestionTimer suggestionTimer = new SuggestionTimer(rpc);

    private VTextField textField;

    private HandlerRegistration inputHandler;
    private HandlerRegistration attachHandler;

    public AutocompleteExtensionConnector() {
        suggestionList = new SuggestionList();

        registerRpc(AutocompleteExtensionClientRpc.class,
                new AutocompleteExtensionClientRpc() {
                    @Override
                    public void showSuggestions(
                            List<SuggestionData> suggestions, String query) {
                        if (Objects.equals(query, textField.getValue())
                                && suggestions != null
                                && suggestions.size() > 0) {
                            // Fill suggestion list with captions
                            suggestionList.fill(suggestions, query);

                            // Show and set width
                            suggestionList
                                    .show(textField.getOffsetWidth() + "px");
                        } else {
                            suggestionList.hide();
                        }

                        wrapper.removeClassName("autocomplete-loading");
                    }

                    @Override
                    public void triggerShowSuggestions() {
                        showSuggestionsFor(textField.getValue());
                    }

                    @Override
                    public void triggerHideSuggestions() {
                        suggestionList.hide();
                    }
                });
    }

    private Element createWrapper() {
        Element elem = DOM.createDiv();
        elem.addClassName(CLASS_AUTOCOMPLETE_WRAPPER);
        elem.addClassName(StyleConstants.UI_WIDGET);
        return elem;
    }

    @Override
    protected void extend(ServerConnector serverConnector) {

        textField = ((TextFieldConnector) serverConnector).getWidget();

        suggestionList.setMaxSize(getState().suggestionListSize);

        if (textField.isAttached()) {
            addExtensionElements();
        }

        attachHandler = textField.addAttachHandler(event -> {
            if (event.isAttached()) {
                addExtensionElements();
            } else {
                removeExtensionElements();
            }
        });

        // Handle arrow key events
        textField.addKeyUpHandler(event -> {
            if (event.isDownArrow()) {
                if (!suggestionList.isVisible()) {
                    showSuggestionsFor(textField.getValue());
                } else {
                    selectNextItem();
                }
            } else if (event.isUpArrow()) {
                selectPreviousItem();
            }
        });

        textField.addKeyDownHandler(event -> {
            if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
                suggestionList.hide();
            } else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER
                    && suggestionList.getSelectedItem() != null) {
                onSuggestionSelected();
                // Prevent handler added to text field from handling when suggestion list was open
                // TODO: 03/02/2017 Test if works as intended
                event.preventDefault();
            } else if (event.isUpArrow()) {
                // Prevent cursor from jumping to beginning of text
                event.preventDefault();
            }
        });

        // Add listener for input event
        inputHandler = textField
                .addDomHandler(this::onInput, InputEvent.getType());

        // Hide suggestion list when field looses focus
        textField.addBlurHandler(event -> suggestionList.hide());

        // Register suggestion click listener
        suggestionList.setItemClickHandler(this::onSuggestionSelected);
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        // Remove autocomplete elements
        if (textField.isAttached()) {
            removeExtensionElements();
        }

        // Remove input event listener
        Optional.ofNullable(inputHandler)
                .ifPresent(HandlerRegistration::removeHandler);

        // Remove text field attach handler
        Optional.ofNullable(attachHandler)
                .ifPresent(HandlerRegistration::removeHandler);
    }

    private void addExtensionElements() {
        Element textElement = textField.getElement();
        textElement.getParentElement().insertBefore(wrapper, textElement);
        wrapper.appendChild(textElement);
        wrapper.appendChild(suggestionList.getElement());
    }

    private void removeExtensionElements() {
        Element textElement = textField.getElement();
        wrapper.getParentElement().insertBefore(textElement, wrapper);
        wrapper.removeFromParent();
    }

    private void onSuggestionSelected() {
        final SuggestionList.SuggestionItem selectedItem = suggestionList
                .getSelectedItem();

        // Fill textfield with suggested content
        textField.setValue(selectedItem.getValue());

        // Hide suggestion list
        suggestionList.hide();

        // Fire suggestion select event
        rpc.suggestionSelected(selectedItem.getKey(), selectedItem.getValue());
    }

    private void onInput(InputEvent event) {
        showSuggestionsFor(textField.getValue(), getState().suggestionDelay);
    }

    private void showSuggestionsFor(String text) {
        showSuggestionsFor(text, 0);
    }

    private void showSuggestionsFor(String text, int delayMillis) {
        if (text.equals("")) {
            wrapper.removeClassName("autocomplete-loading");
        } else {
            wrapper.addClassName("autocomplete-loading");
        }
        suggestionTimer.schedule(text, suggestionList.getQuery(), delayMillis);
    }

    private void selectNextItem() {
        if (suggestionList.getActualSize() > 0) {
            int index = suggestionList.getSelectedIndex();

            if (index < suggestionList.getActualSize() - 1) {
                suggestionList.getItem(index + 1).select();
            } else {
                suggestionList.getItem(index).deselect();
            }
        }
    }

    private void selectPreviousItem() {
        if (suggestionList.getActualSize() > 0) {
            int index = suggestionList.getSelectedIndex();

            if (index > 0) {
                suggestionList.getItem(index - 1).select();
            } else if (index == 0) {
                suggestionList.getItem(index).deselect();
            } else {
                suggestionList.getItem(suggestionList.getActualSize() - 1)
                        .select();
            }
        }
    }

    @OnStateChange("suggestionListSize")
    private void changeSuggestionListSize() {
        suggestionList.setMaxSize(getState().suggestionListSize);
    }

    @Override
    public AutocompleteExtensionState getState() {
        return (AutocompleteExtensionState) super.getState();
    }
}
