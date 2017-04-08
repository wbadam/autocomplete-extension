package org.vaadin.addons.autocomplete.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.safehtml.shared.annotations.IsSafeHtml;

import elemental.events.Event;
import elemental.events.EventListener;
import elemental.events.EventTarget;

/**
 * Represents a suggestion list.
 */
class SuggestionList {

    private static final String CLASS_SUGGESTION_LIST_WRAPPER = "autocomplete-suggestion-list-wrapper";
    private static final String CLASS_SUGGESTION_LIST = "autocomplete-suggestion-list";
    private static final String CLASS_SUGGESTION_LIST_ITEM = "autocomplete-suggestion-list-item";

    private static final String CLASS_HIDDEN = "hidden";
    private static final String CLASS_SELECTED = "selected";
    private static final String CLASS_EMPTY = "empty";

    /**
     * Wrapper element of the suggestion list.
     */
    private final DivElement wrapper = createWrapper();

    /**
     * List {@code <ul>} element.
     */
    private final UListElement ul = createList(wrapper);

    /**
     * Collection of suggestion items. Items contain {@code <li>} elements.
     */
    private final List<SuggestionItem> items = new ArrayList<>();

    /**
     * Selected suggestion item.
     */
    private SuggestionItem selectedItem = null;

    /**
     * Stores suggestion list visibility.
     */
    private boolean visible = false;

    /**
     * The current search query
     */
    private String query;

    /**
     * Callback function for item click event
     */
    private Runnable itemClickHandler;

    public SuggestionList() {
        init();
    }

    private void init() {
        hide();
    }

    private DivElement createWrapper() {
        DivElement div = Document.get().createDivElement();
        div.setClassName(CLASS_SUGGESTION_LIST_WRAPPER);
        return div;
    }

    private UListElement createList(Element wrapper) {
        UListElement ul = Document.get().createULElement();
        ul.setClassName(CLASS_SUGGESTION_LIST);
        wrapper.appendChild(ul);
        return ul;
    }

    /**
     * Returns the root DOM element of this suggestion list.
     *
     * @return Root {@code <div>} element.
     */
    public Element getElement() {
        return wrapper;
    }

    /**
     * Sets the maximum size of the suggestion list.
     *
     * @param size
     *         Required max size of the suggestion list.
     */
    public void setMaxSize(int size) {

        // Add suggestion items if size increased
        IntStream.range(items.size(), size)
                .forEach(i -> addItem(new SuggestionItem()));

        // Remove suggestion elements if count decreased
        IntStream.range(size, items.size()).forEach(this::removeItem);
    }

    /**
     * The actual size of the suggestion list.
     *
     * @return Number of items showing on the suggestion list.
     */
    public int getActualSize() {
        return (int) items.stream().filter(i -> !i.isEmpty()).count();
    }

    private void addItem(SuggestionItem item) {
        items.add(item);
        item.addToParent(ul);
    }

    private void removeItem(int index) {
        items.remove(index).removeFromParent();
    }

    /**
     * Set content of suggestion items.
     *
     * @param suggestions
     *         List of suggestion data.
     */
    public void fill(List<SuggestionData> suggestions, String query) {
        // Fill items
        Iterator<SuggestionItem> itemIterator = items.iterator();
        suggestions.stream().limit(items.size()).forEach(
                suggestion -> itemIterator.next()
                        .setContent(suggestion.getKey(), suggestion.getValue(),
                                suggestion.getCaption()));

        // Clear the rest of the items
        while (itemIterator.hasNext()) {
            itemIterator.next().clear();
        }

        // Set current search term
        this.query = query;
    }

    /**
     * Show suggestion list.
     *
     * @param width
     *         Width of suggestion list to be set.
     */
    public void show(String width) {
        wrapper.getStyle().setProperty("width", width);
        wrapper.removeClassName(CLASS_HIDDEN);
        visible = true;
    }

    /**
     * Hide suggestion list.
     */
    public void hide() {
        Optional.ofNullable(selectedItem).ifPresent(SuggestionItem::deselect);
        wrapper.addClassName(CLASS_HIDDEN);
        visible = false;
    }

    /**
     * Is suggestion list visible.
     *
     * @return {@code true} if suggestion list visible, {@code false} otherwise.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Get the query that belongs to the suggestion list.
     *
     * @return Search query
     */
    public String getQuery() {
        return query;
    }

    /**
     * Returns the index of selected item in the suggestion list.
     *
     * @return Index of selected item or {@code -1} if none selected.
     */
    public int getSelectedIndex() {
        return items.indexOf(selectedItem);
    }

    /**
     * Get item on specified index.
     *
     * @param index
     *         Position of item.
     * @return Item of suggestion list on {@code index} position.
     */
    public SuggestionItem getItem(int index) {
        return items.get(index);
    }

    /**
     * Get selected item.
     *
     * @return Selected item or {@code null} if none selected.
     */
    public SuggestionItem getSelectedItem() {
        return selectedItem;
    }

    /**
     * Set callback function for click event on a selection list item.
     *
     * @param handler
     *         Function to be called when click event happens.
     */
    public void setItemClickHandler(Runnable handler) {
        itemClickHandler = handler;
    }

    /**
     * Represents an item in the suggestion list.
     */
    class SuggestionItem {

        /**
         * List {@code <li>} element.
         */
        private final LIElement li = createItem();

        /**
         * Mouse down callback.
         */
        private EventListener onMouseDown = this::onMouseDown;

        /**
         * On click callback.
         */
        private EventListener onClick = this::onClick;

        /**
         * Identifier of the suggestion item.
         */
        private String key;

        /**
         * Value of this suggestion item. This will be set for text field when
         * selected.
         */
        private String value;

        private SuggestionItem() {
            setContent(null, null, null);
        }

        private LIElement createItem() {
            LIElement li = Document.get().createLIElement();
            li.setClassName(CLASS_SUGGESTION_LIST_ITEM);
            return li;
        }

        private void addToParent(Element parent) {
            // Set parent element
            parent.appendChild(this.li);

            // Register event listeners
            EventTarget li = this.li.cast();
            li.addEventListener(BrowserEvents.MOUSEDOWN, onMouseDown);
            li.addEventListener(BrowserEvents.CLICK, onClick);
        }

        private void removeFromParent() {
            // Unregister event listeners
            EventTarget li = this.li.cast();
            li.removeEventListener(BrowserEvents.MOUSEDOWN, onMouseDown);
            li.removeEventListener(BrowserEvents.CLICK, onClick);

            // Remove from parent
            this.li.removeFromParent();
        }

        private void onMouseDown(Event event) {
            // Prevent blur on text field before click
            event.preventDefault();
        }

        private void onClick(Event event) {
            // Set selection on current item
            select();

            // Call selection click handler
            Optional.ofNullable(itemClickHandler).ifPresent(Runnable::run);
        }

        /**
         * Selects this item while deselecting the previously selected one.
         */
        public void select() {
            Optional.ofNullable(selectedItem).ifPresent(item -> {
                item.li.removeClassName(CLASS_SELECTED);
            });

            this.li.addClassName(CLASS_SELECTED);

            selectedItem = this;
        }

        /**
         * Deselects this item.
         */
        public void deselect() {
            this.li.removeClassName(CLASS_SELECTED);

            selectedItem = null;
        }

        /**
         * Set content for this item.
         *
         * @param key
         *         Identifier for keeping track of the server side item.
         * @param value
         *         Value to be set for text field when selected.
         * @param caption
         *         Safe HTML caption to be displayed as the visible content.
         */
        public void setContent(String key, String value,
                @IsSafeHtml String caption) {
            this.li.setInnerHTML(caption);
            this.value = value;
            this.key = key;

            // Add or remove 'empty' class name
            if (isEmpty()) {
                this.li.addClassName(CLASS_EMPTY);
            } else {
                this.li.removeClassName(CLASS_EMPTY);
            }
        }

        /**
         * Returns caption of this item as HTML.
         *
         * @return Visible caption of this item as HTML.
         */
        public String getCaption() {
            return this.li.getInnerHTML();
        }

        /**
         * Returns the identifier of this item.
         *
         * @return Identifier of this item.
         */
        public String getKey() {
            return key;
        }

        /**
         * Returns the value of this item to be set for the text field when the
         * item is selected
         *
         * @return Value of this item.
         */
        public String getValue() {
            return this.value;
        }

        /**
         * Clear this item. Removes caption and value.
         */
        public void clear() {
            setContent(null, null, null);
        }

        /**
         * Tells if this item is empty.
         *
         * @return {@code true} if content or value is null or empty string,
         * {@code false} otherwise.
         */
        public boolean isEmpty() {
            return (getCaption() == null || getCaption().isEmpty()) && (
                    value == null || value.isEmpty());
        }
    }
}
