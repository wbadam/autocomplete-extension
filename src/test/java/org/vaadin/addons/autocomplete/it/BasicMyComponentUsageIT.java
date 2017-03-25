package org.vaadin.addons.autocomplete.it;

import org.vaadin.addonhelpers.automated.AbstractWebDriverCase;

/**
 * A simple example that uses Selenium to do a browser level test for a
 * BasicMyComponentUsageUI. For more complex tests, consider using page
 * object pattern.
 */
public class BasicMyComponentUsageIT extends AbstractWebDriverCase {

//    @Test
//    public void testJavaScriptComponentWithBrowser() throws InterruptedException {
//
//        startBrowser();
//
//        driver.navigate().to(
//                BASEURL + BasicMyComponentUsageUI.class.getName());
//
//        // Consider using Vaadin TestBench to make stuff easier
//        new WebDriverWait(driver, 30).until(VaadinConditions.ajaxCallsCompleted());
//
//        final WebElement el = driver.findElement(By.cssSelector(".mycomponent"));
//
//        String origText = el.getText();
//
//        el.click();
//
//        new WebDriverWait(driver, 30).until(VaadinConditions.ajaxCallsCompleted());
//
//        String newText = el.getText();
//
//        assertNotSame(origText, newText);
//
//        assertEquals("You have clicked 1 times", newText);
//
//        // Just for demo purposes, keep the UI open for a while
//        Thread.sleep(1000);
//
//    }
}
