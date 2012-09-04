/*
 * Copyright (c) 2011, 2012, Oracle and/or its affiliates. All rights reserved.
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
package javafx.scene.transform;

import static javafx.scene.transform.TransformTest.assertTx;
import javafx.scene.shape.Rectangle;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

import com.sun.javafx.test.TransformHelper;
import com.sun.javafx.geom.transform.Affine2D;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.scene.transform.TransformUtils;

public class AffineTest {

    @Test
    public void testAffine() {
        final Affine trans = new Affine() {{
            setMxx(11);
            setMyx(22);
            setMxy(33);
            setMyy(44);
            setTx(55);
            setTy(66);
        }};
        Assert.assertEquals(1.0f, trans.getMzz(), 1e-50);

        final Rectangle n = new Rectangle();
        n.getTransforms().add(trans);

        final Affine2D affine2D = new Affine2D(11, 22, 33, 44, 55, 66);
        Assert.assertEquals(1, affine2D.getMzz(), 1e-50);
        final Affine3D affine3D = new Affine3D(affine2D);
        Assert.assertEquals(1, affine3D.getMzz(), 1e-50);
        assertTx(n, affine3D);

        trans.setMxx(10);
        assertTx(n, new Affine2D(10, 22, 33, 44, 55, 66));

        trans.setMyx(21);
        assertTx(n, new Affine2D(10, 21, 33, 44, 55, 66));

        trans.setMxy(32);
        assertTx(n, new Affine2D(10, 21, 32, 44, 55, 66));

        trans.setMyy(43);
        assertTx(n, new Affine2D(10, 21, 32, 43, 55, 66));

        trans.setTx(54);
        assertTx(n, new Affine2D(10, 21, 32, 43, 54, 66));

        trans.setTy(65);
        assertTx(n, new Affine2D(10, 21, 32, 43, 54, 65));
    }

    @Test public void testGetters() {
        final Affine trans = new Affine(
                0.5,  2,  3,  4,
                5,  6,  7,  8,
                9, 10, 11, 12);
        TransformHelper.assertMatrix(trans,
                0.5,  2,  3,  4,
                5,  6,  7,  8,
                9, 10, 11, 12);

        trans.setMxx(12);
        trans.setMxy(11);
        trans.setMxz(10);
        trans.setTx(9);
        trans.setMyx(8);
        trans.setMyy(7);
        trans.setMyz(6);
        trans.setTy(5);
        trans.setMzx(4);
        trans.setMzy(3);
        trans.setMzz(2);
        trans.setTz(1);

        TransformHelper.assertMatrix(trans,
                12, 11, 10, 9,
                 8,  7,  6, 5,
                 4,  3,  2, 1);
    }

    @Test public void testConstructingIdentityTransform() {
        final Affine trans = new Affine(
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0);

        TransformHelper.assertMatrix(trans,
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0);

        trans.setMxx(12);
        trans.setMxy(11);
        trans.setMxz(10);
        trans.setTx(9);
        trans.setMyx(8);
        trans.setMyy(7);
        trans.setMyz(6);
        trans.setTy(5);
        trans.setMzx(4);
        trans.setMzy(3);
        trans.setMzz(2);
        trans.setTz(1);

        TransformHelper.assertMatrix(trans,
                12, 11, 10, 9,
                 8,  7,  6, 5,
                 4,  3,  2, 1);
    }

    @Test public void testSettingTransform() {
        final Affine trans = new Affine(
                1,  2,  3,  4,
                5,  6,  7,  8,
                9, 10, 11, 12);

        TransformHelper.assertMatrix(trans,
                1,  2,  3,  4,
                5,  6,  7,  8,
                9, 10, 11, 12);

        Transform it = TransformUtils.immutableTransform(
                12, 11, 10, 9,
                 8,  7,  6, 5,
                 4,  3,  2, 1);

        trans.setTransform(it);

        TransformHelper.assertMatrix(trans,
                12, 11, 10, 9,
                 8,  7,  6, 5,
                 4,  3,  2, 1);
    }

    @Test
    public void testCopying() {
        final Affine trans = new Affine(
                1,  2,  3,  4,
                5,  6,  7,  8,
                9, 10, 11, 12);

        Transform copy = trans.impl_copy();

        TransformHelper.assertMatrix(copy,
                1,  2,  3,  4,
                5,  6,  7,  8,
                9, 10, 11, 12);
    }

    @Test public void testToString() {
        final Affine trans = new Affine(
                1,  2,  3,  4,
                5,  6,  7,  8,
                9, 10, 11, 12);

        String s = trans.toString();

        assertNotNull(s);
        assertFalse(s.isEmpty());
    }

    @Test public void testBoundPropertySynced() throws Exception {

        TransformTest.checkDoublePropertySynced(createTransform(), "mxx" , 2.0);
        TransformTest.checkDoublePropertySynced(createTransform(), "myx" , 2.0);
        TransformTest.checkDoublePropertySynced(createTransform(), "mxy" , 2.0);
        TransformTest.checkDoublePropertySynced(createTransform(), "mxz" , 2.0);
        TransformTest.checkDoublePropertySynced(createTransform(), "myy" , 2.0);
        TransformTest.checkDoublePropertySynced(createTransform(), "myz" , 2.0);
        TransformTest.checkDoublePropertySynced(createTransform(), "mzx" , 2.0);
        TransformTest.checkDoublePropertySynced(createTransform(), "mzy" , 2.0);
        TransformTest.checkDoublePropertySynced(createTransform(), "mzz" , 2.0);
        TransformTest.checkDoublePropertySynced(createTransform(), "tx" , 2.0);
        TransformTest.checkDoublePropertySynced(createTransform(), "ty" , 2.0);
        TransformTest.checkDoublePropertySynced(createTransform(), "tz" , 2.0);
    }

    private Affine createTransform() {
        final Affine trans = new Affine() {{
            setMxx(11);
            setMyx(22);
            setMxy(33);
            setMyy(44);
            setTx(55);
            setTy(66);
        }};
        return trans;
    }
}
