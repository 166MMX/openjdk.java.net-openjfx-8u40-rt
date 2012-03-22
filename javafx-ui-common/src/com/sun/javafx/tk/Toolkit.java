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

package com.sun.javafx.tk;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.CountDownLatch;

import javafx.application.ConditionalFeature;
import javafx.geometry.Dimension2D;
import javafx.scene.Scene;
import javafx.scene.effect.BlurType;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.InputMethodRequests;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import com.sun.javafx.embed.HostInterface;
import com.sun.javafx.geom.ParallelCameraImpl;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.PerspectiveCameraImpl;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.HighlightRegion;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.perf.PerformanceTracker;
import com.sun.javafx.runtime.VersionInfo;
import com.sun.javafx.runtime.async.AsyncOperation;
import com.sun.javafx.runtime.async.AsyncOperationListener;
import com.sun.javafx.scene.paint.ImagePattern;
import com.sun.javafx.scene.text.HitInfo;
import com.sun.javafx.sg.PGArc;
import com.sun.javafx.sg.PGCircle;
import com.sun.javafx.sg.PGCubicCurve;
import com.sun.javafx.sg.PGEllipse;
import com.sun.javafx.sg.PGGroup;
import com.sun.javafx.sg.PGImageView;
import com.sun.javafx.sg.PGLine;
import com.sun.javafx.sg.PGMediaView;
import com.sun.javafx.sg.PGPath;
import com.sun.javafx.sg.PGPolygon;
import com.sun.javafx.sg.PGPolyline;
import com.sun.javafx.sg.PGQuadCurve;
import com.sun.javafx.sg.PGRectangle;
import com.sun.javafx.sg.PGRegion;
import com.sun.javafx.sg.PGSVGPath;
import com.sun.javafx.sg.PGShape;
import com.sun.javafx.sg.PGText;
import com.sun.javafx.PlatformUtil;
import com.sun.scenario.DelayedRunnable;
import com.sun.scenario.animation.AbstractMasterTimer;
import com.sun.scenario.effect.AbstractShadow.ShadowMode;
import com.sun.scenario.effect.Color4f;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.Filterable;


public abstract class Toolkit {
    private static String tk;
    private static Toolkit TOOLKIT;
    private static Thread fxUserThread = null;

    private static final String QUANTUM_TOOLKIT     = "com.sun.javafx.tk.quantum.QuantumToolkit";
    private static final String DEFAULT_TOOLKIT     = QUANTUM_TOOLKIT;
    
    protected static Map gradientMap = new WeakHashMap();

    private static String lookupToolkitClass(String name) {
        if ("prism".equalsIgnoreCase(name)) {
            return QUANTUM_TOOLKIT;
        } else if ("quantum".equalsIgnoreCase(name)) {
            return QUANTUM_TOOLKIT;
        }
        return name;
    }

    private static String getDefaultToolkit() {
        if (PlatformUtil.isWindows()) {
            return DEFAULT_TOOLKIT;
        } else if (PlatformUtil.isMac()) {
            return DEFAULT_TOOLKIT;
        } else if (PlatformUtil.isLinux()) {
            return DEFAULT_TOOLKIT;
        }

        throw new UnsupportedOperationException(System.getProperty("os.name") + " is not supported");
    }

    public static synchronized Toolkit getToolkit() {
        if (TOOLKIT != null) {
            return TOOLKIT;
        }

        // This loading of msvcr100.dll (VS2010) is required when run with Java 6
        // since it was build with VS2003 and doesn't include msvcr100.dll in it's JRE.
        // Note: See README-builds.html on MSVC requirement: VS2010 is required.
        if (PlatformUtil.isWindows()) {
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                public Object run() {
                    try {
                        com.sun.javafx.runtime.NativeLibLoader.loadLibrary("msvcr100");
                    } catch (Throwable t) {
			// TODO: Suppress this message when we are ready for production
			// release or guard it with a verbose flag.
			// For now we want this message to be always on in our testing
			// cycle.
			System.err.println("Error: failed to msvcr100.dll " + t);
                    }
                    return null;
                }
            });
        }

        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                // Get the javafx.version and javafx.runtime.version from a preconstructed
                // java class, VersionInfo, created at build time.
                VersionInfo.setupSystemProperties();
                return null;
            }
        });

        boolean userSpecifiedToolkit = true;

        // Check a system property to see if there is a specific toolkit to use.
        // This is not a doPriviledged check so that applets cannot use this.
        String forcedToolkit = null;
        try {
            forcedToolkit = System.getProperty("javafx.toolkit");
        } catch (SecurityException ex) {}

        if (forcedToolkit == null) {
            forcedToolkit = tk;
        }
        if (forcedToolkit == null) {
            userSpecifiedToolkit = false;
            forcedToolkit = getDefaultToolkit();
        }

        if (forcedToolkit.indexOf(".") == -1) {
            // Turn a short name into a fully qualified classname
            forcedToolkit = lookupToolkitClass(forcedToolkit);
        }

        boolean verbose = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
            public Boolean run() {
                return Boolean.getBoolean("javafx.verbose");
            }
        });

        boolean printToolkit = verbose
                || (userSpecifiedToolkit && !forcedToolkit.endsWith("StubToolkit"));

        try {
            TOOLKIT = (Toolkit) Class.forName(forcedToolkit).newInstance();
            if (TOOLKIT.init()) {
                if (printToolkit) {
                    System.err.println("JavaFX: using " + forcedToolkit);
                }
                return TOOLKIT;
            }
            TOOLKIT = null;
        } catch (Exception any) {
            TOOLKIT = null;
            any.printStackTrace();
        }

        throw new RuntimeException("No toolkit found");
    }

    protected static Thread getFxUserThread() {
        return fxUserThread;
    }

    protected static void setFxUserThread(Thread t) {
        if (fxUserThread != null) {
            throw new IllegalStateException("Error: FX User Thread already initialized");
        }

        fxUserThread = t;
    }

    public void checkFxUserThread() {
        // Throw exception if not on FX user thread
        if (!isFxUserThread()) {
            throw new IllegalStateException("Not on FX application thread; currentThread = "
                    + Thread.currentThread().getName());
        }
    }

    // Toolkit can override this if needed
    public boolean isFxUserThread() {
        return Thread.currentThread() == fxUserThread;
    }

    protected Toolkit() {
        com.sun.scenario.ToolkitAccessor.setInstance(
             new com.sun.scenario.ToolkitAccessor() {
                 public Map<Object, Object> getContextMapImpl() {
                     return Toolkit.this.getContextMap();
                 }

                 public AbstractMasterTimer getMasterTimerImpl() {
                     return Toolkit.this.getMasterTimer();
                 }
             });
    }

    /**
     * Returns a PlatformLogger of a given name.
     *
     * @param name the name of the PlatformLogger to get
     */
    public PlatformLogger getLogger(String name) {
        /*
         * PlatformLogger is full of statics. So, we use PlatformLogger in this
         * manner to limit the amount of mutable static state in the public API.
         */
        return PlatformLogger.getLogger(name);
    }

    public abstract boolean init();

    /**
     * Enter a nested event loop and block until the corresponding
     * exitNestedEventLoop call is made.
     * The key passed into this method is used to
     * uniquely identify the matched enter/exit pair. This method creates a
     * new nested event loop and blocks until the corresponding
     * exitNestedEventLoop method is called with the same key.
     * The return value of this method will be the {@code rval}
     * object supplied to the exitNestedEventLoop method call that unblocks it.
     *
     * @param key the Object that identifies the nested event loop, which
     * must not be null
     *
     * @throws IllegalArgumentException if the specified key is associated
     * with a nested event loop that has not yet returned
     *
     * @throws NullPointerException if the key is null
     *
     * @throws IllegalStateException if this method is called on a thread
     * other than the FX Application thread
     *
     * @return the value passed into the corresponding call to exitEventLoop
     */
    public abstract Object enterNestedEventLoop(Object key);

    /**
     * Exit a nested event loop and unblock the caller of the
     * corresponding enterNestedEventLoop.
     * The key passed into this method is used to
     * uniquely identify the matched enter/exit pair. This method causes the
     * nested event loop that was previously created with the key to exit and
     * return control to the caller. If the specified nested event loop is not
     * the inner-most loop then it will not return until all other inner loops
     * also exit.
     *
     * @param key the Object that identifies the nested event loop, which
     * must not be null
     *
     * @param rval an Object that is returned to the caller of the
     * corresponding enterNestedEventLoop. This may be null.
     *
     * @throws IllegalArgumentException if the specified key is not associated
     * with an active nested event loop
     *
     * @throws NullPointerException if the key is null
     *
     * @throws IllegalStateException if this method is called on a thread
     * other than the FX Application thread
     */
    public abstract void exitNestedEventLoop(Object key, Object rval);

    public abstract TKStage createTKStage(StageStyle stageStyle);
    public abstract TKStage createTKStage(StageStyle stageStyle, boolean primary,
            Modality modality, TKStage owner);

    public abstract TKStage createTKPopupStage(StageStyle stageStyle, Object owner);
    public abstract TKStage createTKEmbeddedStage(HostInterface host);

    private final Map<TKPulseListener,Object> stagePulseListeners =
            new WeakHashMap<TKPulseListener,Object>();
    private final Map<TKPulseListener,Object> scenePulseListeners =
            new WeakHashMap<TKPulseListener,Object>();
    private final Map<TKListener,Object> toolkitListeners =
            new WeakHashMap<TKListener,Object>();

    // The set of shutdown hooks is strongly held to avoid premature GC.
    private final Set<Runnable> shutdownHooks = new HashSet<Runnable>();
    
    private final ArrayList<TKPulseListener> stagePulseList = new ArrayList<TKPulseListener>();
    private final ArrayList<TKPulseListener> scenePulseList = new ArrayList<TKPulseListener>();

    public void firePulse() {
        // Stages need to be notified of pulses before scenes so the Stage can resized 
        // and those changes propogated to scene before it gets its pulse to update
        
        try { 
            synchronized (this) {
                stagePulseList.addAll(stagePulseListeners.keySet());
                scenePulseList.addAll(scenePulseListeners.keySet());
            }
            for (TKPulseListener listener: stagePulseList) {
                listener.pulse();
            }
            for (TKPulseListener listener: scenePulseList) {
                listener.pulse();
            }
            if (lastTkPulseListener != null) {
                lastTkPulseListener.pulse();
            }
        } finally {
            stagePulseList.clear();
            scenePulseList.clear();
        }
    }
    public void addStageTkPulseListener(TKPulseListener listener) {
        synchronized (this) {
            stagePulseListeners.put(listener, null);
        }
    }
    public void removeStageTkPulseListener(TKPulseListener listener) {
        synchronized (this) {
            stagePulseListeners.remove(listener);
        }
    }
    public void addSceneTkPulseListener(TKPulseListener listener) {
        synchronized (this) {
            scenePulseListeners.put(listener, null);
        }
    }
    public void removeSceneTkPulseListener(TKPulseListener listener) {
        synchronized (this) {
            scenePulseListeners.remove(listener);
        }
    }

    public void addTkListener(TKListener listener) {
        toolkitListeners.put(listener, null);
    }

    public void removeTkListener(TKListener listener) {
        toolkitListeners.remove(listener);
    }

    private TKPulseListener lastTkPulseListener = null;
    public void setLastTkPulseListener(TKPulseListener listener) {
        lastTkPulseListener = listener;
    }

    public void addShutdownHook(Runnable hook) {
        synchronized (shutdownHooks) {
            shutdownHooks.add(hook);
        }
    }

    public void removeShutdownHook(Runnable hook) {
        synchronized (shutdownHooks) {
            shutdownHooks.remove(hook);
        }
    }

    protected void notifyShutdownHooks() {
        List<Runnable> hooks;
        synchronized (shutdownHooks) {
            hooks = new ArrayList<Runnable>(shutdownHooks);
            shutdownHooks.clear();
        }

        for (Runnable hook : hooks) {
            hook.run();
        }
    }

    public void notifyWindowListeners(List<TKStage> windows) {
        for (TKListener listener: toolkitListeners.keySet()) {
            listener.changedTopLevelWindows(windows);
        }
    }

    /**
     * Call this to make sure there is a pulse scheduled soon, this is needed by thing that reily on pulse events like
     * layout and stage sizing and need pulse events even when there is no running annimation
     */
    @Deprecated
    public void triggerNextPulse() {
        getMasterTimer().notifyJobsReady();
    }

    // notify the pulse timer code that we need the next pulse to happen
    // this flag is cleared each cycle so subsequent pulses must be requested
    public abstract void requestNextPulse();

    public InputStream getInputStream(String url, Class base)
            throws IOException {
        return (url.startsWith("http:")
                    || url.startsWith("https:")
                    || url.startsWith("file:")
                    || url.startsWith("jar:"))
                        ? new java.net.URL(url).openStream()
                        : base.getResource(url).openStream();
    }

    public abstract ImageLoader loadImage(String url,
                                          int width, int height,
                                          boolean preserveRatio,
                                          boolean smooth);
    public abstract ImageLoader loadImage(InputStream stream,
                                          int width, int height,
                                          boolean preserveRatio,
                                          boolean smooth);
    public abstract AsyncOperation loadImageAsync(
                                          AsyncOperationListener<? extends ImageLoader> listener,
                                          String url,
                                          int width, int height,
                                          boolean preserveRatio,
                                          boolean smooth);
    
    /*
     * The loadPlatformImage method supports the following image types:
     *   - an object returned by the renderToImage method
     *   - an instance of com.sun.prism.Image (in case of prism)
     *   - an instance of an external image object, which can be a BufferedImage 
     * If JavaFX Image had one more constructor Image(ImageLoader), 
     * we could introduce a different method for external image loading support.
     */
    
    public abstract ImageLoader loadPlatformImage(Object platformImage);

    // Indicates the default state of smooth for ImageView and MediaView
    // Subclasses may override this to provide a platform-specific default
    public boolean getDefaultImageSmooth() { return true; }

    public abstract void startup(Runnable runnable);
    public abstract void defer(Runnable runnable);
    public void exit() {
        fxUserThread = null;
    }

    public abstract Map<Object, Object> getContextMap();
    public abstract int getRefreshRate();
    public abstract void setAnimationRunnable(DelayedRunnable animationRunnable);
    public abstract PerformanceTracker getPerformanceTracker();
    public abstract PerformanceTracker createPerformanceTracker();

    //to be used for testing only
    public abstract void waitFor(Task t);

    public abstract PerspectiveCameraImpl createPerspectiveCamera();
    public abstract ParallelCameraImpl createParallelCamera();

    private Object checkSingleColor(List<Stop> stops) {
        if (stops.size() == 2) {
            Color c = stops.get(0).getColor();
            if (c.equals(stops.get(1).getColor())) {
                return c.impl_getPlatformPaint();
            }
        }
        return null;
    }

    private Object getPaint(LinearGradient paint) {
        Object p = gradientMap.get(paint);
        if (p != null) {
            return p;
        }
        p = checkSingleColor(paint.getStops());
        if (p == null) {
            p = createLinearGradientPaint(paint);
        }
        gradientMap.put(paint, p);
        return p;
    }

    private Object getPaint(RadialGradient paint) {
        Object p = gradientMap.get(paint);
        if (p != null) {
            return p;
        }
        p = checkSingleColor(paint.getStops());
        if (p == null) {
            p = createRadialGradientPaint(paint);
        }
        gradientMap.put(paint, p);
        return p;
    }

    public Object getPaint(Paint paint) {
        if (paint instanceof Color) {
            return createColorPaint((Color) paint);
        }

        if (paint instanceof LinearGradient) {          
            return getPaint((LinearGradient) paint);
        }

        if (paint instanceof RadialGradient) {
            return getPaint((RadialGradient) paint);
        }

        if (paint instanceof ImagePattern) {
            return createImagePatternPaint((ImagePattern) paint);
        }

        return null;
    }

    protected static final double clampStopOffset(double offset) {
        return (offset > 1.0) ? 1.0 :
               (offset < 0.0) ? 0.0 : offset;
    }

    protected abstract Object createColorPaint(Color paint);
    protected abstract Object createLinearGradientPaint(LinearGradient paint);
    protected abstract Object createRadialGradientPaint(RadialGradient paint);
    protected abstract Object createImagePatternPaint(ImagePattern paint);

    public abstract void
        accumulateStrokeBounds(com.sun.javafx.geom.Shape shape,
                               float bbox[],
                               PGShape.StrokeType type,
                               double strokewidth,
                               PGShape.StrokeLineCap cap,
                               PGShape.StrokeLineJoin join,
                               float miterLimit,
                               BaseTransform tx);

    public abstract boolean
        strokeContains(com.sun.javafx.geom.Shape shape,
                       double x, double y,
                       PGShape.StrokeType type,
                       double strokewidth,
                       PGShape.StrokeLineCap cap,
                       PGShape.StrokeLineJoin join,
                       float miterLimit);

    public abstract com.sun.javafx.geom.Shape
        createStrokedShape(com.sun.javafx.geom.Shape shape,
                           PGShape.StrokeType type,
                           double strokewidth,
                           PGShape.StrokeLineCap cap,
                           PGShape.StrokeLineJoin join,
                           float miterLimit,
                           float[] dashArray,
                           float dashOffset);

    public abstract int getKeyCodeForChar(String character);
    public abstract MouseEvent convertMouseEventToFX(Object event);
    public abstract KeyEvent convertKeyEventToFX(Object event);
    public abstract DragEvent convertDragRecognizedEventToFX(Object event, Dragboard dragboard);
    public abstract DragEvent convertDragSourceEventToFX(Object event, Dragboard dragboard);
    public abstract DragEvent convertDropTargetEventToFX(Object event, Dragboard dragboard);
    public abstract InputMethodEvent convertInputMethodEventToFX(Object event);
    public abstract Dimension2D getBestCursorSize(int preferredWidth, int preferredHeight);
    public abstract int getMaximumCursorColors();
    public abstract PathElement[] convertShapeToFXPath(Object shape);
    public abstract HitInfo convertHitInfoToFX(Object hit);

    public abstract Filterable toFilterable(Image img);
    public abstract FilterContext getFilterContext(Object config);

    public abstract boolean isForwardTraversalKey(KeyEvent e);
    public abstract boolean isBackwardTraversalKey(KeyEvent e);

    public abstract AbstractMasterTimer getMasterTimer();

    public abstract FontLoader getFontLoader();

    public abstract PGArc createPGArc();
    public abstract PGCircle createPGCircle();
    public abstract PGCubicCurve createPGCubicCurve();
    public abstract PGEllipse createPGEllipse();
    public abstract PGLine createPGLine();
    public abstract PGPath createPGPath();
    public abstract PGSVGPath createPGSVGPath();
    public abstract PGPolygon createPGPolygon();
    public abstract PGPolyline createPGPolyline();
    public abstract PGQuadCurve createPGQuadCurve();
    public abstract PGRectangle createPGRectangle();
    public abstract PGImageView createPGImageView();
    public abstract PGMediaView createPGMediaView();
    public abstract PGGroup createPGGroup();
    public abstract PGText createPGText();
    public abstract PGRegion createPGRegion();

    public abstract Object createSVGPathObject(SVGPath svgpath);
    public abstract Path2D createSVGPath2D(SVGPath svgpath);

    public abstract TextHelper createTextHelper(Text text);

    /**
     * Tests whether the pixel on the given coordinates in the given image
     * is non-empty (not fully transparent). Return value is not defined
     * for pixels out of the image bounds.
     */
    public abstract boolean imageContains(Object image, float x, float y);

    public abstract TKClipboard getSystemClipboard();
    
    public abstract TKSystemMenu getSystemMenu();
    
    public abstract TKClipboard getNamedClipboard(String name);

    /**
     * Creates a default, empty dragboard for use in drag and drop operations.
     */
    public abstract Dragboard createDragboard();

    public boolean isSupported(ConditionalFeature feature) { return false; }

    public abstract ScreenConfigurationAccessor setScreenConfigurationListener(TKScreenConfigurationListener listener);

    public abstract Object getPrimaryScreen();

    public abstract List<?> getScreens();

    public abstract void registerDragGestureListener(TKScene s, Set<TransferMode> tm, TKDragGestureListener l);

    /**
     * This function is called when a drag originates within a JavaFX application.
     * This means that drags that originate in other applications / from the OS
     * do not call this function.
     * The argument o represents an object used to identify a scene on which
     * the drag has started.
     */
    public abstract void startDrag(Object o, Set<TransferMode> tm, TKDragSourceListener l, Dragboard dragboard);

    // template function which can be implemented by toolkit impls such that they
    // can be informed when a drag and drop operation has completed. This allows
    // for any cleanup that may need to be done.
    public void stopDrag(Dragboard dragboard) {
        // no-op
    }

    public abstract void enableDrop(TKScene s, TKDropTargetListener l);

    public interface Task {
        boolean isFinished();
    }

    public Color4f toColor4f(Color color) {
        return new Color4f((float)color.getRed(), (float)color.getGreen(), (float)color.getBlue(), (float)color.getOpacity());
    }


    public ShadowMode toShadowMode(BlurType blurType) {
        switch (blurType) {
            case ONE_PASS_BOX:
                return ShadowMode.ONE_PASS_BOX;
            case TWO_PASS_BOX:
                return ShadowMode.TWO_PASS_BOX;
            case THREE_PASS_BOX:
                return ShadowMode.THREE_PASS_BOX;
            default:
                return ShadowMode.GAUSSIAN;
        }
    }

    public abstract void installInputMethodRequests(TKScene scene, InputMethodRequests requests);
    
    /*
     * ImageRenderingContext holds the many parameters passed to
     * the renderToImage method.
     * The use of the parameters is specified by the renderToImage
     * method.
     * @see #renderToImage
     */
    public static class ImageRenderingContext {
        public double width;
        public double height;
        public double scale;
        public boolean depthBuffer;
        public com.sun.javafx.sg.PGNode root;
        public Object platformPaint;
        public com.sun.javafx.geom.CameraImpl camera;
        public Object platformImage;
    }

    /*
     * This method renders a PG-graph to a platform image object.
     * The returned object can be turned into a useable
     * scene graph image using the appropriate factor of the
     * Image class.
     * The scale specified in the params is used to scale the
     * entire rendering before any transforms in the nodes are
     * applied.
     * The width and height specified in the params represent
     * the user space dimensions to be rendered.  The returned
     * image will be large enough to hold these dimensions
     * scaled by the scale parameter.
     * The depthBuffer specified in the params is used to determine
     * with or without depthBuffer rendering should be performed.
     * The root node is the root of a tree of toolkit-specific
     * scene graph peer nodes to be rendered and should have
     * been previously created by this toolkit.
     * The platformPaint specified in the params must be
     * generated by the appropriate Toolkit.createPaint method
     * and is used to fill the background of the image before
     * rendering the scene graph.
     * The platformImage specified in the params may be non-null
     * and should be a previous return value from this method.
     * If it is non-null then it may be reused as the return value
     * of this method if it is still valid and large enough to
     * hold the requested size.
     *
     * @param context a ImageRenderingContext instance specifying
     *               the various rendering parameters
     * @return a platform specific image object
     * @see javafx.scene.image.Image#impl_fromPlatformImage
     */

    public abstract Object renderToImage(ImageRenderingContext context);

    /*
     * This method indicates whether conversion to/from the specified external 
     * format is possible.
     */
    public abstract boolean isExternalFormatSupported(Class format);
    
    /*
     * Exports platformImage as external image
     * @param platformImg - source platformImage image
     * @param imgType - class or image template for export
     * @return external image if such export supported, null otherwise
     */
    public abstract Object toExternalImage(Object platformImg, Object imgType);

    /**
     * Returns the key code for the key which is commonly used on the
     * corresponding platform as a modifier key in shortcuts. For example
     * it is {@code KeyCode.CONTROL} on Windows (Ctrl + C, Ctrl + V ...) and
     * {@code KeyCode.META} on MacOS (Cmd + C, Cmd + V ...).
     *
     * @return the key code for shortcut modifier key
     */
    public abstract KeyCode getPlatformShortcutKey();
    
    public abstract List<File> showFileChooser(
            TKStage ownerWindow,
            String title,
            File initialDirectory,
            FileChooserType fileChooserType,
            List<ExtensionFilter> extensionFilters);

    public abstract File showDirectoryChooser(
            TKStage ownerWindow,
            String title,
            File initialDirectory);

    /*
     * Methods for obtaining "double-click" speed value.
     */
    public abstract long getMultiClickTime();
    public abstract int getMultiClickMaxX();
    public abstract int getMultiClickMaxY();

    private CountDownLatch pauseScenesLatch = null;

    /*
     * Causes all scenes to stop by removing its TKPulseListener from Toolkit.
     * It is used by Scenegraph-JMX bean.
     */
    public void pauseScenes() {
        pauseScenesLatch = new CountDownLatch(1);
        Iterator<Window> i = Window.impl_getWindows();
        while (i.hasNext()) {
            final Window w = i.next();
            final Scene scene = w.getScene();
            if (scene != null) {
                this.removeSceneTkPulseListener(scene.impl_getScenePulseListener());
            }
        }
        this.getMasterTimer().pause();
    }

    /*
     * Resume all scenes by registering its TKPulseListener back to Toolkit.
     * It is used by Scenegraph-JMX bean.
     */
    public void resumeScenes() {
        this.getMasterTimer().resume();
        Iterator<Window> i = Window.impl_getWindows();
        while (i.hasNext()) {
            final Window w = i.next();
            final Scene scene = w.getScene();
            if (scene != null) {
                this.addSceneTkPulseListener(scene.impl_getScenePulseListener());
            }
        }
        pauseScenesLatch.countDown();
        pauseScenesLatch = null;
    }

    /**
     * Used to pause current thread when the Scene-graph is in "PAUSED" mode.
     * It is mainly used by {@link javafx.application.Platform#runLater(Runnable)}
     * to block the thread and not to pass the runnable to FX event queue.
     */
    public void pauseCurrentThread() {
        final CountDownLatch cdl = pauseScenesLatch;
        if (cdl == null) {
            return;
        }
        try {
            cdl.await();
        } catch (InterruptedException e) { }
    }

    private Set<HighlightRegion> highlightRegions;

    /**
     * Getter for the set of regions to be highlighted using the JMX tooling 
     * interface.
     * 
     * @return the set of regions to be highlighted.
     */
    public Set<HighlightRegion> getHighlightedRegions() {
        if (highlightRegions == null) {
            highlightRegions = new HashSet<HighlightRegion>();
        }
        return highlightRegions;
    }
}
