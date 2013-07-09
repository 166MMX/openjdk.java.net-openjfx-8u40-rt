/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
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

package com.sun.javafx.font.coretext;

import com.sun.javafx.font.FontResource;
import com.sun.javafx.font.Glyph;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Shape;

class CTGlyph implements Glyph {
    private CTFontStrike strike;
    private int glyphCode;
    private CGRect bounds;
    private double xAdvance;
    private double yAdvance;
    private boolean drawShapes;

    /* Always using BRGA context has the same performance as gray */
    private static boolean LCD_CONTEXT = true;
    private static boolean CACHE_CONTEXT = true;

    private static long cachedContextRef;
    private static final int BITMAP_WIDTH = 256;
    private static final int BITMAP_HEIGHT = 256;
    private static final long GRAY_COLORSPACE = OS.CGColorSpaceCreateDeviceGray();
    private static final long RGB_COLORSPACE = OS.CGColorSpaceCreateDeviceRGB();

    CTGlyph(CTFontStrike strike, int glyphCode, boolean drawShapes) {
        this.strike = strike;
        this.glyphCode = glyphCode;
        this.drawShapes = drawShapes;
    }

    @Override public int getGlyphCode() {
        return glyphCode;
    }

    /* Note, according to javadoc these bounds should be
     * in user space but T2K uses device space.
     */
    @Override public RectBounds getBBox() {
        /* IN T2k this is the bounds of the glyph path see GeneralPath.cpp */
        CGRect rect = strike.getBBox(glyphCode);
        return new RectBounds((float)rect.origin.x,
                              (float)rect.origin.y,
                              (float)(rect.origin.x + rect.size.width),
                              (float)(rect.origin.y + rect.size.height));
    }

    private void checkBounds() {
        if (bounds != null) return;

        long fontRef = strike.getFontRef();
        int orientation = OS.kCTFontOrientationDefault;
        CGSize size = new CGSize();
        OS.CTFontGetAdvancesForGlyphs(fontRef, orientation, (short)glyphCode, size, 1);
        xAdvance = size.width;
        yAdvance = -size.height;   /*Inverted coordinates system */
        bounds = new CGRect();

        if (drawShapes) return;

        /* Avoid CTFontGetBoundingRectsForGlyphs as it is too slow */
//        bounds = OS.CTFontGetBoundingRectsForGlyphs(fontRef, orientation, (short)glyphCode, null, 1);

        CTFontFile fr = (CTFontFile)strike.getFontResource();
        float[] bb = new float[4];
        fr.getGlyphBoundingBox((short)glyphCode, strike.getSize(), bb);
        bounds.origin.x = bb[0];
        bounds.origin.y = bb[1];
        bounds.size.width = (bb[2] - bb[0]);
        bounds.size.height = (bb[3] - bb[1]);
        if (strike.matrix != null) {
            /* Need to use the native matrix as it is y up */
            OS.CGRectApplyAffineTransform(bounds, strike.matrix);
        }

        /* The box is increased to capture all fragments from LCD rendering  */
        bounds.origin.x = (int)Math.floor(bounds.origin.x) - 1;
        bounds.origin.y = (int)Math.floor(bounds.origin.y) - 1;
        bounds.size.width = (int)Math.ceil(bounds.size.width) + 1 + 1 + 1;
        bounds.size.height = (int)Math.ceil(bounds.size.height) + 1 + 1 + 1;

    }

    @Override public Shape getShape() {
        return strike.createGlyphOutline(glyphCode);
    }

    private long createContext(boolean lcd, int width, int height) {
        long space;
        int bpc = 8, bpr, flags;
        if (lcd) {
            space = RGB_COLORSPACE;
            bpr = width * 4;
            flags = OS.kCGBitmapByteOrder32Host | OS.kCGImageAlphaPremultipliedFirst;
        } else {
            space = GRAY_COLORSPACE;
            bpr = width;
            flags = OS.kCGImageAlphaNone;
        }
        long context =  OS.CGBitmapContextCreate(0, width, height, bpc, bpr, space, flags);

        boolean subPixel = strike.isSubPixelGlyph();
        OS.CGContextSetAllowsFontSmoothing(context, lcd);
        OS.CGContextSetAllowsAntialiasing(context, true);
        OS.CGContextSetAllowsFontSubpixelPositioning(context, subPixel);
        OS.CGContextSetAllowsFontSubpixelQuantization(context, subPixel);
        return context;

    }

    private long getCachedContext(boolean lcd) {
        if (cachedContextRef == 0) {
            cachedContextRef = createContext(lcd, BITMAP_WIDTH, BITMAP_HEIGHT);
        }
        return cachedContextRef;
    }

    private synchronized byte[] getImage(double x, double y, int w, int h,
                                         double subPixelX, double subPixelY) {

        if (w == 0 || h == 0) return new byte[0];

        long fontRef = strike.getFontRef();
        boolean lcd = isLCDGlyph();
        boolean lcdContext = LCD_CONTEXT || lcd;
        CGAffineTransform matrix = strike.matrix;
        boolean cache = CACHE_CONTEXT & BITMAP_WIDTH >= w & BITMAP_HEIGHT >= h;
        long context = cache ? getCachedContext(lcdContext) :
                               createContext(lcdContext, w, h);

        /* Fill background with white */
        OS.CGContextSetRGBFillColor(context, 1, 1, 1, 1);
        CGRect rect = new CGRect();
        rect.size.width = w;
        rect.size.height = h;
        OS.CGContextFillRect(context, rect);

        CGPoint pt = new CGPoint();
        pt.x = x;
        pt.y = y;
        if (matrix != null) {
            /* Need to use the native matrix as it is y up */
            OS.CGPointApplyAffineTransform(pt, strike.imatrix);
        } else {
            if (strike.isSubPixelGlyph()) {
                pt.x -= subPixelX;
                pt.y -= subPixelY;
            }
        }

        /* Draw the text with black */
        OS.CGContextSetRGBFillColor(context, 0, 0, 0, 1);
        OS.CTFontDrawGlyphs(fontRef, (short)glyphCode, -pt.x, -pt.y, 1, context);

        byte[] imageData;
        if (lcd) {
            imageData = OS.CGBitmapContextGetData(context, w, h, 24);
        } else {
            imageData = OS.CGBitmapContextGetData(context, w, h, 8);
        }

        if (!cache) {
            OS.CGContextRelease(context);
        }
        return imageData;
    }

    @Override public byte[] getPixelData() {
        return getPixelData(0, 0);
    }

    @Override public byte[] getPixelData(float x, float y) {
        checkBounds();
        return getImage(bounds.origin.x, bounds.origin.y,
                        (int)bounds.size.width, (int)bounds.size.height, x, y);
    }

    @Override public float getAdvance() {
        checkBounds();
        //TODO should be user space (this method is not used)
        return (float)xAdvance;
    }

    @Override public float getPixelXAdvance() {
        checkBounds();
        return (float)xAdvance;
    }

    @Override public float getPixelYAdvance() {
        checkBounds();
        return (float)yAdvance;
    }

    @Override public int getWidth() {
        checkBounds();
        int w = (int)bounds.size.width;
        return isLCDGlyph() ? w * 3 : w;
    }

    @Override public int getHeight() {
        checkBounds();
        return (int)bounds.size.height;
    }

    @Override public int getOriginX() {
        checkBounds();
        return (int)bounds.origin.x;
    }

    @Override public int getOriginY() {
        checkBounds();
        int h = (int)bounds.size.height;
        int y = (int)bounds.origin.y;
        return -h - y; /*Inverted coordinates system */
    }

    @Override public boolean isLCDGlyph() {
        return strike.getAAMode() == FontResource.AA_LCD;
    }

}
