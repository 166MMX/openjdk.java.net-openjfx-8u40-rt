/* 
 * Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
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

package javafx.scene.media;

/**
Builder class for javafx.scene.media.MediaView
@see javafx.scene.media.MediaView
@deprecated This class is deprecated and will be removed in the next version
*/
@javax.annotation.Generated("Generated by javafx.builder.processor.BuilderProcessor")
@Deprecated
public class MediaViewBuilder<B extends javafx.scene.media.MediaViewBuilder<B>> extends javafx.scene.NodeBuilder<B> implements javafx.util.Builder<javafx.scene.media.MediaView> {
    protected MediaViewBuilder() {
    }
    
    /** Creates a new instance of MediaViewBuilder. */
    @SuppressWarnings({"deprecation", "rawtypes", "unchecked"})
    public static javafx.scene.media.MediaViewBuilder<?> create() {
        return new javafx.scene.media.MediaViewBuilder();
    }
    
    private int __set;
    private void __set(int i) {
        __set |= 1 << i;
    }
    public void applyTo(javafx.scene.media.MediaView x) {
        super.applyTo(x);
        int set = __set;
        while (set != 0) {
            int i = Integer.numberOfTrailingZeros(set);
            set &= ~(1 << i);
            switch (i) {
                case 0: x.setFitHeight(this.fitHeight); break;
                case 1: x.setFitWidth(this.fitWidth); break;
                case 2: x.setMediaPlayer(this.mediaPlayer); break;
                case 3: x.setOnError(this.onError); break;
                case 4: x.setPreserveRatio(this.preserveRatio); break;
                case 5: x.setSmooth(this.smooth); break;
                case 6: x.setViewport(this.viewport); break;
                case 7: x.setX(this.x); break;
                case 8: x.setY(this.y); break;
            }
        }
    }
    
    private double fitHeight;
    /**
    Set the value of the {@link javafx.scene.media.MediaView#getFitHeight() fitHeight} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B fitHeight(double x) {
        this.fitHeight = x;
        __set(0);
        return (B) this;
    }
    
    private double fitWidth;
    /**
    Set the value of the {@link javafx.scene.media.MediaView#getFitWidth() fitWidth} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B fitWidth(double x) {
        this.fitWidth = x;
        __set(1);
        return (B) this;
    }
    
    private javafx.scene.media.MediaPlayer mediaPlayer;
    /**
    Set the value of the {@link javafx.scene.media.MediaView#getMediaPlayer() mediaPlayer} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B mediaPlayer(javafx.scene.media.MediaPlayer x) {
        this.mediaPlayer = x;
        __set(2);
        return (B) this;
    }
    
    private javafx.event.EventHandler<javafx.scene.media.MediaErrorEvent> onError;
    /**
    Set the value of the {@link javafx.scene.media.MediaView#getOnError() onError} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B onError(javafx.event.EventHandler<javafx.scene.media.MediaErrorEvent> x) {
        this.onError = x;
        __set(3);
        return (B) this;
    }
    
    private boolean preserveRatio;
    /**
    Set the value of the {@link javafx.scene.media.MediaView#isPreserveRatio() preserveRatio} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B preserveRatio(boolean x) {
        this.preserveRatio = x;
        __set(4);
        return (B) this;
    }
    
    private boolean smooth;
    /**
    Set the value of the {@link javafx.scene.media.MediaView#isSmooth() smooth} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B smooth(boolean x) {
        this.smooth = x;
        __set(5);
        return (B) this;
    }
    
    private javafx.geometry.Rectangle2D viewport;
    /**
    Set the value of the {@link javafx.scene.media.MediaView#getViewport() viewport} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B viewport(javafx.geometry.Rectangle2D x) {
        this.viewport = x;
        __set(6);
        return (B) this;
    }
    
    private double x;
    /**
    Set the value of the {@link javafx.scene.media.MediaView#getX() x} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B x(double x) {
        this.x = x;
        __set(7);
        return (B) this;
    }
    
    private double y;
    /**
    Set the value of the {@link javafx.scene.media.MediaView#getY() y} property for the instance constructed by this builder.
    */
    @SuppressWarnings("unchecked")
    public B y(double x) {
        this.y = x;
        __set(8);
        return (B) this;
    }
    
    /**
    Make an instance of {@link javafx.scene.media.MediaView} based on the properties set on this builder.
    */
    public javafx.scene.media.MediaView build() {
        javafx.scene.media.MediaView x = new javafx.scene.media.MediaView();
        applyTo(x);
        return x;
    }
}
