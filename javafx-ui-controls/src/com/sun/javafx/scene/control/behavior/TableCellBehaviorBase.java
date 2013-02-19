/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
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
package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.PlatformUtil;
import java.util.WeakHashMap;
import javafx.scene.control.Control;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableFocusModel;
import javafx.scene.control.TablePositionBase;
import javafx.scene.control.TableSelectionModel;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 */
public abstract class TableCellBehaviorBase<T extends IndexedCell> extends CellBehaviorBase<T> {
    
    /***************************************************************************
     *                                                                         *
     * Private static implementation                                           *
     *                                                                         *
     **************************************************************************/
    
    // global map used to store the focus cell for a table view when it is first
    // shift-clicked. This allows for proper keyboard interactions, in particular
    // resolving RT-11446
    private static final WeakHashMap<Control, TablePositionBase> map = 
            new WeakHashMap<Control, TablePositionBase>();
    
    static TablePositionBase getAnchor(Control table, TablePositionBase focusedCell) {
        return hasAnchor(table) ? map.get(table) : focusedCell;
    }
    
    static void setAnchor(Control table, TablePositionBase anchor) {
        if (table != null && anchor == null) {
            map.remove(table);
        } else {
            map.put(table, anchor);
        }
    }
    
    static boolean hasAnchor(Control table) {
        return map.containsKey(table) && map.get(table) != null;
    }
    
    
    
    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/      
    
    // For RT-17456: have selection occur as fast as possible with mouse input.
    // The idea is (consistently with some native applications we've tested) to 
    // do the action as soon as you can. It takes a bit more coding but provides
    // the best feel:
    //  - when you click on a not-selected item, you can select immediately on press
    //  - when you click on a selected item, you need to wait whether DragDetected or Release comes first 
    // To support touch devices, we have to slightly modify this behavior, such
    // that selection only happens on mouse release, if only minimal dragging
    // has occurred.
    private boolean latePress = false;
    private final boolean isEmbedded = PlatformUtil.isEmbedded();
    private boolean wasSelected = false;
    
    

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/    

    public TableCellBehaviorBase(T control) {
        super(control);
    }
    
    
    
    /**************************************************************************
     *                                                                        *
     * Abstract API                                                           *
     *                                                                        *  
     *************************************************************************/  
    
    abstract Control getTableControl(); // tableCell.getTreeTableView()
    abstract TableColumnBase getTableColumn(); // getControl().getTableColumn()
    abstract int getItemCount();        // tableView.impl_getTreeItemCount()
    abstract TableSelectionModel getSelectionModel();
    abstract TableFocusModel getFocusModel();
    abstract TablePositionBase getFocusedCell();
    abstract boolean isTableRowSelected(); // tableCell.getTreeTableRow().isSelected()
    abstract TableColumnBase getVisibleLeafColumn(int index);
    
    /**
     * Returns the position of the given table column in the visible leaf columns
     * list of the underlying control.
     */
    protected abstract int getVisibleLeafIndex(TableColumnBase tc);
    
    abstract void focus(int row, TableColumnBase tc); //fm.focus(new TreeTablePosition(tableView, row, tableColumn));
    abstract void edit(int row, TableColumnBase tc); 
    
    
    
    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/    
    
    @Override public void mousePressed(MouseEvent event) {
        boolean selectedBefore = getControl().isSelected();
        
        if (getControl().isSelected()) {
            latePress = true;
            return;
        }

        doSelect(event);
        
        if (isEmbedded && selectedBefore) {
            wasSelected = getControl().isSelected();
        }
    }
    
    @Override public void mouseReleased(MouseEvent event) {
        if (latePress) {
            latePress = false;
            doSelect(event);
        }
        
        wasSelected = false;
    }
    
    @Override public void mouseDragged(MouseEvent event) {
        latePress = false;
        
        // the mouse has now been dragged on a touch device, we should
        // remove the selection if we just added it in the last mouse press
        // event
        if (isEmbedded && ! wasSelected && getControl().isSelected()) {
            getSelectionModel().clearSelection(getControl().getIndex());
        }
    }
    
    
    
    /***************************************************************************
     *                                                                         *
     * Private implementation                                                  *
     *                                                                         *
     **************************************************************************/   
    
    private void doSelect(MouseEvent e) {
        // Note that table.select will reset selection
        // for out of bounds indexes. So, need to check
        final IndexedCell tableCell = getControl();

        // If the mouse event is not contained within this tableCell, then
        // we don't want to react to it.
        if (! tableCell.contains(e.getX(), e.getY())) return;

        final Control tableView = getTableControl();
        if (tableView == null) return;
        
        int count = getItemCount();
        if (tableCell.getIndex() >= count) return;

        TableSelectionModel sm = getSelectionModel();
        if (sm == null) return;

        final boolean selected = ! sm.isCellSelectionEnabled() ? isTableRowSelected() : tableCell.isSelected();
        final int row = tableCell.getIndex();
        final int column = getColumn();
        final TableColumnBase<?,?> tableColumn = getTableColumn();

        TableFocusModel fm = getFocusModel();
        if (fm == null) return;
        
        TablePositionBase focusedCell = getFocusedCell();
        
        // if shift is down, and we don't already have the initial focus index
        // recorded, we record the focus index now so that subsequent shift+clicks
        // result in the correct selection occuring (whilst the focus index moves
        // about).
        if (e.isShiftDown()) {
            if (! map.containsKey(tableView)) {
                setAnchor(tableView, focusedCell);
            }
        } else {
            map.remove(tableView);
        }

        // we must update the table appropriately, and this is determined by
        // what modifiers the user held down as they released the mouse.
        MouseButton button = e.getButton();
        if (button == MouseButton.PRIMARY || (button == MouseButton.SECONDARY && !selected)) { 
            if (sm.getSelectionMode() == SelectionMode.SINGLE) {
                simpleSelect(e);
            } else {
                if (e.isControlDown() || e.isMetaDown()) {
                    if (selected) {
                        // we remove this row/cell from the current selection
                        sm.clearSelection(row, tableColumn);
                        fm.focus(row, tableColumn);
                    } else {
                        // We add this cell/row to the current selection
                        sm.select(row, tableColumn);
                    }
                } else if (e.isShiftDown()) {
                    // we add all cells/rows between the current selection focus and
                    // this cell/row (inclusive) to the current selection.
                    focusedCell = map.containsKey(tableView) ? map.get(tableView) : focusedCell;

                    // and then determine all row and columns which must be selected
                    int minRow = Math.min(focusedCell.getRow(), row);
                    int maxRow = Math.max(focusedCell.getRow(), row);
                    int minColumn = Math.min(focusedCell.getColumn(), column);
                    int maxColumn = Math.max(focusedCell.getColumn(), column);

                    // clear selection
                    sm.clearSelection();

                    // and then perform the selection
                    if (sm.isCellSelectionEnabled()) {
                        for (int _row = minRow; _row <= maxRow; _row++) {
                            for (int _col = minColumn; _col <= maxColumn; _col++) {
                                sm.select(_row, getVisibleLeafColumn(_col));
                            }
                        }
                    } else {
                        sm.selectRange(minRow, maxRow + 1);
                    }

                    // return selection back to the focus owner
                    focus(row, tableColumn);
                } else {
                    simpleSelect(e);
                }
            }
        }
    }

    protected void simpleSelect(MouseEvent e) {
        TableSelectionModel sm = getSelectionModel();
        int row = getControl().getIndex();
        TableColumnBase column = getTableColumn();
        boolean isAlreadySelected = sm.isSelected(row, column);

        sm.clearAndSelect(row, column);

        // handle editing, which only occurs with the primary mouse button
        if (e.getButton() == MouseButton.PRIMARY) {
            if (e.getClickCount() == 1 && isAlreadySelected) {
                edit(row, column);
            } else if (e.getClickCount() == 1) {
                // cancel editing
                edit(-1, null);
            } else if (e.getClickCount() == 2 && getControl().isEditable()) {
                // edit at the specified row and column
                edit(row, column);
            }
        }
    }

    private int getColumn() {
        if (getSelectionModel().isCellSelectionEnabled()) {
            TableColumnBase tc = getTableColumn();
            return getVisibleLeafIndex(tc);
        }

        return -1;
    }
}
