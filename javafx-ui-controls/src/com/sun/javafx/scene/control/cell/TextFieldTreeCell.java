/*
 * Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved.
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
package com.sun.javafx.scene.control.cell;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.DefaultStringConverter;

/**
 * A class containing a {@link TreeCell} implementation that draws a 
 * {@link TextField} node inside the cell.
 * 
 * <p>By default, the TextFieldTreeCell is rendered as a {@link Label} when not 
 * being edited, and as a TextField when in editing mode. The TextField will, by 
 * default, stretch to fill the entire tree cell.
 * 
 * @param <T> The type of the elements contained within the ListView.
 */
public class TextFieldTreeCell<T> extends TreeCell<T> {
    
    /***************************************************************************
     *                                                                         *
     * Static cell factories                                                   *
     *                                                                         *
     **************************************************************************/
    
    /**
     * Provides a {@link TextField} that allows editing of the cell content when 
     * the cell is double-clicked, or when {@link ListView#edit(int)} is called. 
     * This method will only work on {@link TreeView} instances which are of 
     * type String.
     * 
     * @return A {@link Callback} that can be inserted into the 
     *      {@link TreeView#cellFactoryProperty() cell factory property} of a 
     *      TreeView, that enables textual editing of the content.
     */
    public static Callback<TreeView<String>, TreeCell<String>> forTreeView() {
        return forTreeView(new DefaultStringConverter());
    }
    
    /**
     * Provides a {@link TextField} that allows editing of the cell content when 
     * the cell is double-clicked, or when 
     * {@link TreeView#edit(javafx.scene.control.TreeItem)} is called. This 
     * method will work on any {@link TreeView} instance, 
     * regardless of its generic type. However, to enable this, a 
     * {@link StringConverter} must be provided that will convert the given String 
     * (from what the user typed in) into an instance of type T. This item will 
     * then be passed along to the {@link TreeView#onEditCommitProperty()} 
     * callback.
     * 
     * @param converter A {@link StringConverter} that can convert the given String 
     *      (from what the user typed in) into an instance of type T.
     * @return A {@link Callback} that can be inserted into the 
     *      {@link TreeView#cellFactoryProperty() cell factory property} of a 
     *      TreeView, that enables textual editing of the content.
     */
    public static <T> Callback<TreeView<T>, TreeCell<T>> forTreeView(
            final StringConverter<T> converter) {
        return new Callback<TreeView<T>, TreeCell<T>>() {
            @Override public TreeCell<T> call(TreeView<T> list) {
                return new TextFieldTreeCell<T>(converter);
            }
        };
    }
    
    
    
    /***************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/
    
    private TextField textField;
    
    
    
    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/
    
    /**
     * Creates a default TextFieldTreeCell with a null converter.
     */
    public TextFieldTreeCell() { 
        this(null);
    } 
    
    /**
     * Creates a TextFieldTreeCell that provides a {@link TextField} when put 
     * into editing mode that allows editing of the cell content. This method 
     * will work on any TreeView instance, regardless of its generic type. 
     * However, to enable this, a {@link StringConverter} must be provided that 
     * will convert the given String (from what the user typed in) into an 
     * instance of type T. This item will then be passed along to the 
     * {@link TreeView#onEditCommitProperty()} callback.
     * 
     * @param onCommit A {@link StringConverter<T> converter} that can convert 
     *      the given String (from what the user typed in) into an instance of 
     *      type T.
     */
    public TextFieldTreeCell(StringConverter<T> converter) {
        this.getStyleClass().add("text-field-tree-cell");
        setConverter(converter);
    }
    
    
    
    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/
    
    // --- converter
    private ObjectProperty<StringConverter<T>> converter = 
            new SimpleObjectProperty<StringConverter<T>>(this, "converter");

    /**
     * The {@link StringConverter} property.
     */
    public final ObjectProperty<StringConverter<T>> converterProperty() { 
        return converter; 
    }
    
    /** 
     * Sets the {@link StringConverter} to be used in this cell.
     */
    public final void setConverter(StringConverter<T> value) { 
        converterProperty().set(value); 
    }
    
    /**
     * Returns the {@link StringConverter} used in this cell.
     */
    public final StringConverter<T> getConverter() { 
        return converterProperty().get(); 
    }  
    
    
    
    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/    
    
    /** {@inheritDoc} */
    @Override public void startEdit() {
        if (! isEditable() || ! getTreeView().isEditable()) {
            return;
        }
        super.startEdit();
        CellUtils.startEdit(this, textField, getConverter());
    }

    /** {@inheritDoc} */
    @Override public void cancelEdit() {
        super.cancelEdit();
        CellUtils.cancelEdit(this, getConverter());
    }
    
    /** {@inheritDoc} */
    @Override public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        CellUtils.updateItem(this, textField, getConverter());
    }
}