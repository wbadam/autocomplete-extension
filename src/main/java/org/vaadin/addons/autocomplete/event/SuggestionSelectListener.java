package org.vaadin.addons.autocomplete.event;

import java.lang.reflect.Method;

import com.vaadin.event.SerializableEventListener;

/**
 * Listener interface for suggestion select events.
 *
 * @since 0.2.0
 */
@FunctionalInterface
public interface SuggestionSelectListener extends SerializableEventListener {

    static final Method SUGGESTION_SELECT_METHOD = SuggestionSelectListener.class
            .getDeclaredMethods()[0];

    /**
     * Method called when a suggestion select event is triggered.
     *
     * @param event
     *         Suggestion select event.
     */
    public void suggestionSelect(SuggestionSelectEvent event);
}
