/*
 * Copyright (c) 2010, 2012, Oracle and/or its affiliates. All rights reserved.
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

package javafx.scene.image;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.Queue;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyDoublePropertyBase;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import com.sun.javafx.beans.annotations.Default;
import com.sun.javafx.runtime.async.AsyncOperation;
import com.sun.javafx.runtime.async.AsyncOperationListener;
import com.sun.javafx.tk.ImageLoader;
import com.sun.javafx.tk.Toolkit;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;

/**
 * The {@code Image} class represents graphical images and is used for loading
 * images from a specified URL.
 *
 * <p>
 * Images can be resized as they are loaded (for example to reduce the amount of
 * memory consumed by the image). The application can specify the quality of
 * filtering used when scaling, and whether or not to preserve the original
 * image's aspect ratio.
 * </p>
 *
 * <p>Use {@link ImageView} for displaying images loaded with this
 * class. The same {@code Image} instance can be displayed by multiple
 * {@code ImageView}s.</p>
 *
 *<p>Example code for loading images.</p>

<PRE>
import javafx.scene.image.Image;

// load an image in background, displaying a placeholder while it's loading
// (assuming there's an ImageView node somewhere displaying this image)
Image image1 = new Image("flower.png", true);

// load an image and resize it to 100x150 without preserving its original
// aspect ratio
Image image2 = new Image("flower.png", 100, 150, false, false);

// load an image and resize it to width of 100 while preserving its
// original aspect ratio, using faster filtering method
Image image3 = new Image("flower.png", 100, 0, false, false);

// load an image and resize it only in one dimension, to the height of 100 and
// the original width, without preserving original aspect ratio
Image image4 = new Image("flower.png", 0, 100, false, false);

</PRE>
 */
public class Image {
    /**
     * The string representing the URL to use in fetching the pixel data.
     *
     * @defaultvalue empty string
     */
    private final String url;

    /**
     * @treatasprivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Deprecated
    public final String impl_getUrl() {
        return url;
    }

    /**
     * @treatasprivate
     */
    private final InputStream impl_source;

    final InputStream getImpl_source() {
        return impl_source;
    }

    /**
     * The approximate percentage of image's loading that
     * has been completed. A positive value between 0 and 1 where 0 is 0% and 1
     * is 100%.
     *
     * @defaultvalue 0
     */
    private ReadOnlyDoubleWrapper progress;


    private void setProgress(double value) {
        progressPropertyImpl().set(value);
    }

    public final double getProgress() {
        return progress == null ? 0.0 : progress.get();
    }

    public final ReadOnlyDoubleProperty progressProperty() {
        return progressPropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyDoubleWrapper progressPropertyImpl() {
        if (progress == null) {
            progress = new ReadOnlyDoubleWrapper(this, "progress");
        }
        return progress;
    }
    // PENDING_DOC_REVIEW
    /**
     * The width of the bounding box within which the source image is
     * resized as necessary to fit. If set to a value {@code <= 0}, then the
     * intrinsic width of the image will be used.
     * <p/>
     * See {@link #preserveRatio} for information on interaction between image's
     * {@code requestedWidth}, {@code requestedHeight} and {@code preserveRatio}
     * attributes.
     *
     * @defaultvalue 0
     */
    private final double requestedWidth;

    /**
     * Gets the width of the bounding box within which the source image is
     * resized as necessary to fit. If set to a value {@code <= 0}, then the
     * intrinsic width of the image will be used.
     * <p/>
     * See {@link #preserveRatio} for information on interaction between image's
     * {@code requestedWidth}, {@code requestedHeight} and {@code preserveRatio}
     * attributes.
     *
     * @return The requested width
     */
    public final double getRequestedWidth() {
        return requestedWidth;
    }
    // PENDING_DOC_REVIEW
    /**
     * The height of the bounding box within which the source image is
     * resized as necessary to fit. If set to a value {@code <= 0}, then the
     * intrinsic height of the image will be used.
     * <p/>
     * See {@link #preserveRatio} for information on interaction between image's
     * {@code requestedWidth}, {@code requestedHeight} and {@code preserveRatio}
     * attributes.
     *
     * @defaultvalue 0
     */
    private final double requestedHeight;

    /**
     * Gets the height of the bounding box within which the source image is
     * resized as necessary to fit. If set to a value {@code <= 0}, then the
     * intrinsic height of the image will be used.
     * <p/>
     * See {@link #preserveRatio} for information on interaction between image's
     * {@code requestedWidth}, {@code requestedHeight} and {@code preserveRatio}
     * attributes.
     *
     * @return The requested height
     */
    public final double getRequestedHeight() {
        return requestedHeight;
    }
    // PENDING_DOC_REVIEW
    /**
     * The image width or {@code 0} if the image loading fails. While the image
     * is being loaded it is set to {@code 0}.
     */
    private DoublePropertyImpl width;

    public final double getWidth() {
        return width == null ? 0.0 : width.get();
    }

    public final ReadOnlyDoubleProperty widthProperty() {
        return widthPropertyImpl();
    }

    private DoublePropertyImpl widthPropertyImpl() {
        if (width == null) {
            width = new DoublePropertyImpl("width");
        }

        return width;
    }

    private final class DoublePropertyImpl extends ReadOnlyDoublePropertyBase {
        private final String name;

        private double value;

        public DoublePropertyImpl(final String name) {
            this.name = name;
        }

        public void store(final double value) {
            this.value = value;
        }

        @Override
        public void fireValueChangedEvent() {
            super.fireValueChangedEvent();
        }

        @Override
        public double get() {
            return value;
        }

        @Override
        public Object getBean() {
            return Image.this;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    // PENDING_DOC_REVIEW
    /**
     * The image height or {@code 0} if the image loading fails. While the image
     * is being loaded it is set to {@code 0}.
     */
    private DoublePropertyImpl height;

    public final double getHeight() {
        return height == null ? 0.0 : height.get();
    }

    public final ReadOnlyDoubleProperty heightProperty() {
        return heightPropertyImpl();
    }

    private DoublePropertyImpl heightPropertyImpl() {
        if (height == null) {
            height = new DoublePropertyImpl("height");
        }

        return height;
    }

    /**
     * Indicates whether to preserve the aspect ratio of the original image
     * when scaling to fit the image within the bounding box provided by
     * {@code width} and {@code height}.
     * <p/>
     * If set to {@code true}, it affects the dimensions of this {@code Image}
     * in the following way:
     * <ul>
     *  <li> If only {@code width} is set, height is scaled to preserve ratio
     *  <li> If only {@code height} is set, width is scaled to preserve ratio
     *  <li> If both are set, they both may be scaled to get the best fit in a
     *  width by height rectangle while preserving the original aspect ratio
     * </ul>
     * The reported {@code width} and {@code height} may be different from the
     * initially set values if they needed to be adjusted to preserve aspect
     * ratio.
     *
     * If unset or set to {@code false}, it affects the dimensions of this
     * {@code ImageView} in the following way:
     * <ul>
     *  <li> If only {@code width} is set, the image's width is scaled to
     *  match and height is unchanged;
     *  <li> If only {@code height} is set, the image's height is scaled to
     *  match and height is unchanged;
     *  <li> If both are set, the image is scaled to match both.
     * </ul>
     * </p>
     *
     * @defaultvalue false
     */
    private final boolean preserveRatio;

    /**
     * Indicates whether to preserve the aspect ratio of the original image
     * when scaling to fit the image within the bounding box provided by
     * {@code width} and {@code height}.
     * <p/>
     * If set to {@code true}, it affects the dimensions of this {@code Image}
     * in the following way:
     * <ul>
     *  <li> If only {@code width} is set, height is scaled to preserve ratio
     *  <li> If only {@code height} is set, width is scaled to preserve ratio
     *  <li> If both are set, they both may be scaled to get the best fit in a
     *  width by height rectangle while preserving the original aspect ratio
     * </ul>
     * The reported {@code width} and {@code height} may be different from the
     * initially set values if they needed to be adjusted to preserve aspect
     * ratio.
     *
     * If unset or set to {@code false}, it affects the dimensions of this
     * {@code ImageView} in the following way:
     * <ul>
     *  <li> If only {@code width} is set, the image's width is scaled to
     *  match and height is unchanged;
     *  <li> If only {@code height} is set, the image's height is scaled to
     *  match and height is unchanged;
     *  <li> If both are set, the image is scaled to match both.
     * </ul>
     * </p>
     *
     * @returns true if the aspect ratio of the original image is to be
     *               preserved when scaling to fit the image within the bounding
     *               box provided by {@code width} and {@code height}.
     */
    public final boolean isPreserveRatio() {
        return preserveRatio;
    }

    /**
     * Indicates whether to use a better quality filtering algorithm or a faster
     * one when scaling this image to fit within the
     * bounding box provided by {@code width} and {@code height}.
     *
     * <p>
     * If not initialized or set to {@code true} a better quality filtering
     * will be used, otherwise a faster but lesser quality filtering will be
     * used.
     * </p>
     *
     * @defaultvalue true
     */
    private final boolean smooth;

    /**
     * Indicates whether to use a better quality filtering algorithm or a faster
     * one when scaling this image to fit within the
     * bounding box provided by {@code width} and {@code height}.
     *
     * <p>
     * If not initialized or set to {@code true} a better quality filtering
     * will be used, otherwise a faster but lesser quality filtering will be
     * used.
     * </p>
     *
     * @return true if a better quality (but slower) filtering algorithm
     *              is used for scaling to fit within the
     *              bounding box provided by {@code width} and {@code height}.
     */
    public final boolean isSmooth() {
        return smooth;
    }

    /**
     * Indicates whether the image is being loaded in the background.
     *
     * @defaultvalue false
     */
    private final boolean backgroundLoading;

    /**
     * Indicates whether the image is being loaded in the background.
     * @return true if the image is loaded in the background
     */
    public final boolean isBackgroundLoading() {
        return backgroundLoading;
    }

    /**
     * Indicates whether an error was detected while loading an image.
     *
     * @defaultvalue false
     */
    private ReadOnlyBooleanWrapper error;


    private void setError(boolean value) {
        errorPropertyImpl().set(value);
    }

    public final boolean isError() {
        return error == null ? false : error.get();
    }

    public final ReadOnlyBooleanProperty errorProperty() {
        return errorPropertyImpl().getReadOnlyProperty();
    }

    private ReadOnlyBooleanWrapper errorPropertyImpl() {
        if (error == null) {
            error = new ReadOnlyBooleanWrapper(this, "error");
        }
        return error;
    }

    /**
     * The underlying platform representation of this Image object.
     *
     * @defaultvalue null
     * @treatasprivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    private ObjectPropertyImpl<Object> platformImage;

    /**
     * @treatasprivate implementation detail
     */
    @Deprecated
    public final Object impl_getPlatformImage() {
        return platformImage == null ? null : platformImage.get();
    }

    /**
     * @treatasprivate implementation detail
     */
    @Deprecated
    public final ReadOnlyObjectProperty<Object> impl_platformImageProperty() {
        return platformImagePropertyImpl();
    }

    private ObjectPropertyImpl<Object> platformImagePropertyImpl() {
        if (platformImage == null) {
            platformImage = new ObjectPropertyImpl<Object>("platformImage");
        }

        return platformImage;
    }

    private final class ObjectPropertyImpl<T>
            extends ReadOnlyObjectPropertyBase<T> {
        private final String name;

        private T value;

        public ObjectPropertyImpl(final String name) {
            this.name = name;
        }

        public void store(final T value) {
            this.value = value;
        }

        public void set(final T value) {
            if (this.value != value) {
                this.value = value;
                fireValueChangedEvent();
            }
        }

        @Override
        public void fireValueChangedEvent() {
            super.fireValueChangedEvent();
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public Object getBean() {
            return Image.this;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    /**
     * Construct an {@code Image} which pixels are loaded from the specified
     * url.
     *
     * @param url the string representing the URL to use in fetching the pixel
     *      data
     */
    public Image(String url) {
        this(url, null, 0, 0, false, false, false);
        initialize(null);
    }

    /**
     * Construct a new {@code Image} with the specified parameters.
     *
     * @param url the string representing the URL to use in fetching the pixel
     *      data
     * @param backgroundLoading indicates whether the image
     *      is being loaded in the background
     */
    public Image(String url, boolean backgroundLoading) {
        this(url, null, 0, 0, false, false, backgroundLoading);
        initialize(null);
    }

    /**
     * Construct a new {@code Image} with the specified parameters.
     *
     * @param url the string representing the URL to use in fetching the pixel
     *      data
     * @param requestedWidth the image's bounding box width
     * @param requestedHeight the image's bounding box height
     * @param preserveRatio indicates whether to preserve the aspect ratio of
     *      the original image when scaling to fit the image within the
     *      specified bounding box
     * @param smooth indicates whether to use a better quality filtering
     *      algorithm or a faster one when scaling this image to fit within
     *      the specified bounding box
     */
    public Image(String url, double requestedWidth, double requestedHeight,
                 boolean preserveRatio, boolean smooth) {
        this(url, null, requestedWidth, requestedHeight, preserveRatio, smooth,
             false);
        initialize(null);
    }

    /**
     * Construct a new {@code Image} with the specified parameters.
     *
     * @param url the string representing the URL to use in fetching the pixel
     *      data
     * @param requestedWidth the image's bounding box width
     * @param requestedHeight the image's bounding box height
     * @param preserveRatio indicates whether to preserve the aspect ratio of
     *      the original image when scaling to fit the image within the
     *      specified bounding box
     * @param smooth indicates whether to use a better quality filtering
     *      algorithm or a faster one when scaling this image to fit within
     *      the specified bounding box
     * @param backgroundLoading indicates whether the image
     *      is being loaded in the background
     */
    public Image(
            @Default("\"\"") String url,
            double requestedWidth,
            double requestedHeight,
            boolean preserveRatio,
            @Default("true") boolean smooth,
            boolean backgroundLoading) {
        this(url, null, requestedWidth, requestedHeight, preserveRatio, smooth,
             backgroundLoading);
        initialize(null);
    }

    /**
     * Construct an {@code Image} which pixels are loaded from the specified
     * input stream.
     *
     * @param is the stream from which to load the image
     */
    public Image(InputStream is) {
        this(null, is, 0, 0, false, false, false);
        initialize(null);
    }

    /**
     * Construct a new {@code Image} with the specified parameters.
     *
     * @param is the stream from which to load the image
     * @param requestedWidth the image's bounding box width
     * @param requestedHeight the image's bounding box height
     * @param preserveRatio indicates whether to preserve the aspect ratio of
     *      the original image when scaling to fit the image within the
     *      specified bounding box
     * @param smooth indicates whether to use a better quality filtering
     *      algorithm or a faster one when scaling this image to fit within
     *      the specified bounding box
     */
    public Image(InputStream is, double requestedWidth, double requestedHeight,
                 boolean preserveRatio, boolean smooth) {
        this(null, is, requestedWidth, requestedHeight, preserveRatio, smooth,
             false);
        initialize(null);
    }

    private Image(Object platformImage) {
        this(null, null, 0, 0, false, false, false);
        initialize(platformImage);
    }

    private Image(String url, InputStream is,
                  double requestedWidth, double requestedHeight,
                  boolean preserveRatio, boolean smooth,
                  boolean backgroundLoading) {
        this.url = url;
        this.impl_source = is;
        this.requestedWidth = requestedWidth;
        this.requestedHeight = requestedHeight;
        this.preserveRatio = preserveRatio;
        this.smooth = smooth;
        this.backgroundLoading = backgroundLoading;
    }

    /**
     * Cancels the background loading of this image.
     *
     * <p>Has no effect if this image isn't loaded in background or if loading
     * has already completed.</p>
     */
    public void cancel() {
        if (backgroundTask != null) {
            backgroundTask.cancel();
        }
    }

    /**
     * @treatasprivate used for testing
     */
    void dispose() {
        cancel();
        if (timeline != null) {
            timeline.stop();
        }
    }

    private ImageTask backgroundTask;

    private void initialize(Object platformImage) {
        // we need to check the original values here, because setting placeholder
        // changes platformImage, so wrong branch of if would be used
        if (platformImage != null) {
            // Make an image from the provided platform-specific image
            // object (e.g. a BufferedImage in the case of the Swing profile)
            ImageLoader loader = loadPlatformImage(platformImage);
            finishImage(loader);
        } else if (isBackgroundLoading() && (impl_source == null)) {
            // Load image in the background.
            loadInBackground();
        } else {
            // Load image immediately.
            ImageLoader loader;
            if (impl_source != null) {
                loader = loadImage(impl_source, getRequestedWidth(), getRequestedHeight(),
                                   isPreserveRatio(), isSmooth());
            } else {
                loader = loadImage(impl_getUrl(), getRequestedWidth(), getRequestedHeight(),
                                   isPreserveRatio(), isSmooth());
            }
            finishImage(loader);
        }
    }

    private void finishImage(ImageLoader loader) {
        if ((loader != null) && !loader.getError()) {
            if (loader.getFrameCount() > 1) {
                makeAnimationTimeline(loader);
            } else {
                setPlatformImageWH(loader.getFrame(0),
                                   loader.getWidth(),
                                   loader.getHeight());
            }
        } else {
            setError(true);
            setPlatformImageWH(null, 0, 0);
        }

        setProgress(1);
    }

    // Support for animated images.
    private Timeline timeline;

    // Generates the animation Timeline for multiframe images.
    private void makeAnimationTimeline(ImageLoader loader) {
        // create and start the animation thread.
        final Object[] frames = loader.getFrames();

        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        final ObservableList<KeyFrame> keyFrames = timeline.getKeyFrames();

        int duration = 0;
        for (int i = 0; i < frames.length; ++i) {
            keyFrames.add(createPlatformImageSetKeyFrame(duration, frames[i]));
            duration = duration + loader.getFrameDelay(i);
        }

        // Note: we need one extra frame in the timeline to define how long
        // the last frame is shown, the wrap around is "instantaneous"
        keyFrames.add(createPlatformImageSetKeyFrame(duration, frames[0]));

        setPlatformImageWH(frames[0], loader.getWidth(), loader.getHeight());
        timeline.play();
    }

    private KeyFrame createPlatformImageSetKeyFrame(
            final long duration,
            final Object platformImage) {
        return new KeyFrame(Duration.millis(duration),
                            new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        platformImagePropertyImpl().set(
                                                platformImage);
                                    }
                            });
    }

    private void cycleTasks() {
        synchronized (pendingTasks) {
            runningTasks--;
            // do we have any pending tasks to run ?
            // we can assume we are under the throttle limit because
            // one task just completed.
            final ImageTask nextTask = pendingTasks.poll();
            if (nextTask != null) {
                runningTasks++;
                nextTask.start();
            }
        }
    }

    private void loadInBackground() {
        backgroundTask = new ImageTask();
        // This is an artificial throttle on background image loading tasks.
        // It has been shown that with large images, we can quickly use up the
        // heap loading images, even if they result in thumbnails.
        // The limit of MAX_RUNNING_TASKS is arbitrary, and was based on initial
        // testing with
        // about 60 2-6 megapixel images.
        synchronized (pendingTasks) {
            if (runningTasks >= MAX_RUNNING_TASKS) {
                pendingTasks.offer(backgroundTask);
            } else {
                runningTasks++;
                backgroundTask.start();
            }
        }
    }

    // Used by SwingUtils.toFXImage
    /**
     * @treatasprivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Deprecated
    public static Image impl_fromPlatformImage(Object image) {
        return new Image(image);
    }

    private void setPlatformImageWH(final Object newPlatformImage,
                                    final double newWidth,
                                    final double newHeight) {
        if ((impl_getPlatformImage() == newPlatformImage)
                && (getWidth() == newWidth)
                && (getHeight() == newHeight)) {
            return;
        }

        final Object oldPlatformImage = impl_getPlatformImage();
        final double oldWidth = getWidth();
        final double oldHeight = getHeight();

        storePlatformImageWH(newPlatformImage, newWidth, newHeight);

        if (oldPlatformImage != newPlatformImage) {
            platformImagePropertyImpl().fireValueChangedEvent();
        }

        if (oldWidth != newWidth) {
            widthPropertyImpl().fireValueChangedEvent();
        }

        if (oldHeight != newHeight) {
            heightPropertyImpl().fireValueChangedEvent();
        }
    }

    private void storePlatformImageWH(final Object platformImage,
                                      final double width,
                                      final double height) {
        platformImagePropertyImpl().store(platformImage);
        widthPropertyImpl().store(width);
        heightPropertyImpl().store(height);
    }

    private static final int MAX_RUNNING_TASKS = 4;
    private static int runningTasks = 0;
    private static final Queue<ImageTask> pendingTasks =
            new LinkedList<ImageTask>();

    private final class ImageTask
            implements AsyncOperationListener<ImageLoader> {

        private final AsyncOperation peer;

        public ImageTask() {
            peer = constructPeer();
        }

        @Override
        public void onCancel() {
            finishImage(null);
            cycleTasks();
        }

        @Override
        public void onException(Exception exception) {
            finishImage(null);
            cycleTasks();
        }

        @Override
        public void onCompletion(ImageLoader value) {
            finishImage(value);
            cycleTasks();
        }

        @Override
        public void onProgress(int cur, int max) {
            if (max > 0) {
                double curProgress = (double) cur / max;
                if ((curProgress < 1) && (curProgress >= (getProgress() + 0.1))) {
                    setProgress(curProgress);
                }
            }
        }

        public void start() {
            peer.start();
        }

        public void cancel() {
            peer.cancel();
        }

        private AsyncOperation constructPeer() {
            return loadImageAsync(this, url,
                                  requestedWidth, requestedHeight,
                                  preserveRatio, smooth);
        }
    }

    private static ImageLoader loadImage(
            String url, double width, double height,
            boolean preserveRatio, boolean smooth) {
        return Toolkit.getToolkit().loadImage(url, (int) width, (int) height,
                                              preserveRatio, smooth);

    }

    private static ImageLoader loadImage(
            InputStream stream, double width, double height,
            boolean preserveRatio, boolean smooth) {
        return Toolkit.getToolkit().loadImage(stream, (int) width, (int) height,
                                              preserveRatio, smooth);

    }

    private static AsyncOperation loadImageAsync(
            AsyncOperationListener<? extends ImageLoader> listener,
            String url, double width, double height,
            boolean preserveRatio, boolean smooth) {
        return Toolkit.getToolkit().loadImageAsync(listener, url,
                                                   (int) width, (int) height,
                                                   preserveRatio, smooth);
    }

    private static ImageLoader loadPlatformImage(Object platformImage) {
        return Toolkit.getToolkit().loadPlatformImage(platformImage);
    }


    /**
     * This method converts a JavaFX Image to the specified image class or
     * to an image of the same class and the same format as specified image.
     * If the specified image class is not supported, then null will be returned.
     *
     * @param imgType either a Class object that specifies the type of image,
     *      or an actual image object of the desired type and format.
     * @treatasprivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Deprecated
    public Object impl_toExternalImage(Object imgType) {
        return Toolkit.getToolkit().toExternalImage(impl_getPlatformImage(), imgType);
    }

    /**
     * This method converts the specified image to a JavaFX Image.
     * If the specified image class is not supported, then null will be returned.
     *
     * @param extImage - an image to convert.
     * @treatasprivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Deprecated
    public static Image impl_fromExternalImage(Object extImage) {
        return impl_isExternalFormatSupported(extImage.getClass())
                ? new Image(extImage)
                : null;
    }

    /**
     * This method indicates whether conversion to/from the specified external
     * format is possible.
     *
     * @treatasprivate implementation detail
     * @deprecated This is an internal API that is not intended for use and will be removed in the next version
     */
    @Deprecated
    public static boolean impl_isExternalFormatSupported(Class format) {
        return Toolkit.getToolkit().isExternalFormatSupported(format);
    }

    /**
     * Indicates whether image is animated.
     */
    boolean isAnimation() {
        return timeline != null;
    }
}
