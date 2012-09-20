/*
 * Copyright (c) 2010, 2011, Oracle  and/or its affiliates. All rights reserved.
 */

package javafx.scene.control;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 */
@Ignore
public class ControlTooltipTest {
    private ControlStub c;
    private SkinStub<ControlStub> s;
    private Tooltip t;

    @Before public void setUp() {
        c = new ControlStub();
        s = new SkinStub<ControlStub>(c);
        c.setSkin(s);
        t = new Tooltip();
//        t.setSkin(new SkinStub<Tooltip>(t));
    }

    @Test public void controlsWithNoTooltipHaveNoTooltipAsAChild() {
        // only the skin's node should be a child
        assertEquals(1, c.getChildrenUnmodifiable().size());
        assertSame(s.getNode(), c.getChildrenUnmodifiable().get(0));
    }

    @Test public void settingTooltipOnControlResultsInTooltipBeingFirstChild() {
        c.setTooltip(t);
        assertEquals(2, c.getChildrenUnmodifiable().size());
        assertSame(t, c.getChildrenUnmodifiable().get(0));
        assertSame(s.getNode(), c.getChildrenUnmodifiable().get(1));
    }

    @Test public void settingTooltipToNullRemovesTheTooltipFromChildren() {
        c.setTooltip(t);
        c.setTooltip(null);
        assertEquals(1, c.getChildrenUnmodifiable().size());
        assertSame(s.getNode(), c.getChildrenUnmodifiable().get(0));
    }

    @Test public void settingTooltipTwiceIgnoresTheSecondAdd() {
        c.setTooltip(t);
        c.setTooltip(t);
        assertEquals(2, c.getChildrenUnmodifiable().size());
        assertSame(t, c.getChildrenUnmodifiable().get(0));
        assertSame(s.getNode(), c.getChildrenUnmodifiable().get(1));
    }

    @Test public void swappingTheTooltipForAnotherResultsInTheNewTooltipBeingAChildAndTheOldOneRemoved() {
        c.setTooltip(t);
        Tooltip t2 = new Tooltip();
//        t2.setSkin(new SkinStub<Tooltip>(t2));
        c.setTooltip(t2);
        assertEquals(2, c.getChildrenUnmodifiable().size());
        assertSame(t2, c.getChildrenUnmodifiable().get(0));
        assertSame(s.getNode(), c.getChildrenUnmodifiable().get(1));
    }
}
