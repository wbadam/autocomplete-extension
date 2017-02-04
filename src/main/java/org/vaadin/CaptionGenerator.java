package org.vaadin;

import java.util.function.BiFunction;

/**
 *
 * @param <T>
 */
public interface CaptionGenerator<T> extends BiFunction<T, String, String> {

    /**
     *
     * @param suggestion
     * @param query
     * @return
     */
    @Override
    String apply(T suggestion, String query);
}
