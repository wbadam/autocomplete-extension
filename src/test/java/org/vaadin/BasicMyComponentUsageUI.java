package org.vaadin;

import com.vaadin.ui.Component;
import org.vaadin.addonhelpers.AbstractTest;

/**
 * Add many of these with different configurations,
 * combine with different components, for regressions
 * and also make them dynamic if needed.
 */
public class BasicMyComponentUsageUI extends AbstractTest {

    @Override
    public Component getTestComponent() {
        MyComponent clearableTextBox = new MyComponent();
        return clearableTextBox;
    }

}
