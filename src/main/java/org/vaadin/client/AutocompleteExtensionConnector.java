package org.vaadin.client;

import java.util.List;

import org.vaadin.AutocompleteExtension;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.ui.VTextField;
import com.vaadin.client.ui.textfield.TextFieldConnector;
import com.vaadin.shared.ui.Connect;

@Connect(AutocompleteExtension.class)
public class AutocompleteExtensionConnector extends AbstractExtensionConnector {

    private final JavaScriptObject inputEventListener = createNativeFunction(
            this::onInput);

    private final SuggestionList suggestionList = new SuggestionList();

    private AutocompleteExtensionServerRpc rpc = RpcProxy
            .create(AutocompleteExtensionServerRpc.class, this);

    public AutocompleteExtensionConnector() {
        registerRpc(AutocompleteExtensionClientRpc.class,
                new AutocompleteExtensionClientRpc() {
                    @Override
                    public void showSuggestions(List<String> suggestions) {

                        // Fill suggestion list with suggestions
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
            } else if (event.isUpArrow()) {
                // Prevent cursor from jumping to beginning of text
                event.preventDefault();
            }
        });

        addEventListener(textField.getElement(), "input", inputEventListener);
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        removeEventListener(getTextField().getElement(), "input",
                inputEventListener);
    }

    private VTextField getTextField() {
        return ((TextFieldConnector) getParent()).getWidget();
    }

    private void onInput(Event event) {
        if (!getTextField().getValue().isEmpty()) {
            showSuggestionsFor(getTextField().getValue());
        } else {
            suggestionList.hide();
        }
    }

    private void showSuggestionsFor(String text) {
        if (!text.isEmpty()) {
            rpc.getSuggestion(text);
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

    private native void addEventListener(Element element, String eventName,
            JavaScriptObject listenerFunction)/*-{
        element.addEventListener(eventName, listenerFunction, false);
    }-*/;

    private native void removeEventListener(Element element, String eventName,
            JavaScriptObject listener)/*-{
        element.removeEventListener(eventName, listener, false);
    }-*/;

    private native JavaScriptObject createNativeFunction(
            EventListener listener)/*-{
        return $entry(function (event) {
            listener.@com.google.gwt.user.client.EventListener::onBrowserEvent(*)(event);
        });
    }-*/;

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
