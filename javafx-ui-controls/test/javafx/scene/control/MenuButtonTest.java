/*
 * Copyright (c) 2010, Oracle and/or its affiliates. All rights reserved.
 */

package javafx.scene.control;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Side;
import javafx.scene.shape.Rectangle;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author lubermud
 */
public class MenuButtonTest {
    private MenuButton menuButton;

    @Before public void setup() {
        menuButton = new MenuButton();
    }

    @Test public void defaultConstructorShouldHaveNoGraphic() {
        assertNull(menuButton.getGraphic());
    }

    @Test public void defaultConstructorShouldHaveNullString() {
        assertEquals("", menuButton.getText());
    }

    @Test public void oneArgConstructorShouldHaveNoGraphic1() {
        MenuButton mb2 = new MenuButton(null);
        assertNull(mb2.getGraphic());
    }

    @Test public void oneArgConstructorShouldHaveNoGraphic2() {
        MenuButton mb2 = new MenuButton("");
        assertNull(mb2.getGraphic());
    }

    @Test public void oneArgConstructorShouldHaveNoGraphic3() {
        MenuButton mb2 = new MenuButton("Hello");
        assertNull(mb2.getGraphic());
    }

    @Test public void oneArgConstructorShouldHaveSpecifiedString1() {
        MenuButton mb2 = new MenuButton(null);
        assertEquals("", mb2.getText());
    }

    @Test public void oneArgConstructorShouldHaveSpecifiedString2() {
        MenuButton mb2 = new MenuButton("");
        assertEquals("", mb2.getText());
    }

    @Test public void oneArgConstructorShouldHaveSpecifiedString3() {
        MenuButton mb2 = new MenuButton("Hello");
        assertEquals("Hello", mb2.getText());
    }

    @Test public void twoArgConstructorShouldHaveSpecifiedGraphic1() {
        MenuButton mb2 = new MenuButton(null, null);
        assertNull(mb2.getGraphic());
    }

    @Test public void twoArgConstructorShouldHaveSpecifiedGraphic2() {
        Rectangle rect = new Rectangle();
        MenuButton mb2 = new MenuButton("Hello", rect);
        assertSame(rect, mb2.getGraphic());
    }

    @Test public void twoArgConstructorShouldHaveSpecifiedString1() {
        MenuButton mb2 = new MenuButton(null, null);
        assertEquals("", mb2.getText());
    }

    @Test public void twoArgConstructorShouldHaveSpecifiedString2() {
        Rectangle rect = new Rectangle();
        MenuButton mb2 = new MenuButton("Hello", rect);
        assertEquals("Hello", mb2.getText());
    }

    @Test public void getItemsDefaultNotNull() {
        assertNotNull(menuButton.getItems());
    }

    @Test public void getItemsDefaultSizeZero() {
        assertEquals(0, menuButton.getItems().size());
    }

    @Test public void getItemsAddable() {
        menuButton.getItems().add(new MenuItem());
        assertTrue(menuButton.getItems().size() > 0);
    }

    @Test public void getItemsClearable() {
        menuButton.getItems().add(new MenuItem());
        menuButton.getItems().clear();
        assertEquals(0, menuButton.getItems().size());
    }

    @Test public void defaultIsShowingFalse() {
        assertFalse(menuButton.isShowing());
    }

    @Test public void showIsShowingTrue() {
        menuButton.show();
        assertTrue(menuButton.isShowing());
    }

    @Test public void hideIsShowingFalse1() {
        menuButton.hide();
        assertFalse(menuButton.isShowing());
    }

    @Test public void hideIsShowingFalse2() {
        menuButton.show();
        menuButton.hide();
        assertFalse(menuButton.isShowing());
    }

    @Test public void getUnspecifiedShowingProperty1() {
        assertNotNull(menuButton.showingProperty());
    }

    @Test public void getUnspecifiedShowingProperty2() {
        MenuButton mb2 = new MenuButton("", null);
        assertNotNull(mb2.showingProperty());
    }

    @Test public void unsetShowingButNotNull() {
        menuButton.showingProperty();
        assertNotNull(menuButton.isShowing());
    }

    @Test public void menuButtonIsFiredIsNoOp() {
        menuButton.fire(); // should throw no exceptions, if it does, the test fails
    }

    @Test public void defaultPopupSide() {
        assertEquals(Side.BOTTOM, menuButton.getPopupSide());
        assertEquals(Side.BOTTOM, menuButton.popupSideProperty().get());
    }

    @Test public void setNullPopupSide() {
        menuButton.setPopupSide(null);
        assertNull(menuButton.getPopupSide());
    }

    @Test public void setSpecifiedPopupSide() {
        Side side = Side.TOP;
        menuButton.setPopupSide(side);
        assertSame(side, menuButton.getPopupSide());
    }

    @Test public void getUnspecifiedPopupSideProperty1() {
        assertNotNull(menuButton.popupSideProperty());
    }

    @Test public void getUnspecifiedPopupSideProperty2() {
        MenuButton mb2 = new MenuButton("", null);
        assertNotNull(mb2.popupSideProperty());
    }

    @Test public void unsetPopupSideButNotNull() {
        menuButton.popupSideProperty();
        assertNotNull(menuButton.getPopupSide());
    }

    @Test public void popupSideCanBeBound() {
        Side side = Side.TOP;
        SimpleObjectProperty<Side> other = new SimpleObjectProperty<Side>(menuButton, "popupSide", side);
        menuButton.popupSideProperty().bind(other);
        assertSame(side, menuButton.getPopupSide());
    }

    //TODO: test show()/isShowing() for disabled=true
    //TODO: test MenuButton.impl_getPsuedoClassState
}
