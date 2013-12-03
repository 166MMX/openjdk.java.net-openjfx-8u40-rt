/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.oracle.javafx.scenebuilder.app;

import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.SplitPane.Divider;

/**
 *
 */
class SplitController {
    
    public enum Target { FIRST, LAST };
    
    private final SplitPane splitPane;
    private final Target target;
    private final Node targetNode;
    private double dividerPosition = -1.0;
    
    public SplitController(SplitPane splitPane, Target target) {
        assert splitPane != null;
        assert splitPane.getItems().size() >= 1;
        
        this.splitPane = splitPane;
        this.target = target;
        
        final List<Node> children = splitPane.getItems();
        final int targetIndex = (target == Target.FIRST) ? 0 : children.size()-1;
        this.targetNode = children.get(targetIndex);
    }
    
    public void showTarget() {
        if (isTargetVisible() == false) {
            // Put the target node back in the split pane items
            if (target == Target.FIRST) {
                splitPane.getItems().add(0, targetNode);
            } else {
                splitPane.getItems().add(targetNode);
            }

            // Restore the divider position (if any)
            final List<Divider> dividers = splitPane.getDividers();
            if ((dividers.isEmpty() == false) && (dividerPosition != -1)) {
                if (target == Target.FIRST) {
                    dividers.get(0).setPosition(dividerPosition);
                } else {
                    dividers.get(dividers.size()-1).setPosition(dividerPosition);
                }
            }
            dividerPosition = -1;
        }
    }
    
    public void hideTarget() {
        if (isTargetVisible()) {
            // Backup the divider position
            final List<Divider> dividers = splitPane.getDividers();
            if (dividers.isEmpty() == false) {
                if (target == Target.FIRST) {
                    dividerPosition = dividers.get(0).getPosition();
                } else {
                    dividerPosition = dividers.get(dividers.size()-1).getPosition();
                }
            }

            // Removes the target node from the split pane items
            splitPane.getItems().remove(targetNode);
        }
    }
    
    public void toggleTarget() {
        if (isTargetVisible()) {
            hideTarget();
        } else {
            showTarget();
        }
    }
    
    public boolean isTargetVisible() {
        return splitPane.getItems().contains(targetNode);
    }
}
