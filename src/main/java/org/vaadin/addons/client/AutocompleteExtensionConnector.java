package org.vaadin.addons.client;

import java.util.Objects;

import org.vaadin.addons.AutocompleteExtension;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.VTextField;
import com.vaadin.client.ui.textfield.TextFieldConnector;
import com.vaadin.shared.ui.Connect;

import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.EventTarget;

@Connect(AutocompleteExtension.class)
public class AutocompleteExtensionConnector extends AbstractExtensionConnector {

    /**
     * Timer for delaying server RPCs for requesting suggestion items.
     */
    private static class SuggestionTimer extends Timer {
        private final AutocompleteExtensionServerRpc rpc;
        private String query;

        SuggestionTimer(AutocompleteExtensionServerRpc rpc) {
            this.rpc = rpc;
        }

        @Override
        public void run() {
            rpc.getSuggestion(query);
        }

        /**
         * Schedule calling autocomplete RPC.
         *
         * @param query
         * @param delayMillis
         */
        void schedule(String query, int delayMillis) {
            this.query = query;
            schedule(delayMillis);
        }
    }

    private final EventListener onInput = this::onInput;

    private final SuggestionList suggestionList = new SuggestionList();

    private final AutocompleteExtensionServerRpc rpc = RpcProxy
            .create(AutocompleteExtensionServerRpc.class, this);

    private final SuggestionTimer suggestionTimer = new SuggestionTimer(rpc);

    public AutocompleteExtensionConnector() {
        registerRpc(AutocompleteExtensionClientRpc.class,
                (suggestions, query) -> {
                    // Make sure that the received suggestions are not outdated
                    if (Objects.equals(query, getTextField().getValue())) {
                        // Fill suggestion list with captions
                        suggestionList.fill(suggestions);

                        // Show and set width
                        suggestionList.show(getTextField().getOffsetWidth(),
                                Style.Unit.PX);
                    }
                });
    }

    @Override
    protected void extend(ServerConnector serverConnector) {

        suggestionList.setMaxSize(getState().suggestionListSize);

        VTextField textField = getTextField();

        textField.addAttachHandler(event -> {
            if (event.isAttached()) {
                DOM.appendChild(textField.getElement().getParentElement(),
                        suggestionList.getElement());
            } else {
                suggestionList.getElement().removeFromParent();
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
        EventTarget textFieldTarget = textField.getElement().cast();
        textFieldTarget.addEventListener(Event.INPUT, onInput);

        // Hide suggestion list when field looses focus
        textField.addBlurHandler(event -> suggestionList.hide());

        // Register suggestion click listener
        suggestionList.setItemClickHandler(this::onSuggestionSelected);
    }

    private void onSuggestionSelected() {
        // Fill textfield with suggested content
        getTextField().setValue(suggestionList.getSelectedItem().getValue());

        // Hide suggestion list
        suggestionList.hide();
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        // Remove input event listener
        EventTarget textFieldTarget = getTextField().getElement().cast();
        textFieldTarget.removeEventListener(Event.INPUT, onInput);
    }

    private VTextField getTextField() {
        return ((TextFieldConnector) getParent()).getWidget();
    }

    private void onInput(Event event) {
        if (!getTextField().getValue().isEmpty()) {
            showSuggestionsFor(getTextField().getValue(),
                    getState().suggestionDelay);
        } else {
            suggestionList.hide();
        }
    }

    private void showSuggestionsFor(String text) {
        showSuggestionsFor(text, 0);
    }

    private void showSuggestionsFor(String text, int delayMillis) {
        if (Objects.nonNull(text) && !text.isEmpty()) {
            suggestionTimer.schedule(text, delayMillis);
        } else {
            suggestionTimer.cancel();
        }
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

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        if (stateChangeEvent.hasPropertyChanged("suggestionListSize")) {
            suggestionList.setMaxSize(getState().suggestionListSize);
        }
    }

    @Override
    public AutocompleteExtensionState getState() {
        return (AutocompleteExtensionState) super.getState();
    }
}
