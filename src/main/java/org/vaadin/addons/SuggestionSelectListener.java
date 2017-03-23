package org.vaadin.addons;

import java.lang.reflect.Method;

import com.vaadin.event.SerializableEventListener;

/**
 * Listener interface for suggestion select events.
 *
 * @since 0.1.5
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
