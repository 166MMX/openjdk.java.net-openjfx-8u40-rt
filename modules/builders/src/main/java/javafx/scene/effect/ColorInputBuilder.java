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

package javafx.scene.effect;

/**
Builder class for javafx.scene.effect.ColorInput
@see javafx.scene.effect.ColorInput
@deprecated This class is deprecated and will be removed in the next version
* @since JavaFX 2.0
*/
@javax.annotation.Generated("Generated by javafx.builder.processor.BuilderProcessor")
@Deprecated
public class ColorInputBuilder<B extends javafx.scene.effect.ColorInputBuilder<B>> implements javafx.util.Builder<javafx.scene.effect.ColorInput> {
    protected ColorInputBuilder() {
    }
    
    /** Creates a new instance of ColorInputBuilder. */
    @SuppressWarnings({"deprecation", "rawtypes", "unchecked"})
    public static javafx.scene.effect.ColorInputBuilder<?> create() {
        return new javafx.scene.effect.ColorInputBuilder();
    }
    
    private int __set;
    public void applyTo(javafx.scene.effect.ColorInput x) {
        int set = __set;
        if ((set & (1 << 0)) != 0) x.setHeight(this.height);
        if ((set & (1 << 1)) != 0) x.setPaint(this.paint);
        if ((set & (1 << 2)) != 0) x.setWidth(this.width);
        if ((set & (1 << 3)) != 0) x.setX(this.x);
        if ((set & (1 << 4)) != 0) x.setY(this.y);
    }
    
    private double height;
    /**
    Set the value of the {@link javafx.scene.effect.ColorInput#getHeight() height} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B height(double x) {
        this.height = x;
        __set |= 1 << 0;
        return (B) this;
    }
    
    private javafx.scene.paint.Paint paint;
    /**
    Set the value of the {@link javafx.scene.effect.ColorInput#getPaint() paint} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B paint(javafx.scene.paint.Paint x) {
        this.paint = x;
        __set |= 1 << 1;
        return (B) this;
    }
    
    private double width;
    /**
    Set the value of the {@link javafx.scene.effect.ColorInput#getWidth() width} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B width(double x) {
        this.width = x;
        __set |= 1 << 2;
        return (B) this;
    }
    
    private double x;
    /**
    Set the value of the {@link javafx.scene.effect.ColorInput#getX() x} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B x(double x) {
        this.x = x;
        __set |= 1 << 3;
        return (B) this;
    }
    
    private double y;
    /**
    Set the value of the {@link javafx.scene.effect.ColorInput#getY() y} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B y(double x) {
        this.y = x;
        __set |= 1 << 4;
        return (B) this;
    }
    
    /**
    Make an instance of {@link javafx.scene.effect.ColorInput} based on the properties set on this builder.
    */
    public javafx.scene.effect.ColorInput build() {
        javafx.scene.effect.ColorInput x = new javafx.scene.effect.ColorInput();
        applyTo(x);
        return x;
    }
}
