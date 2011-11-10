/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
 */
package com.sun.javafx.scene.control.skin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import org.junit.Before;
import org.junit.Test;

/**
 * @author mickf
 */
public class ScrollPaneSkinTest {
    private ScrollPane scrollPane;
    private ScrollPaneSkinMock skin;

    @Before public void setup() {
        scrollPane = new ScrollPane();
        skin = new ScrollPaneSkinMock(scrollPane);
        scrollPane.setSkin(skin);
    }

    /*
    ** RT-16641 : root cause, you shouldn't be able to drag
    ** contents if they don't fill the scrollpane
    */
    @Test public void shouldntDragContentSmallerThanViewport() {
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        StackPane sp = new StackPane();
        sp.setPrefWidth(80);
        sp.setPrefHeight(80);
    
        scrollPane.setContent(sp);
        scrollPane.setTranslateX(70);
        scrollPane.setTranslateY(30);
        scrollPane.setPrefWidth(100);
        scrollPane.setPrefHeight(100);
        scrollPane.setPannable(true);

        MouseEventGenerator generator = new MouseEventGenerator();

        Scene scene = new Scene(new Group(), 400, 400);
        ((Group) scene.getRoot()).getChildren().clear();
        ((Group) scene.getRoot()).getChildren().add(scrollPane);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

        double originalValue = scrollPane.getVvalue();

        Event.fireEvent(sp, generator.generateMouseEvent(MouseEvent.MOUSE_PRESSED, 50, 50));
        Event.fireEvent(sp, generator.generateMouseEvent(MouseEvent.MOUSE_DRAGGED, 75, 75));
        Event.fireEvent(sp, generator.generateMouseEvent(MouseEvent.MOUSE_RELEASED, 75, 75));

        assertEquals(originalValue, scrollPane.getVvalue(), 0.01);

    }

    /*
    ** check we can drag contents that are larger than the scrollpane
    */
    @Test public void shouldDragContentLargerThanViewport() {
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        StackPane sp = new StackPane();
        sp.setPrefWidth(180);
        sp.setPrefHeight(180);
    
        scrollPane.setContent(sp);
        scrollPane.setTranslateX(70);
        scrollPane.setTranslateY(30);
        scrollPane.setPrefWidth(100);
        scrollPane.setPrefHeight(100);
        scrollPane.setPannable(true);

        MouseEventGenerator generator = new MouseEventGenerator();

        Scene scene = new Scene(new Group(), 400, 400);
        ((Group) scene.getRoot()).getChildren().clear();
        ((Group) scene.getRoot()).getChildren().add(scrollPane);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

        double originalValue = scrollPane.getVvalue();

        Event.fireEvent(sp, generator.generateMouseEvent(MouseEvent.MOUSE_PRESSED, 50, 50));
        Event.fireEvent(sp, generator.generateMouseEvent(MouseEvent.MOUSE_DRAGGED, 75, 75));
        Event.fireEvent(sp, generator.generateMouseEvent(MouseEvent.MOUSE_RELEASED, 75, 75));

        assertTrue(originalValue < scrollPane.getVvalue());

    }

    public static final class ScrollPaneSkinMock extends ScrollPaneSkin {
        boolean propertyChanged = false;
        int propertyChangeCount = 0;
        public ScrollPaneSkinMock(ScrollPane scrollPane) {
            super(scrollPane);
        }
        
        @Override protected void handleControlPropertyChanged(String p) {
            super.handleControlPropertyChanged(p);
            propertyChanged = true;
            propertyChangeCount++;
        }
    }

    private static class MouseEventGenerator {
        private boolean primaryButtonDown = false;

        public MouseEvent generateMouseEvent(EventType<MouseEvent> type,
                double x, double y) {

            MouseButton button = MouseButton.NONE;
            if (type == MouseEvent.MOUSE_PRESSED ||
                    type == MouseEvent.MOUSE_RELEASED ||
                    type == MouseEvent.MOUSE_DRAGGED) {
                button = MouseButton.PRIMARY;
            }

            if (type == MouseEvent.MOUSE_PRESSED ||
                    type == MouseEvent.MOUSE_DRAGGED) {
                primaryButtonDown = true;
            }

            if (type == MouseEvent.MOUSE_RELEASED) {
                primaryButtonDown = false;
            }

            MouseEvent event = MouseEvent.impl_mouseEvent(x, y, x, y, button,
                    1, false, false, false, false, false, primaryButtonDown,
                    false, false, type);

            return event;
        }
    }
}
