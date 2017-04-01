package org.vaadin.addons.autocomplete.event;

import java.util.Optional;

import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;

/**
 * Server side event that is triggered when a suggestion is selected from the
 * list.
 *
 * @since 0.2.0
 */
public class SuggestionSelectEvent<T> extends Component.Event {

    private final String selectedValue;

    private final T selectedItem;

    /**
     * Creates a suggestion select event.
     *
     * @param source
     *         {@code TextField} component that originated the event.
     * @param selectedItem
     *         The selected item. May be {@literal null} if the server side
     *         doesn't keep reference to the suggested items.
     * @param selectedValue
     *         The value of the selected item.
     */
    public SuggestionSelectEvent(TextField source, T selectedItem,
            String selectedValue) {
        super(source);

        this.selectedItem = selectedItem;
        this.selectedValue = selectedValue;
    }

    /**
     * Gets the value of the selected item from the suggestion list.
     *
     * @return Selected suggestion item's value.
     */
    public String getSelectedValue() {
        return selectedValue;
    }

    /**
     * Gets the selected item from the suggestion list.
     *
     * @return Optional selected item or empty if the item's server side
     * reference is missing.
     */
    public Optional<T> getSelectedItem() {
        return Optional.ofNullable(selectedItem);
    }
}
