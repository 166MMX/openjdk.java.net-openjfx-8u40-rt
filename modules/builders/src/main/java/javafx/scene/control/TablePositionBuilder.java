/* 
 * Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
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

package javafx.scene.control;

/**
Builder class for javafx.scene.control.TablePosition
@see javafx.scene.control.TablePosition
@deprecated This class is deprecated and will be removed in the next version
* @since JavaFX 2.0
*/
@javax.annotation.Generated("Generated by javafx.builder.processor.BuilderProcessor")
@Deprecated
public class TablePositionBuilder<S, T, B extends javafx.scene.control.TablePositionBuilder<S, T, B>> implements javafx.util.Builder<javafx.scene.control.TablePosition<S, T>> {
    protected TablePositionBuilder() {
    }
    
    /** Creates a new instance of TablePositionBuilder. */
    @SuppressWarnings({"deprecation", "rawtypes", "unchecked"})
    public static <S, T> javafx.scene.control.TablePositionBuilder<S, T, ?> create() {
        return new javafx.scene.control.TablePositionBuilder();
    }
    
    private int row;
    /**
    Set the value of the {@link javafx.scene.control.TablePosition#getRow() row} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B row(int x) {
        this.row = x;
        return (B) this;
    }
    
    private javafx.scene.control.TableColumn<S,T> tableColumn;
    /**
    Set the value of the {@link javafx.scene.control.TablePosition#getTableColumn() tableColumn} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B tableColumn(javafx.scene.control.TableColumn<S,T> x) {
        this.tableColumn = x;
        return (B) this;
    }
    
    private javafx.scene.control.TableView<S> tableView;
    /**
    Set the value of the {@link javafx.scene.control.TablePosition#getTableView() tableView} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B tableView(javafx.scene.control.TableView<S> x) {
        this.tableView = x;
        return (B) this;
    }
    
    /**
    Make an instance of {@link javafx.scene.control.TablePosition} based on the properties set on this builder.
    */
    public javafx.scene.control.TablePosition<S, T> build() {
        javafx.scene.control.TablePosition<S, T> x = new javafx.scene.control.TablePosition<S, T>(this.tableView, this.row, this.tableColumn);
        return x;
    }
}
