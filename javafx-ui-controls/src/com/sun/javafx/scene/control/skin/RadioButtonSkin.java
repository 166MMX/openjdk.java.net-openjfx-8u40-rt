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

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.StackPane;

import com.sun.javafx.scene.control.behavior.ButtonBehavior;

public class RadioButtonSkin extends LabeledSkinBase<RadioButton, ButtonBehavior<RadioButton>> {

    /** The radio contains the "dot", which is usually a circle */
    private StackPane radio;

    /**
     * Used for laying out the label + radio together as a group
     *
     * NOTE: This extra node should be eliminated in the future.
     * Instead, position inner nodes directly with the utility
     * functions in Pane (computeXOffset()/computeYOffset()).
     */
    public RadioButtonSkin(RadioButton radioButton) {
        super(radioButton, new ButtonBehavior<RadioButton>(radioButton));

        radio = createRadio();        
        updateChildren();
    }

    @Override protected void updateChildren() {
        super.updateChildren();
        getChildren().add(radio);
    }

    private static StackPane createRadio() {
        StackPane radio = new StackPane();
        radio.getStyleClass().setAll("radio");
        radio.setSnapToPixel(false);
        StackPane region = new StackPane();
        region.getStyleClass().setAll("dot");
        radio.getChildren().clear();
        radio.getChildren().addAll(region);
        return radio;
    }


    /***************************************************************************
     *                                                                         *
     * Layout                                                                  *
     *                                                                         *
     **************************************************************************/

    @Override protected double computePrefWidth(double height) {
        return super.computePrefWidth(height) + snapSize(radio.prefWidth(height));
    }

    @Override protected double computePrefHeight(double width) {
        return Math.max(snapSize(super.computePrefHeight(width)),
                        getInsets().getTop() + radio.prefHeight(width) + getInsets().getBottom());
    }

    @Override protected void layoutChildren() {
        Insets padding = getInsets();

        final double w = getWidth() - padding.getLeft() - padding.getRight();
        final double h = getHeight() - padding.getTop() - padding.getBottom();
        final double radioWidth = radio.prefWidth(-1);
        final double radioHeight = radio.prefHeight(-1);
        final double labelWidth = Math.min(prefWidth(-1) - radioWidth, w - snapSize(radioWidth));
        final double labelHeight = Math.min(prefHeight(labelWidth), h);
        final double maxHeight = Math.max(radioHeight, labelHeight);
        final double x = Utils.computeXOffset(w, labelWidth + radioWidth, getSkinnable().getAlignment().getHpos()) + padding.getLeft();
        final double y = Utils.computeYOffset(h, maxHeight, getSkinnable().getAlignment().getVpos()) + padding.getTop();

        layoutLabelInArea(x + radioWidth, y, labelWidth, maxHeight, Pos.CENTER_LEFT);
        radio.resize(snapSize(radioWidth), snapSize(radioHeight));
        positionInArea(radio, x, y, radioWidth, maxHeight, getBaselineOffset(), HPos.CENTER, VPos.CENTER);
    }
}
