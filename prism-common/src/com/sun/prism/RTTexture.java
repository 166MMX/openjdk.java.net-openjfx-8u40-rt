/*
 * Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.prism;

import java.nio.Buffer;

public interface RTTexture extends Texture, RenderTarget {
    public int[] getPixels();
    public boolean readPixels(Buffer pixels);
    public boolean readPixels(Buffer pixels, int x, int y, int width, int height);
    public boolean isVolatile();

    /**
     * Called by code wanting to know if the RTTexture's surface is lost. This happens
     * in some cases (mostly on Windows) when, for example, the user presses Ctrl+Alt+Delete,
     * or the system goes to sleep.
     * @return True if the backing surface of this RTTexture is gone and the image is therefore
     *         no longer usable. False if it is still OK.
     */
    public boolean isSurfaceLost();
}
