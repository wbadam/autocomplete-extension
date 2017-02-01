package org.vaadin.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import org.vaadin.AutocompleteExtension;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.UListElement;
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

    private static final String CLASS_SUGGESTION_LIST = "autocomplete-suggestion-list";
    private static final String CLASS_SUGGESTION_LIST_ITEM = "autocomplete-suggestion-list-item";

    private static final String CLASS_HIDDEN = "hidden";
    private static final String CLASS_SELECTED = "selected";
    private static final String CLASS_EMPTY = "empty";

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
                        VTextField textField = ((TextFieldConnector) getParent())
                                .getWidget();
                        suggestionList.show(textField.getOffsetWidth(),
                                Style.Unit.PX);
                    }
                });
    }

    @Override
    protected void extend(ServerConnector serverConnector) {

        suggestionList.setSize(getState().suggestionListSize);

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
                suggestionList.selectNextItem();

            } else if (event.isUpArrow()) {
                suggestionList.selectPreviousItem();
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
        String value = getTextField().getValue();
        if (value.length() > 0) {
            rpc.getSuggestion(value);
        } else {
            suggestionList.hide();
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
            suggestionList.setSize(getState().suggestionListSize);
        }
    }

    @Override
    public AutocompleteExtensionState getState() {
        return (AutocompleteExtensionState) super.getState();
    }

    private class SuggestionList {
        private final UListElement suggestionList = createList();
        private final List<LIElement> suggestionListItems = new ArrayList<>();
        private int selectedIndex = -1;
        private boolean visible = false;
        private int itemCount = 0;

        private UListElement createList() {
            UListElement ul = Document.get().createULElement();
            ul.setClassName(CLASS_SUGGESTION_LIST);
            return ul;
        }

        private LIElement createItem() {
            LIElement li = Document.get().createLIElement();
            li.setClassName(CLASS_SUGGESTION_LIST_ITEM);
            return li;
        }

        public Element getElement() {
            return this.suggestionList;
        }

        private List<LIElement> getItems() {
            return this.suggestionListItems;
        }

        public void setSize(int newSize) {

            // Add suggestion items if size increased
            IntStream.range(getSize(), newSize).forEach(i -> {
                LIElement li = createItem();
                getItems().add(li);
                getElement().appendChild(li);
            });

            // Remove suggestion elements if count decreased
            IntStream.range(newSize, getSize()).forEach(i -> {
                LIElement item = getItems().remove(i + 1);
                item.removeFromParent();
            });
        }

        public int getSize() {
            return getItems().size();
        }

        public void show(double width, Style.Unit unit) {
            visible = true;
            getElement().getStyle().setWidth(width, unit);
            getElement().removeClassName(CLASS_HIDDEN);
        }

        public void hide() {
            visible = false;
            getElement().addClassName(CLASS_HIDDEN);
            selectedIndex = -1;
        }

        public boolean isVisible() {
            return visible;
        }

        public void fill(List<String> suggestions) {
            // Fill items
            Iterator<LIElement> itemIterator = getItems().iterator();
            suggestions.stream().limit(getSize())
                    .forEach(suggestion -> {
                        LIElement li = itemIterator.next();
                        li.setInnerHTML(suggestion);
                        li.removeClassName(CLASS_EMPTY);
                    });

            itemCount = getSize();

            // Clear remaining slots
            while (itemIterator.hasNext()) {
                LIElement li = itemIterator.next();
                li.setInnerHTML(null);
                li.addClassName(CLASS_EMPTY);

                itemCount--;
            }
        }

        public void selectNextItem() {
            if (isVisible()) {
                unselectItem(selectedIndex);
                selectedIndex = (selectedIndex + 2) % (itemCount + 1) - 1;
                selectItem(selectedIndex);
            }
        }

        public void selectPreviousItem() {
            if (isVisible()) {
                unselectItem(selectedIndex);
                selectedIndex = Math.floorMod(selectedIndex, itemCount + 1) - 1;
                selectItem(selectedIndex);
            }
        }

        private void selectItem(int index) {
            if (index > -1 && index < getSize()) {
                getItems().get(index).addClassName(CLASS_SELECTED);
            }
        }

        private void unselectItem(int index) {
            if (index > -1 && index < getSize()) {
                getItems().get(index).removeClassName(CLASS_SELECTED);
            }
        }
    }
}