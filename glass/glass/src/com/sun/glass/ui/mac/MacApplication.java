/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
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
package com.sun.glass.ui.mac;

import com.sun.glass.ui.*;
import com.sun.glass.ui.CommonDialogs.ExtensionFilter;
import com.sun.glass.ui.CommonDialogs.FileChooserResult;
import com.sun.glass.events.KeyEvent;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

final class MacApplication extends Application implements InvokeLaterDispatcher.InvokeLaterSubmitter {

    private native static void _initIDs();
    static {
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
                Application.loadNativeLibrary();
                return null;
            }
        });
        _initIDs();
    }

    private boolean isTaskbarApplication = false;
    private final InvokeLaterDispatcher invokeLaterDispatcher;

    MacApplication() {
        invokeLaterDispatcher = new InvokeLaterDispatcher(this);
        invokeLaterDispatcher.start();
    }

    private Menu appleMenu;

    native void _runLoop(ClassLoader classLoader, Runnable launchable,
                         boolean isTaskbarApplication);
    @Override
    protected void runLoop(final Runnable launchable) {
        isTaskbarApplication =
            AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
                public Boolean run() {
                    String taskbarAppProp = System.getProperty("glass.taskbarApplication");
                    return  !"false".equalsIgnoreCase(taskbarAppProp); 
                }
            });
        
        ClassLoader classLoader = MacApplication.class.getClassLoader();
        _runLoop(classLoader, launchable, isTaskbarApplication);
    }

    native private void _finishTerminating();
    @Override
    protected void finishTerminating() {
        setEventThread(null);
        _finishTerminating();

        super.finishTerminating();
    }

    // Called from the native code
    private void setEventThread() {
        setEventThread(Thread.currentThread());
    }

    native private Object _enterNestedEventLoopImpl();
    @Override protected Object _enterNestedEventLoop() {
        invokeLaterDispatcher.notifyEnteringNestedEventLoop();
        try {
            return _enterNestedEventLoopImpl();
        } finally {
            invokeLaterDispatcher.notifyLeftNestedEventLoop();
        }
    }

    native private void _leaveNestedEventLoopImpl(Object retValue);
    @Override protected void _leaveNestedEventLoop(Object retValue) {
        invokeLaterDispatcher.notifyLeavingNestedEventLoop();
        _leaveNestedEventLoopImpl(retValue);
    }

    public void installAppleMenu(MenuBar menubar) {
        this.appleMenu = createMenu("Apple");
        
        MenuItem quitMenu = createMenuItem("Quit "+getName(), new MenuItem.Callback() {
            @Override public void action() {
                Application.EventHandler eh = getEventHandler();
                if (eh != null) {
                    eh.handleQuitAction(Application.GetApplication(), System.nanoTime());
                }
            }
            @Override public void validate() {
            }
        }, 'q', KeyEvent.MODIFIER_COMMAND);
        this.appleMenu.add(quitMenu);

        menubar.add(this.appleMenu);
    }
    
    public Menu getAppleMenu() {
        return this.appleMenu;
    }

    @Override public void installDefaultMenus(MenuBar menubar) {
        installAppleMenu(menubar);
    }


    // FACTORY METHODS

    @Override public Window createWindow(Window owner, Screen screen, int styleMask) {
        return new MacWindow(owner, screen, styleMask);
    }
    
    final static long BROWSER_PARENT_ID = -1L;
    @Override public Window createWindow(long parent) {
        Window window = new MacWindow(parent);
        if (parent == BROWSER_PARENT_ID) {
            // Special case: a Mac embedded window, which is a parent to other child Windows.
            // Needs implicit view, with a layer that will be provided to the plugin
            window.setView(createView());
        }
        return window;
    }

    @Override public View createView() {
        return new MacView();
    }

    @Override public Cursor createCursor(int type) {
        return new MacCursor(type);
    }

    @Override public Cursor createCursor(int x, int y, Pixels pixels) {
        return new MacCursor(x, y, pixels);
    }

    @Override protected void staticCursor_setVisible(boolean visible) {
        MacCursor.setVisible_impl(visible);
    }

    @Override protected Size staticCursor_getBestSize(int width, int height) {
        return MacCursor.getBestSize_impl(width, height);
    }

    @Override public Pixels createPixels(int width, int height, ByteBuffer data) {
        return new MacPixels(width, height, data);
    }

    @Override public Pixels createPixels(int width, int height, IntBuffer data) {
        return new MacPixels(width, height, data);
    }

    @Override protected int staticPixels_getNativeFormat() {
        return MacPixels.getNativeFormat_impl();
    }

    @Override public Robot createRobot() {
        return new MacRobot();
    }

    @Override protected double staticScreen_getVideoRefreshPeriod() {
        return MacScreen.getVideoRefreshPeriod_impl();
    }
    
    @Override protected Screen staticScreen_getDeepestScreen() {
        return MacScreen.getDeepestScreen_impl();
    }
    
    @Override protected Screen staticScreen_getMainScreen() {
        return MacScreen.getMainScreen_impl();
    }

    @Override protected Screen staticScreen_getScreenForLocation(int x, int y) {
        return MacScreen.getScreenForLocation_impl(x, y);
    }

    @Override protected Screen staticScreen_getScreenForPtr(long screenPtr) {
        return MacScreen.getScreenForPtr_impl(screenPtr);
    }

    @Override protected List<Screen> staticScreen_getScreens() {
        return MacScreen.getScreens_impl();
    }

    @Override public Timer createTimer(Runnable runnable) {
        return new MacTimer(runnable);
    }

    @Override protected int staticTimer_getMinPeriod() {
        return MacTimer.getMinPeriod_impl();
    }

    @Override protected int staticTimer_getMaxPeriod() {
        return MacTimer.getMaxPeriod_impl();
    }

    @Override protected FileChooserResult staticCommonDialogs_showFileChooser(Window owner, String folder, String filename, String title, int type,
                                                     boolean multipleMode, ExtensionFilter[] extensionFilters) {
        //TODO: support FileChooserResult
        return new FileChooserResult(MacCommonDialogs.showFileChooser_impl(owner, folder, filename, title, type, multipleMode, extensionFilters), null);
    }

    @Override protected File staticCommonDialogs_showFolderChooser(Window owner, String folder, String title) {
        // TODO: handle owner, folder and title
        return MacCommonDialogs.showFolderChooser_impl();
    }

    @Override protected long staticView_getMultiClickTime() {
        return MacView.getMultiClickTime_impl();
    }

    @Override protected int staticView_getMultiClickMaxX() {
        return MacView.getMultiClickMaxX_impl();
    }

    @Override protected int staticView_getMultiClickMaxY() {
        return MacView.getMultiClickMaxY_impl();
    }

    @Override native protected void _invokeAndWait(Runnable runnable);

    private native void _submitForLaterInvocation(Runnable r);
    // InvokeLaterDispatcher.InvokeLaterSubmitter
    @Override public void submitForLaterInvocation(Runnable r) {
        _submitForLaterInvocation(r);
    }

    @Override protected void _invokeLater(Runnable runnable) {
        invokeLaterDispatcher.invokeLater(runnable);
    }

    @Override
    protected boolean _supportsTransparentWindows() {
        return true;
    }

    @Override protected boolean _supportsUnifiedWindows() {
        return true;
    }

    native protected String _getRemoteLayerServerName();
    public String getRemoteLayerServerName() {
        return _getRemoteLayerServerName();
    }
}
