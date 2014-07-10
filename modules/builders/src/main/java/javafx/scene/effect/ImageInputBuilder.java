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
Builder class for javafx.scene.effect.ImageInput
@see javafx.scene.effect.ImageInput
@deprecated This class is deprecated and will be removed in the next version
* @since JavaFX 2.0
*/
@javax.annotation.Generated("Generated by javafx.builder.processor.BuilderProcessor")
@Deprecated
public class ImageInputBuilder<B extends javafx.scene.effect.ImageInputBuilder<B>> implements javafx.util.Builder<javafx.scene.effect.ImageInput> {
    protected ImageInputBuilder() {
    }
    
    /** Creates a new instance of ImageInputBuilder. */
    @SuppressWarnings({"deprecation", "rawtypes", "unchecked"})
    public static javafx.scene.effect.ImageInputBuilder<?> create() {
        return new javafx.scene.effect.ImageInputBuilder();
    }
    
    private int __set;
    public void applyTo(javafx.scene.effect.ImageInput x) {
        int set = __set;
        if ((set & (1 << 0)) != 0) x.setSource(this.source);
        if ((set & (1 << 1)) != 0) x.setX(this.x);
        if ((set & (1 << 2)) != 0) x.setY(this.y);
    }
    
    private javafx.scene.image.Image source;
    /**
    Set the value of the {@link javafx.scene.effect.ImageInput#getSource() source} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B source(javafx.scene.image.Image x) {
        this.source = x;
        __set |= 1 << 0;
        return (B) this;
    }
    
    private double x;
    /**
    Set the value of the {@link javafx.scene.effect.ImageInput#getX() x} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B x(double x) {
        this.x = x;
        __set |= 1 << 1;
        return (B) this;
    }
    
    private double y;
    /**
    Set the value of the {@link javafx.scene.effect.ImageInput#getY() y} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B y(double x) {
        this.y = x;
        __set |= 1 << 2;
        return (B) this;
    }
    
    /**
    Make an instance of {@link javafx.scene.effect.ImageInput} based on the properties set on this builder.
    */
    public javafx.scene.effect.ImageInput build() {
        javafx.scene.effect.ImageInput x = new javafx.scene.effect.ImageInput();
        applyTo(x);
        return x;
    }
}
