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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.jogamp.JoglAwtGraphics.JoglAwtDisplayMode;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.jogamp.opengl.awt.GLJPanel;

public class JoglSwingApplication extends JoglApplicationBase {

	JFrame frame;
	
	/** Creates a new {@link JoglSwingApplication} with the given title and dimensions.
	 * 
	 * @param listener the ApplicationListener implementing the program logic
	 * @param title the title of the application
	 * @param width the width of the surface in pixels
	 * @param height the height of the surface in pixels*/
	public JoglSwingApplication (final ApplicationListener listener, final String title, final int width, final int height) {
		this(listener, new JoglAwtApplicationConfiguration(title, width, height));
	}

	public JoglSwingApplication (final ApplicationListener listener, final JoglAwtApplicationConfiguration config) {
		super(listener, config);
		final Runnable runnable = new Runnable() {

			@Override
			public void run() {
				if (!config.fullscreen) {
					frame = new JFrame(config.title);
					((JoglSwingGraphics)graphics).getCanvas().setPreferredSize(new Dimension(config.width, config.height));
					frame.setSize(config.width + frame.getInsets().left + frame.getInsets().right, frame.getInsets().top
						+ frame.getInsets().bottom + config.height);
					frame.add(((JoglSwingGraphics)graphics).getCanvas(), BorderLayout.CENTER);
					frame.setLocationRelativeTo(null);
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					frame.addWindowListener(windowListener);
					frame.pack();
					frame.setVisible(true);
				} else {
					GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
					GraphicsDevice device = genv.getDefaultScreenDevice();
					frame = new JFrame(config.title);
					((JoglSwingGraphics)graphics).getCanvas().setPreferredSize(new Dimension(config.width, config.height));
					frame.setSize(config.width + frame.getInsets().left + frame.getInsets().right, frame.getInsets().top
						+ frame.getInsets().bottom + config.height);
					frame.add(((JoglSwingGraphics)graphics).getCanvas(), BorderLayout.CENTER);
					frame.setLocationRelativeTo(null);
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					frame.addWindowListener(windowListener);
					frame.setUndecorated(true);
					frame.setResizable(false);
					frame.pack();
					frame.setVisible(true);
					java.awt.DisplayMode desktopMode = device.getDisplayMode();
					try {
						device.setFullScreenWindow(frame);
						JoglAwtDisplayMode mode = ((JoglSwingGraphics)graphics).findBestMatch(config.width, config.height);
						if (mode == null)
							throw new GdxRuntimeException("Couldn't set fullscreen mode " + config.width + "x" + config.height);
						device.setDisplayMode(mode.mode);
					} catch (Throwable e) {
						e.printStackTrace();
						device.setDisplayMode(desktopMode);
						device.setFullScreenWindow(null);
						frame.dispose();
						audio.dispose();
						System.exit(-1);
					}
				}
			}
		};
		if (SwingUtilities.isEventDispatchThread()) {
			runnable.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(runnable);
			} catch (Throwable t) {
				throw new GdxRuntimeException("Creating window failed", t);
			}
		}
	}
	
	final WindowAdapter windowListener = new WindowAdapter() {
		@Override
		public void windowOpened (WindowEvent arg0) {
			((JoglSwingGraphics)graphics).getCanvas().requestFocus();
			((JoglSwingGraphics)graphics).getCanvas().requestFocusInWindow();
		}

		@Override
		public void windowIconified (WindowEvent arg0) {
		}

		@Override
		public void windowDeiconified (WindowEvent arg0) {
		}

		@Override
		public void windowClosing (WindowEvent arg0) {
			graphics.setContinuousRendering(true);
			graphics.pause();
			graphics.destroy();
			audio.dispose();
			frame.remove(((JoglSwingGraphics)graphics).getCanvas());
			frame.dispose();
		}
	};
	
	/** @return the drawable of the application. */
	@Override
	public GLJPanel getGLCanvas() {
		return ((JoglSwingGraphics)graphics).getCanvas();
	}
	
	/** @return the Frame of the application. */
	public JFrame getFrame () {
		return frame;
	}
	
	@Override
	public Clipboard getClipboard() {
		return new JoglAwtClipboard();
	}

	@Override
	protected JoglSwingGraphics createGraphics(ApplicationListener listener, JoglApplicationConfiguration config) {
		return new JoglSwingGraphics(listener, (JoglAwtApplicationConfiguration) config);
	}

	@Override
	protected Input createInput(JoglGraphicsBase graphics) {
		return new JoglAwtInput(((JoglSwingGraphics)graphics).getCanvas());
	}
	
	@Override
	public void exit () {
		postRunnable(new Runnable() {
			@Override
			public void run () {
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			}
		});
	}
}
