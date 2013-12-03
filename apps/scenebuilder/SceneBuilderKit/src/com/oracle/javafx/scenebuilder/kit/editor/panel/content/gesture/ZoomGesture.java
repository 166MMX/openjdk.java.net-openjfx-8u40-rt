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
package com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture;

import com.oracle.javafx.scenebuilder.kit.editor.panel.content.ContentPanelController;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.InputEvent;
import javafx.scene.input.ZoomEvent;

/**
 *
 * 
 */
public class ZoomGesture extends AbstractGesture {
    
    private Observer observer;

    public ZoomGesture(ContentPanelController contentPanelController) {
        super(contentPanelController);
    }

    /*
     * AbstractGesture
     */
    
    @Override
    public void start(InputEvent e, Observer observer) {
        
        assert e != null;
        assert e.getEventType() == ZoomEvent.ZOOM_STARTED;
        assert observer != null;
        
        this.observer = observer;
        
        final Node glassLayer = contentPanelController.getGlassLayer();
        assert glassLayer.getOnZoom() == null;
        assert glassLayer.getOnZoomFinished() == null;
        
        glassLayer.setOnZoom(new EventHandler<ZoomEvent>() {
            @Override
            public void handle(ZoomEvent e) {
                updateContentPanelScaling(e);
            }
        });
        glassLayer.setOnZoomFinished(new EventHandler<ZoomEvent>() {
            @Override
            public void handle(ZoomEvent e) {
                performTermination();
            }
        });
    }
    
    
    /*
     * Private
     */
    
    private void updateContentPanelScaling(ZoomEvent e) {
        assert Double.isNaN(e.getZoomFactor()) == false;
        final double scaling = contentPanelController.getScaling();
        contentPanelController.setScaling(Math.min(5, scaling * e.getZoomFactor()));
    }
    
    private void performTermination() {
        final Node glassLayer = contentPanelController.getGlassLayer();
        glassLayer.setOnZoom(null);
        glassLayer.setOnZoomFinished(null);
        
        observer.gestureDidTerminate(this);
        observer = null;
    }
}
