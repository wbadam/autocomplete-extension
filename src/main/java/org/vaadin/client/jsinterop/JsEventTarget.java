package org.vaadin.client.jsinterop;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * JS Interop interface for adding/removing event listener to/from Element.
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "EventTarget")
public interface JsEventTarget {

    /**
     * Add listener for {@code eventType} event. Store {@code listener}
     * reference to be able to remove later.
     *
     * @param eventType
     *         Type of event.
     * @param listener
     *         Event callback.
     */
    public void addEventListener(String eventType, JsEventListener listener);

    /**
     * Remove listener for {@code eventType} event. {@code listener} needs to be
     * the same instance as the one added earlier.
     *
     * @param eventType
     *         Type of event.
     * @param listener
     *         Event callback.
     */
    public void removeEventListener(String eventType, JsEventListener listener);
}
