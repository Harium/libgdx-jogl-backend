/*******************************************************************************
 * Copyright 2015 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.backends.jogamp;

import java.util.List;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Cursor.SystemCursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.jogamp.nativewindow.WindowClosingProtocol.WindowClosingMode;
import com.jogamp.newt.Display;
import com.jogamp.newt.MonitorDevice;
import com.jogamp.newt.MonitorMode;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;
import com.jogamp.newt.Window;
import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;

/** Implements the {@link Graphics} interface with Jogl.
 *
 * @author mzechner */
public class JoglNewtGraphics extends JoglGraphicsBase {
	/**
	 * TODO move most of the code into a separate NEWT JoglGraphicsBase implementation and into a NEWT JoglApplicationConfiguration implementation,
	 * implement getDesktopDisplayMode() and move getDisplayModes() into the latter
	 */
	final JoglNewtDisplayMode desktopMode;

	public JoglNewtGraphics (ApplicationListener listener, JoglNewtApplicationConfiguration config) {
		initialize(listener, config);
		getCanvas().getScreen().addReference();
		if (config.fullscreen) {
			setFullscreenMode(getDisplayMode(getMonitor()));
		} else {
			setWindowedMode(config.width, config.height);
		}
		setTitle(config.title);
		desktopMode = config.getDesktopDisplayMode();
	}

	protected GLWindow createCanvas(final GLCapabilities caps) {
		final GLWindow glwin = GLWindow.create(caps);
		glwin.setDefaultCloseOperation(WindowClosingMode.DO_NOTHING_ON_CLOSE);
		return glwin;
	}

	GLWindow getCanvas () {
		return (GLWindow) super.getCanvas();
	}

	@Override
	public int getHeight () {
		return getCanvas().getHeight();
	}

	@Override
	public int getWidth () {
		return getCanvas().getWidth();
	}

	@Override
	public void create() {
		super.create();
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void destroy () {
		getCanvas().setVisible(false);
		final Screen screen = getCanvas().getScreen();
		super.destroy();
		screen.removeReference();
	}

	@Override
	public boolean supportsDisplayModeChange () {
		return true;
	}

	@Override
	public void setTitle (String title) {
		getCanvas().setTitle(title);
	}

	@Override
	public boolean isFullscreen () {
		return getCanvas().isFullscreen();
	}

	@Override
	public void setCursor(Cursor cursor) {
		if (cursor == null) {
			getCanvas().setPointerIcon(null);
		} else {
			((JoglNewtCursor)cursor).setSystemCursor();
		}
	}

	@Override
	public Cursor newCursor(Pixmap pixmap, int xHotspot, int yHotspot) {
		return new JoglNewtCursor(pixmap, xHotspot, yHotspot, getCanvas());
	}

	@Override
	public Monitor getMonitor() {
		final Window window = getCanvas();
		if (window == null) return getPrimaryMonitor();
		return JoglNewtMonitor.from(window.getMainMonitor());
	}

	@Override
	public Monitor getPrimaryMonitor() {
		Display disp = NewtFactory.createDisplay(null);
		final Screen screen = Screen.getFirstScreenOf(disp, Screen.getActiveScreenNumber(), 0);
		screen.addReference();
		final Monitor monitor = JoglNewtMonitor.from(screen.getPrimaryMonitor());
		screen.removeReference();
		screen.destroy();
		disp.destroy();
		return monitor;
	}

	@Override
	public Monitor[] getMonitors() {
		Display disp = NewtFactory.createDisplay(null);
		final Screen screen = Screen.getFirstScreenOf(disp, Screen.getActiveScreenNumber(), 0);
		screen.addReference();
		final List <MonitorDevice> devices = screen.getMonitorDevices();
		screen.removeReference();
		screen.destroy();
		disp.destroy();

		final Monitor[] monitors = new Monitor[devices.size()];
		for (int i = 0; i < monitors.length; i++) {
			monitors[i] = JoglNewtMonitor.from(devices.get(i));
		}
		return monitors;
	}

	@Override
	public DisplayMode getDisplayMode(Monitor monitor) {
		if (!(monitor instanceof JoglNewtMonitor)) {
			throw new IllegalArgumentException("incompatible monitor type: " + monitor.getClass());
		}

		return JoglNewtDisplayMode.from(((JoglNewtMonitor) monitor).device.getCurrentMode());
	}

	@Override
	public DisplayMode[] getDisplayModes(Monitor monitor) {
		if (!(monitor instanceof JoglNewtMonitor)) {
			throw new IllegalArgumentException("incompatible monitor type: " + monitor.getClass());
		}

		final JoglNewtMonitor joglMonitor = (JoglNewtMonitor) monitor;
		final List<MonitorMode> monitorModes = joglMonitor.device.getSupportedModes();
		final DisplayMode[] displayModes = new DisplayMode[monitorModes.size()];
		for (int i = 0; i < displayModes.length; i++) {
			displayModes[i] = JoglNewtDisplayMode.from(monitorModes.get(i));
		}

		return displayModes;
	}

	@Override
	public boolean setFullscreenMode(DisplayMode displayMode) {
		return setFullscreenDisplayMode(displayMode);
	}

	@Override
	public boolean setWindowedMode(int width, int height) {
		return setWindowedDisplayMode(width, height, config.x, config.y);
	}

	@Override
	public void setSystemCursor(SystemCursor systemCursor) {
		// TODO ??
		getCanvas().setPointerIcon(null);
	}

	@Override
	public void setResizable(boolean resizable) {
		getCanvas().setResizable(resizable);
		this.config.resizable = resizable;
	}

	@Override
	public void setUndecorated(boolean undecorated) {
		getCanvas().setUndecorated(undecorated);
		this.config.undecorated = undecorated;
	}

	public void setPosition(int x, int y) {
		getCanvas().setPosition(x, y);
	}

	public void addWindowListener(WindowListener listener) {
		getCanvas().addWindowListener(listener);
	}

	public void removeWindowListener(WindowListener listener) {
		getCanvas().removeWindowListener(listener);
	}

	public void setVisible(boolean visible) {
		getCanvas().setVisible(visible);
	}

	private int getMonitorWidth () {
		final MonitorDevice device = ((JoglNewtMonitor)getMonitor()).device;
		return device.getViewport().getWidth();
	}

	private int getMonitorHeight () {
		final MonitorDevice device = ((JoglNewtMonitor)getMonitor()).device;
		return device.getViewport().getHeight();
	}

	private boolean setFullscreenDisplayMode (DisplayMode displayMode) {
		MonitorMode screenMode = ((JoglNewtDisplayMode)displayMode).mode;
		getCanvas().getMainMonitor().setCurrentMode(screenMode);
		getCanvas().setFullscreen(true);
		getCanvas().setPosition(0, 0);
		getCanvas().setSize(displayMode.width, displayMode.height);
		getCanvas().setUndecorated(true);
		if (Gdx.gl != null) Gdx.gl.glViewport(0, 0, displayMode.width, displayMode.height);
		config.width = displayMode.width;
		config.height = displayMode.height;
		// do a full window repaint; intended to help reduce flickering/fragmentation
		// after a configuration change on some systems
		getCanvas().windowRepaint(0, 0, displayMode.width, displayMode.height);

		return true;
	}

	private boolean setWindowedDisplayMode (int width, int height, int x, int y) {
		getCanvas().setFullscreen(false);
		getCanvas().setSize(width, height);
		if (x < 0 || y < 0) {
			int newX = (getMonitorWidth() - width) / 2;
		  int newY = (getMonitorHeight() - height) / 2;
		  getCanvas().setPosition(newX, newY);
		} else {
			getCanvas().setPosition(x, y);
		}
		if (Gdx.gl != null) Gdx.gl.glViewport(0, 0, width, height);
		config.width = width;
		config.height = height;
		// do a full window repaint; intended to help reduce flickering/fragmentation
		// after a configuration change on some systems
		getCanvas().windowRepaint(0, 0, width, height);

		return true;
	}

	protected static class JoglNewtDisplayMode extends DisplayMode {
		final MonitorMode mode;

		protected JoglNewtDisplayMode(int width, int height, int refreshRate, int bitsPerPixel, MonitorMode mode) {
			super(width, height, refreshRate, bitsPerPixel);
			this.mode = mode;
		}

		static JoglNewtDisplayMode from(MonitorMode mode) {
			return new JoglNewtDisplayMode(mode.getRotatedWidth(), mode.getRotatedHeight(), Math.round(mode.getRefreshRate()),
					mode.getSurfaceSize().getBitsPerPixel(), mode);
		}

		@Override
		public String toString() {
			return super.toString() + " | NEWT MonitorMode: " + mode.toString();
		}
	}

	protected static class JoglNewtMonitor extends Monitor {
		final MonitorDevice device;

		protected JoglNewtMonitor(int virtualX, int virtualY, String name, MonitorDevice device) {
			super(virtualX, virtualY, name);

			this.device = device;
		}

		static JoglNewtMonitor from(MonitorDevice device) {
			return new JoglNewtMonitor(device.getViewport().getX(), device.getViewport().getY(),
					String.valueOf(device.getId()), device);
		}
	}
}
