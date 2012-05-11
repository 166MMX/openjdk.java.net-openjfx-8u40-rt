/*
 * Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.javafx.css;

import javafx.scene.paint.Paint;



final public class BorderPaint {
    private javafx.beans.property.ObjectProperty<Paint> top;

    public final Paint getTop() {
        return top == null ? null : top.get();
    }
    private javafx.beans.property.ObjectProperty<Paint> right;

    public final Paint getRight() {
        return right == null ? null : right.get();
    }
    private javafx.beans.property.ObjectProperty<Paint> bottom;

    public final Paint getBottom() {
        return bottom == null ? null : bottom.get();
    }
    private javafx.beans.property.ObjectProperty<Paint> left;

    public final Paint getLeft() {
        return left == null ? null : left.get();
    }

    public BorderPaint(final Paint top, final Paint right,
                       final Paint bottom, final Paint left) {
        this.top.set(top);
        this.right.set(right);
        this.bottom.set(bottom);
        this.left.set(left);
    }

    @Override public String toString() {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("top: ");
        sbuf.append(getTop());
        sbuf.append(", right: ");
        sbuf.append(getRight());
        sbuf.append(", bottom: ");
        sbuf.append(getBottom());
        sbuf.append(", left: ");
        sbuf.append(getLeft());
        return sbuf.toString();
    }
}

