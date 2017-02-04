package org.vaadin.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.vaadin.client.jsinterop.JsEventTarget;
import org.vaadin.client.jsinterop.JsEventListener;

import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.LIElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.UListElement;
import com.google.gwt.user.client.Event;

/**
 * Represents a suggestion list.
 */
class SuggestionList {

    private static final String CLASS_SUGGESTION_LIST = "autocomplete-suggestion-list";
    private static final String CLASS_SUGGESTION_LIST_ITEM = "autocomplete-suggestion-list-item";

    private static final String CLASS_HIDDEN = "hidden";
    private static final String CLASS_SELECTED = "selected";
    private static final String CLASS_EMPTY = "empty";

    /**
     * List {@code <ul>} element.
     */
    private final UListElement ul = createList();

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
     * Callback function for item click event
     */
    private Runnable itemClickHandler;

    private UListElement createList() {
        UListElement ul = Document.get().createULElement();
        ul.setClassName(CLASS_SUGGESTION_LIST);
        return ul;
    }

    /**
     * Returns the root DOM element of this suggestion list.
     *
     * @return Root {@code <ul>} element.
     */
    public Element getElement() {
        return ul;
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
     *         List of suggestions in HTML format.
     */
    public void fill(List<String> suggestions) {
        // Fill items
        Iterator<SuggestionItem> itemIterator = items.iterator();
        suggestions.stream().limit(items.size()).forEach(
                suggestion -> itemIterator.next().setContent(suggestion));

        while (itemIterator.hasNext()) {
            itemIterator.next().setContent(null);
        }
    }

    /**
     * Show suggestion list.
     *
     * @param width
     *         Width of suggestion list to be set.
     * @param unit
     *         Unit of width.
     */
    public void show(double width, Style.Unit unit) {
        ul.getStyle().setWidth(width, unit);
        ul.removeClassName(CLASS_HIDDEN);
        visible = true;
    }

    /**
     * Hide suggestion list.
     */
    public void hide() {
        Optional.ofNullable(selectedItem).ifPresent(SuggestionItem::deselect);
        ul.addClassName(CLASS_HIDDEN);
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
        private JsEventListener onMouseDown = this::onMouseDown;

        /**
         * On click callback.
         */
        private JsEventListener onClick = this::onClick;

        private SuggestionItem() {
            setContent(null);
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
            JsEventTarget li = (JsEventTarget) this.li;
            li.addEventListener(BrowserEvents.MOUSEDOWN, onMouseDown);
            li.addEventListener(BrowserEvents.CLICK, onClick);
        }

        private void removeFromParent() {
            // Unregister event listeners
            JsEventTarget li = (JsEventTarget) this.li;
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
         * Set content of this item as HTML.
         *
         * @param html
         *         Content to be set.
         */
        public void setContent(String html) {
            this.li.setInnerHTML(html);

            // Set 'empty' class name
            if (html == null || html.isEmpty()) {
                this.li.addClassName(CLASS_EMPTY);
            } else {
                this.li.removeClassName(CLASS_EMPTY);
            }
        }

        /**
         * Returns content of this item as HTML.
         *
         * @return Content of this item as HTML.
         */
        public String getContent() {
            return this.li.getInnerHTML();
        }

        /**
         * Tells if this item is empty.
         *
         * @return {@code true} if content is null or empty string, {@code
         * false} otherwise.
         */
        public boolean isEmpty() {
            return getContent() == null || getContent().isEmpty();
        }
    }
}
