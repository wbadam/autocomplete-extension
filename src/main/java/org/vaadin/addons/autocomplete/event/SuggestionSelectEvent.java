package org.vaadin.addons.autocomplete.event;

import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;

/**
 * Server side event that is triggered when a suggestion is selected from the
 * list.
 *
 * @since 0.2.0
 */
public class SuggestionSelectEvent extends Component.Event {

    private final String selectedValue;

    /**
     * Creates a suggestion select event.
     *
     * @param source
     *         {@code TextField} component that originated the event.
     */
    public SuggestionSelectEvent(TextField source, String selectedValue) {
        super(source);

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
}
