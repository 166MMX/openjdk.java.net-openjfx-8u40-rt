/*
 * Copyright (c) 2009, 2012, Oracle and/or its affiliates. All rights reserved.
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
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package com.sun.javafx.tk;

import javafx.scene.input.TransferMode;

/**
 * Listens for drop target events on a TKScene.
 * Event objects are passed transparently as java.lang.Object and are expected
 * to be converted to {@link javafx.scene.transfer.DragEvent}s by the implementation
 * of this interface through the use of
 * {@link Toolkit#convertDropTargetEventToFX(Object):javafx.scene.transfer.DragEvent}
 * as necessary.
 * <br>
 * The implementation of this interface uses the event objects' pointer position
 * to determine if any nodes in the scene graph are to be notified
 * of drag gesture events.
 */
public interface TKDropTargetListener {
    /**
     * Called during a drag gesture, when the mouse pointer enters
     * the operable part of the Scene.
     *
     * @param dropTargetDragEvent a drop target drag event
     * @return transfer mode chosen by potential target
     */

    public TransferMode dragEnter(Object dropTargetDragEvent);

    /**
     * Called during a drag gesture, while the mouse pointer is still
     * over (is moving over) the operable part of the Sceme.
     *
     * @param dropTargetDragEvent a drop target drag event
     * @return transfer mode chosen by potential target
     */
    public TransferMode dragOver(Object dropTargetDragEvent);

    /**
     * Called if the user has modified the intended 
     * {@link javafx.scene.input.TransferMode} of the drag gesture.
     * This usually happens as a result of different keys being pressed/released
     * during the gesture (alt, shift, ctrl, etc.)
     *
     * @param dropTargetDragEvent a drop target drag event
     */
    public void dropActionChanged(Object dropTargetDragEvent);

    /**
     * Called during a drag gesture, when the mouse pointer exits
     * the operable part of the Scene.
     *
     * @param dropTargetDragEvent a drop target drag event
     */
    public void dragExit(Object dropTargetDragEvent);

    /**
     * Called when the drag gesture is being terminated with a drop on
     * the operable part Scene.
     * <br>
     * This method is responsible for undertaking
     * the transfer of the data associated with the
     * gesture. The <code>dropTargetDropEvent</code>
     * provides a means to obtain an
     * object that represents the data object(s) to
     * be transfered.
     * <br>
     * From this method, the implemenation needs to
     * allow {javafx.scene.transfer.DragEvent#accept()} or
     * {javafx.scene.transfer.DragEvent#reject()} to be called from a drop
     * target node in the scene graph.
     * <br>
     * Subsequent to {javafx.scene.transfer.DragEvent#accept()}, the target
     * node should retrieve the drag gesture's data via the
     * {javafx.scene.transfer.DragEvent#dragboard}
     * <br>
     * At the completion of a drop, the target node is required to call
     * {javafx.scene.transfer.DragEvent#dropComplete(boolean))}.
     * <br>
     * Note: The data transfer should be completed before the call to
     * {javafx.scene.transfer.DragEvent#dropComplete(boolean))}.
     * After that, the transfer of data can only succeed if the data transfer is
     * local (within the same VM hosting the running JavaFX code).
     * <br>
     * @param dropTargetDropEvent a drop target drop event
     * @return transfer mode chosen by target
     */
    public TransferMode drop(Object dropTargetDropEvent);
}
