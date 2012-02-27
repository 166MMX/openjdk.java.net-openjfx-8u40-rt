/*
 * Copyright (c) 2010, Oracle and/or its affiliates. All rights reserved.
 */

package javafx.scene.control;


import org.junit.Ignore;

/**
 * @author smarks
 */
@Ignore
public class SliderTest {
//    Slider slider;
//
//    /**
//     * Creates a slider.
//     *
//     * Assumes min == 0, value == 0, max == 100, horizontal
//     * (i.e., vertical == false).
//     */
//    @Override protected Node createNodeToTest() {
//        slider = new Slider();
//        return slider;
//    }
//
//    // TESTS
//
//    @Test
//    public void ensureSkinExists() {
//        assertNotNull(slider.getSkin());
//    }
//
//    @Test
//    public void ensureSkinNodesExist() {
//        assertNotNull(findNodeByStyleClass("thumb"));
//        assertNotNull(findNodeByStyleClass("track"));
//    }
//
//    @Test
//    public void thumbIsPositionedAtLeftWhenValueIsMinimum() {
//        Node thumb = findNodeByStyleClass("thumb");
//        Node track = findNodeByStyleClass("track");
//        Bounds thumbBounds = thumb.localToScene(thumb.getBoundsInLocal());
//        Bounds trackBounds = track.localToScene(track.getBoundsInLocal());
//        double trackLeftX = trackBounds.getMinX();
//        assertTrue(thumbBounds.getMinX() < trackLeftX);
//        assertTrue(thumbBounds.getMaxX() > trackLeftX);
//    }
//
//    @Test
//    public void thumbIsPositionedAtHcenterWhenValueIsMiddle() {
//        slider.setValue(50);
//        awaitQuiescent();
//        Node thumb = findNodeByStyleClass("thumb");
//        Node track = findNodeByStyleClass("track");
//        Bounds thumbBounds = thumb.localToScene(thumb.getBoundsInLocal());
//        Bounds trackBounds = track.localToScene(track.getBoundsInLocal());
//        double trackCenterX = trackBounds.getMinX() + trackBounds.getWidth() / 2.0;
//        assertTrue(thumbBounds.getMinX() < trackCenterX);
//        assertTrue(thumbBounds.getMaxX() > trackCenterX);
//    }
//
//    @Test
//    public void thumbIsPositionedAtRightWhenValueIsMaximum() {
//        slider.setValue(100);
//        awaitQuiescent();
//        Node thumb = findNodeByStyleClass("thumb");
//        Node track = findNodeByStyleClass("track");
//        Bounds thumbBounds = thumb.localToScene(thumb.getBoundsInLocal());
//        Bounds trackBounds = track.localToScene(track.getBoundsInLocal());
//        double trackRightX = trackBounds.getMaxX();
//        assertTrue(thumbBounds.getMinX() < trackRightX);
//        assertTrue(thumbBounds.getMaxX() > trackRightX);
//    }
//
//    @Test
//    public void thumbIsPositionedAtTopWhenValueIsMinimum() {
//        slider.setVertical(true);
//        awaitQuiescent();
//        Node track = findNodeByStyleClass("track");
//        Node thumb = findNodeByStyleClass("thumb");
//        Bounds thumbBounds = thumb.localToScene(thumb.getBoundsInLocal());
//        Bounds trackBounds = track.localToScene(track.getBoundsInLocal());
//        double trackTopY = trackBounds.getMinY();
//        assertTrue(thumbBounds.getMinY() < trackTopY);
//        assertTrue(thumbBounds.getMaxY() > trackTopY);
//    }
//
//    @Test
//    public void thumbIsPositionedAtVcenterWhenValueIsMiddle() {
//        slider.setVertical(true);
//        slider.setValue(50);
//        awaitQuiescent();
//        Node track = findNodeByStyleClass("track");
//        Node thumb = findNodeByStyleClass("thumb");
//        Bounds thumbBounds = thumb.localToScene(thumb.getBoundsInLocal());
//        Bounds trackBounds = track.localToScene(track.getBoundsInLocal());
//        double trackCenterY = trackBounds.getMinY() + trackBounds.getHeight() / 2.0;
//        assertTrue(thumbBounds.getMinY() < trackCenterY);
//        assertTrue(thumbBounds.getMaxY() > trackCenterY);
//    }
//
//    @Test
//    public void thumbIsPositionedAtBottomWhenValueIsMaximum() {
//        slider.setVertical(true);
//        slider.setValue(100);
//        awaitQuiescent();
//        Node track = findNodeByStyleClass("track");
//        Node thumb = findNodeByStyleClass("thumb");
//        Bounds thumbBounds = thumb.localToScene(thumb.getBoundsInLocal());
//        Bounds trackBounds = track.localToScene(track.getBoundsInLocal());
//        double trackBottomY = trackBounds.getMaxY();
//        assertTrue(thumbBounds.getMinY() < trackBottomY);
//        assertTrue(thumbBounds.getMaxY() > trackBottomY);
//    }
//
//    @Test
//    public void movingThumbShouldChangeValue() {
//        double originalValue = slider.getValue();
//        Node thumb = findNodeByStyleClass("thumb");
//        mouse().positionAtCenterOf(thumb);
//        Point2D center = centerOf(thumb);
//        mouse().leftPressDragRelease(50, center.getY());
//        assertTrue(slider.getValue() > originalValue);
//    }

}
