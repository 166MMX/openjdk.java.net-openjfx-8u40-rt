/*
 * Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.sun.javafx.scene.control.skin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.WeakHashMap;

import static com.sun.javafx.scene.traversal.Direction.DOWN;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import com.sun.javafx.menu.MenuBase;
import com.sun.javafx.scene.control.GlobalMenuAdapter;
import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.scene.traversal.TraversalEngine;
import com.sun.javafx.scene.traversal.TraverseListener;
import com.sun.javafx.stage.StageHelper;
import com.sun.javafx.tk.Toolkit;


/**
 * The skin for the MenuBar. In essence it is a simple toolbar. For the time
 * being there is no overflow behavior and we just hide nodes which fall
 * outside the bounds.
 */
public class MenuBarSkin extends SkinBase<MenuBar, BehaviorBase<MenuBar>> implements TraverseListener {
    
    private final HBox container;

    private Menu openMenu;
    private MenuBarButton openMenuButton;
    private int focusedMenuIndex = 0;
    private TraversalEngine engine;
    private Direction direction;


    private static WeakHashMap<Stage, MenuBarSkin> systemMenuMap;
    private static List<MenuBase> emptyMenuList = new ArrayList<MenuBase>();
    private static Stage currentMenuBarStage;
    private List<MenuBase> wrappedMenus;

    private static void setSystemMenu(Stage stage) {
        if (stage.isFocused() && stage != currentMenuBarStage) {
            List<MenuBase> menuList = null;
            MenuBarSkin skin = systemMenuMap.get(stage);
            if (skin != null) {
                menuList = skin.wrappedMenus;
            }
            Toolkit.getToolkit().getSystemMenu().setMenus((menuList != null) ? menuList : emptyMenuList);
            currentMenuBarStage = stage;
        }
    }

    private static void initSystemMenuBar() {
        systemMenuMap = new WeakHashMap<Stage, MenuBarSkin>();

        final InvalidationListener focusedStageListener = new InvalidationListener() {
            @Override public void invalidated(Observable ov) {
                setSystemMenu((Stage)((ReadOnlyProperty)ov).getBean());
            }
        };

        final ObservableList<Stage> stages = StageHelper.getStages();
        for (Stage stage : stages) {
            stage.focusedProperty().addListener(focusedStageListener);
        }
        stages.addListener(new ListChangeListener<Stage>() {
            @Override public void onChanged(Change<? extends Stage> c) {
                while (c.next()) {
                    for (Stage stage : c.getRemoved()) {
                        stage.focusedProperty().removeListener(focusedStageListener);
                    }
                    for (Stage stage : c.getAddedSubList()) {
                        stage.focusedProperty().addListener(focusedStageListener);
                        setSystemMenu(stage);
                    }
                }
            }
        });
    }



    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public MenuBarSkin(final MenuBar control) {
        super(control, new BehaviorBase<MenuBar>(control));
        
        container = new HBox();
        getChildren().add(container);
        
        control.getScene().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override public void handle(KeyEvent event) {
                // process right left and may be tab key events
                switch (event.getCode()) {
                    case LEFT:
                        if (control.getScene().getWindow().isFocused()) {
                            Menu prevMenu = findPreviousSibling();
                            if (openMenu == null || ! openMenu.isShowing()) {
                                return;
                            }
                             // hide the currently visible menu, and move to the previous one
                            openMenu.hide();
                            if (!isMenuEmpty(prevMenu)) {
                                openMenu = prevMenu;
                                openMenu.show();
                            } else {
                                openMenu = null;
                            }
                        }
                        event.consume();
                        break;

                    case RIGHT:
                        if (control.getScene().getWindow().isFocused()) {
                            Menu nextMenu = findNextSibling();
                            if (openMenu == null || ! openMenu.isShowing()) {
                                return;
                            }
                             // hide the currently visible menu, and move to the next one
                            openMenu.hide();
                            if (!isMenuEmpty(nextMenu)) {
                                openMenu = nextMenu;
                                openMenu.show();
                            } else {
                                openMenu = null;
                            }
                        }
                        event.consume();
                        break;

                    case DOWN:
                    case SPACE:
                    case ENTER:
                        // RT-18859: Doing nothing for down, space and enter 
//                        if (control.getScene().getWindow().isFocused()) {
//                            if (focusedMenuIndex != -1) {
//                                if (!isMenuEmpty(getSkinnable().getMenus().get(focusedMenuIndex))) {
//                                    openMenu = getSkinnable().getMenus().get(focusedMenuIndex);
//                                    openMenu.show();
//                                } else {
//                                    openMenu = null;
//                                }
//                                event.consume();
//                            }
//                        }
                        break;
                }
               
            }
        });
        rebuildUI();
        control.getMenus().addListener(new ListChangeListener<Menu>() {
            @Override public void onChanged(Change<? extends Menu> c) {
                rebuildUI();
            }
        });
        for (final Menu menu : getSkinnable().getMenus()) {
            menu.visibleProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) {
                    rebuildUI();
                }
            });
        }

        if (Toolkit.getToolkit().getSystemMenu().isSupported()) {
            control.useSystemMenuBarProperty().addListener(new InvalidationListener() {
                @Override public void invalidated(Observable valueModel) {
                    rebuildUI();
                }
            });
        }

        // When the mouse leaves the menu, the last hovered item should lose
        // it's focus so that it is no longer selected. This code returns focus
        // to the MenuBar itself, such that keyboard navigation can continue.
          // fix RT-12254 : menu bar should not request focus on mouse exit.
//        addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                requestFocus();
//            }
//        });

        /*
        ** add an accelerator for F10.
        ** pressing f10 will select the first menu button on a menubar
        */
        KeyCode acceleratorCode = KeyCode.F10;
        KeyCodeCombination acceleratorKeyCombo =
                new KeyCodeCombination(acceleratorCode);

        getSkinnable().getParent().getScene().getAccelerators().put(acceleratorKeyCombo, firstMenuRunnable);
        engine = new TraversalEngine(this, false) {
            @Override public void trav(Node node, Direction dir) {
                direction = dir;
                super.trav(node,dir);
            }
        };
        engine.addTraverseListener(this);
        setImpl_traversalEngine(engine);
    }
    

    Runnable firstMenuRunnable = new Runnable() {
            public void run() {
                /*
                ** check that this menubar's container has contents,
                ** and that the first item is a MenuButton.... 
                ** otherwise the transfer is off!
                */
                if (container.getChildren().size() > 0) {
                    if (container.getChildren().get(0) instanceof MenuButton) {
                        container.getChildren().get(0).requestFocus();
                    }
                }
            }
        };


    private boolean pendingDismiss = false;

    private boolean menusContainCustomMenuItem() {
        for (Menu menu : getSkinnable().getMenus()) {
            if (menuContainsCustomMenuItem(menu)) {
                System.err.println("Warning: MenuBar ignored property useSystemMenuBar because menus contain CustomMenuItem");
                return true;
            }
        }
        return false;
    }

    private boolean menuContainsCustomMenuItem(Menu menu) {
        for (MenuItem mi : menu.getItems()) {
            if (mi instanceof CustomMenuItem && !(mi instanceof SeparatorMenuItem)) {
                return true;
            } else if (mi instanceof Menu) {
                if (menuContainsCustomMenuItem((Menu)mi)) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getMenuBarButtonIndex(MenuBarButton m) {
        for (int i= 0; i < container.getChildren().size(); i++) {
            MenuBarButton menuButton = (MenuBarButton)container.getChildren().get(i);
            if (m == menuButton) {
                return i;
            }
        }
        return -1;
    }
    
    private void rebuildUI() {
        for(Node n : container.getChildren()) {
            //Stop observing menu's showing & disable property for changes.
            //Need to unbind before clearing container's children.
            MenuBarButton menuButton = (MenuBarButton)n;
            menuButton.hide();
            menuButton.menu.showingProperty().removeListener(menuButton.menuListener);
            menuButton.disableProperty().unbind();
            menuButton.textProperty().unbind();
            menuButton.graphicProperty().unbind();
            menuButton.styleProperty().unbind();
        }
        container.getChildren().clear();


        if (Toolkit.getToolkit().getSystemMenu().isSupported() && getSkinnable().getScene() != null) {
            Scene scene = getSkinnable().getScene();
            if (scene.getWindow() instanceof Stage) {
                Stage stage = (Stage)scene.getWindow();
                if (getSkinnable().isUseSystemMenuBar() && !menusContainCustomMenuItem()) {
                    // Set the system menu bar if not set by another
                    // MenuBarSkin instance on this stage.
                    if (systemMenuMap == null ||
                        systemMenuMap.get(stage) == null ||
                        systemMenuMap.get(stage) == this) {

                        if (systemMenuMap == null) {
                            initSystemMenuBar();
                        }
                        if (wrappedMenus == null) {
                            wrappedMenus = new ArrayList<MenuBase>();
                            systemMenuMap.put(stage, this);
                        } else {
                            wrappedMenus.clear();
                        }
                        for (Menu menu : getSkinnable().getMenus()) {
                            wrappedMenus.add(GlobalMenuAdapter.adapt(menu));
                        }
                        currentMenuBarStage = null;
                        setSystemMenu(stage);

                        requestLayout();
                        javafx.application.Platform.runLater(new Runnable() {
                            public void run() {
                                requestLayout();
                            }
                        });
                        return;
                    }
                }
                if (systemMenuMap != null && systemMenuMap.get(stage) == this) {
                    // This MenuBar was previously installed in the
                    // system menu bar. Remove it.
                    wrappedMenus = null;
                    systemMenuMap.remove(stage);
                    currentMenuBarStage = null;
                    setSystemMenu(stage);
                }
            }
        }


        for (final Menu menu : getSkinnable().getMenus()) {
            if (!menu.isVisible()) continue;
            final MenuBarButton menuButton = new MenuBarButton(menu.getText(), menu.getGraphic());
            menuButton.setFocusTraversable(false);
            menuButton.getStyleClass().add("menu");
            menuButton.setStyle(menu.getStyle()); // copy style 

            menuButton.getItems().setAll(menu.getItems());
            container.getChildren().add(menuButton);
            // listen to changes in menu items & update menuButton items
            menu.getItems().addListener(new ListChangeListener() {
                @Override public void onChanged(Change c) {
                    while (c.next()) {
                        menuButton.getItems().removeAll(c.getRemoved());
                        menuButton.getItems().addAll(c.getFrom(), c.getAddedSubList());
                    }
                }
            });
            menu.getStyleClass().addListener(new ListChangeListener<String>() {
                @Override
                public void onChanged(Change<? extends String> c) {
                    while(c.next()) {
                        for(int i=c.getFrom(); i<c.getTo(); i++) {
                            menuButton.getStyleClass().add(menu.getStyleClass().get(i));
                        }
                        for (String str : c.getRemoved()) {
                            menuButton.getStyleClass().remove(str);
                        }
                    }
                }
            });
            menuButton.menuListener = new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                    if (menu.isShowing()) {
                        menuButton.show();
                    } else {
                        menuButton.hide();
                    }
                }

            };
            menuButton.menu = menu;
            menu.showingProperty().addListener(menuButton.menuListener);
            menuButton.disableProperty().bindBidirectional(menu.disableProperty());
            menuButton.textProperty().bind(menu.textProperty());
            menuButton.graphicProperty().bind(menu.graphicProperty());
            menuButton.styleProperty().bind(menu.styleProperty());
            menuButton.getProperties().addListener(new MapChangeListener<Object, Object>() {
                @Override
                public void onChanged(Change<? extends Object, ? extends Object> c) {
                     if (c.wasAdded() && MenuButtonSkin.AUTOHIDE.equals(c.getKey())) {
                        menuButton.getProperties().remove(MenuButtonSkin.AUTOHIDE);
                        menu.hide();
                    }
                }
            });
            menuButton.showingProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean isShowing) {
                    if (isShowing) {
                        if (openMenuButton != null && openMenuButton != menuButton) {
                            openMenuButton.hide();
                        }
                        openMenuButton = menuButton;
                        openMenu = menu;
                    }
                }
            });

            menuButton.setOnMousePressed(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent event) {
                    pendingDismiss = menuButton.isShowing();

                    // check if the owner window has focus
                    if (menuButton.getScene().getWindow().isFocused()) {
                        if (!isMenuEmpty(menu)){
                            openMenu = menu;
                            openMenu.show();
                        } else {
                            openMenu = null;
                        }
                        // update FocusedIndex
                        focusedMenuIndex = getMenuBarButtonIndex(menuButton);
                    }
                }
            });

            menuButton.setOnMouseReleased(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent event) {
                    // check if the owner window has focus
                    if (menuButton.getScene().getWindow().isFocused()) {
                        if (pendingDismiss) {
                            if (openMenu != null) openMenu.hide();
//                            menuButton.hide();
                        }
                    }
                    pendingDismiss = false;
                }
            });

//            menuButton. setOnKeyPressed(new EventHandler<javafx.scene.input.KeyEvent>() {
//                @Override public void handle(javafx.scene.input.KeyEvent ke) {
//                    switch (ke.getCode()) {
//                        case LEFT:
//                            if (menuButton.getScene().getWindow().isFocused()) {
//                                Menu prevMenu = findPreviousSibling();
//                                if (openMenu == null || ! openMenu.isShowing()) {
//                                    return;
//                                }
////                                if (focusedMenuIndex == container.getChildren().size() - 1) {
////                                   ((MenuBarButton)container.getChildren().get(focusedMenuIndex)).requestFocus();
////                                }
//                                 // hide the currently visible menu, and move to the previous one
//                                openMenu.hide();
//                                if (!isMenuEmpty(prevMenu)) {
//                                    openMenu = prevMenu;
//                                    openMenu.show();
//                                } else {
//                                    openMenu = null;
//                                }
//                            }
//                            ke.consume();
//                            break;
//                        case RIGHT:
//                            if (menuButton.getScene().getWindow().isFocused()) {
//                                Menu nextMenu = findNextSibling();
//                                if (openMenu == null || ! openMenu.isShowing()) {
//                                    return;
//                                }
////                                if (focusedMenuIndex == 0) {
////                                    ((MenuBarButton)container.getChildren().get(focusedMenuIndex)).requestFocus();
////                                }
//                                 // hide the currently visible menu, and move to the next one
//                                openMenu.hide();
//                                if (!isMenuEmpty(nextMenu)) {
//                                    openMenu = nextMenu;
//                                    openMenu.show();
//                                } else {
//                                    openMenu = null;
//                                }
//                            }
//                            ke.consume();
//                            break;
//
//                        case DOWN:
//                        case SPACE:
//                        case ENTER:
//                            if (menuButton.getScene().getWindow().isFocused()) {
//                                if (focusedMenuIndex != -1) {
//                                    if (!isMenuEmpty(getSkinnable().getMenus().get(focusedMenuIndex))) {
//                                        openMenu = getSkinnable().getMenus().get(focusedMenuIndex);
//                                        openMenu.show();
//                                    } else {
//                                        openMenu = null;
//                                    }
//                                    ke.consume();
//                                }
//                            }
//                            break;
//                    }
//                }
//            });
            menuButton.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override public void handle(MouseEvent event) {
                    // check if the owner window has focus
                    if (menuButton.getScene().getWindow().isFocused()) {
                        if (openMenu == null || ! openMenu.isShowing()) {
                            updateFocusedIndex();
                            return;
                        }
                         // hide the currently visible menu, and move to the new one
                        openMenu.hide();
    //                    openMenuButton.hide();
                        if (!isMenuEmpty(menu)) {
                            openMenu = menu;
                            openMenuButton = menuButton;
                            updateFocusedIndex();
                            openMenu.show();
                        } else {
                            openMenu = null;
                        }
    //                    openMenuButton.show();
                        }
                    }
            });
        }
        requestLayout();
    }

    private boolean isMenuEmpty(Menu menu) {
        boolean retVal = true;
        for (MenuItem m : menu.getItems()) {
            if (m.isVisible()) retVal = false;
        }
        return retVal;
    }

    private boolean isAnyMenuSelected() {
        if (container != null) {
            for(Node n : container.getChildren()) {
                if (((MenuButton)n).isFocused()) return true;
            }
        }
        return false;
    }
    private Menu findPreviousSibling() {
        if (focusedMenuIndex == -1) return null;
        if (focusedMenuIndex == 0) {
            focusedMenuIndex = container.getChildren().size() - 1;
        } else {
            focusedMenuIndex--;
        }
        clearMenuButtonHover();
        return getSkinnable().getMenus().get(focusedMenuIndex);
    }

    private Menu findNextSibling() {
        if (focusedMenuIndex == -1) return null;
        if (focusedMenuIndex == container.getChildren().size() - 1) {
            focusedMenuIndex = 0;
        } else {
            focusedMenuIndex++;
        }
        clearMenuButtonHover();
        return getSkinnable().getMenus().get(focusedMenuIndex);
    }

    private void updateFocusedIndex() {
        int index = 0;
        for(Node n : container.getChildren()) {
            if (n.isHover()) {
                focusedMenuIndex = index;
                return;
            }
            index++;
        }
    }

    private void clearMenuButtonHover() {
         for(Node n : container.getChildren()) {
            if (n.isHover()) {
                ((MenuBarButton)n).clearHover();
                return;
            }
        }
    }

    @Override
    public void onTraverse(Node node, Bounds bounds) {
        if (direction.equals(Direction.NEXT)) {
            if (openMenu != null) openMenu.hide();
            focusedMenuIndex = 0;
            new TraversalEngine(getSkinnable(), false).trav(getSkinnable(), Direction.NEXT);
        } else if (direction.equals(DOWN)) {
            // do nothing 
        }
    }

    class MenuBarButton extends MenuButton {
        private ChangeListener<Boolean> menuListener;
        private Menu menu;

        public MenuBarButton() {
            super();
        }

        public MenuBarButton(String text) {
            super(text);
        }

        public MenuBarButton(String text, Node graphic) {
            super(text, graphic);
        }

        private void clearHover() {
            setHover(false);
        }
    }

    /***************************************************************************
     *                                                                         *
     * Layout                                                                  *
     *                                                                         *
     **************************************************************************/

    // Return empty insets when "container" is empty, which happens
    // when using the system menu bar.
    @Override public Insets getInsets() {
        if (container.getChildren().size() == 0) {
            return new Insets(0, 0, 0, 0);
        } else {
            return super.getInsets();
        }
    }

    /**
     * Layout the menu bar. This is a simple horizontal layout like an hbox.
     * Any menu items which don't fit into it will simply be made invisible.
     */
    @Override protected void layoutChildren() {
        // layout the menus one after another
        double x = getInsets().getLeft();
        double y = getInsets().getTop();
        double w = getWidth() - (getInsets().getLeft() + getInsets().getRight());
        double h = getHeight() - (getInsets().getTop() + getInsets().getBottom());
        
        container.resizeRelocate(x, y, w, h);
    }

    @Override protected double computeMinWidth(double height) {
        return container.minWidth(height) + getInsets().getLeft() + getInsets().getRight();
    }

    @Override protected double computePrefWidth(double height) {
        return container.prefWidth(height) + getInsets().getLeft() + getInsets().getRight();
    }

    @Override protected double computeMinHeight(double width) {
        return container.minHeight(width) + getInsets().getTop() + getInsets().getBottom();
    }

    @Override protected double computePrefHeight(double width) {
        return container.prefHeight(width) + getInsets().getTop() + getInsets().getBottom();
    }

    // grow horizontally, but not vertically
    @Override protected double computeMaxHeight(double width) {
        return getSkinnable().prefHeight(-1);
    }
}
