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

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.jogamp.JoglAwtGraphics.JoglAwtDisplayMode;
import com.badlogic.gdx.utils.Clipboard;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.jogamp.opengl.awt.GLCanvas;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class JoglAwtApplication extends JoglApplicationBase {

    Frame frame;

    /**
     * Creates a new {@link JoglAwtApplication} with the given title and dimensions.
     *
     * @param listener the ApplicationListener implementing the program logic
     * @param title    the title of the application
     * @param width    the width of the surface in pixels
     * @param height   the height of the surface in pixels
     */
    public JoglAwtApplication(final ApplicationListener listener, final String title, final int width, final int height) {
        this(listener, new JoglAwtApplicationConfiguration(title, width, height));
    }

    public JoglAwtApplication(final ApplicationListener listener, final JoglAwtApplicationConfiguration config) {
        super(listener, config);
        initialize(listener, config);
    }

    protected void initialize(final ApplicationListener listener, final JoglAwtApplicationConfiguration config) {
        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                if (!config.fullscreen) {
                    frame = new Frame(config.title);
                    ((JoglAwtGraphics) graphics).getCanvas().setPreferredSize(new Dimension(config.width, config.height));
                    frame.setSize(config.width + frame.getInsets().left + frame.getInsets().right, frame.getInsets().top
                            + frame.getInsets().bottom + config.height);
                    frame.add(((JoglAwtGraphics) graphics).getCanvas(), BorderLayout.CENTER);
                    frame.setLocationRelativeTo(null);
                    frame.addWindowListener(windowListener);

                    frame.pack();
                    frame.setVisible(true);
                } else {
                    frame = new Frame(config.title);
                    ((JoglAwtGraphics) graphics).getCanvas().setPreferredSize(new Dimension(config.width, config.height));
                    frame.setSize(config.width + frame.getInsets().left + frame.getInsets().right, frame.getInsets().top
                            + frame.getInsets().bottom + config.height);
                    frame.add(((JoglAwtGraphics) graphics).getCanvas(), BorderLayout.CENTER);
                    frame.setLocationRelativeTo(null);
                    frame.addWindowListener(windowListener);
                    frame.setUndecorated(true);
                    frame.setResizable(true);
                    frame.setAlwaysOnTop(true);
                    frame.pack();
                    frame.setVisible(true);

                    GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
                    GraphicsDevice device = getScreen(frame, genv);

                    java.awt.DisplayMode desktopMode = device.getDisplayMode();
                    try {
                        device.setFullScreenWindow(frame);
                        JoglAwtDisplayMode mode = ((JoglAwtGraphics) graphics).findBestMatch(config.width, config.height);
                        if (mode == null)
                            throw new GdxRuntimeException("Couldn't set fullscreen mode " + config.width + "x" + config.height);
                        device.setDisplayMode(mode.mode);

                        Rectangle bounds = device.getDefaultConfiguration().getBounds();
                        frame.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);

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
        if (EventQueue.isDispatchThread()) {
            runnable.run();
        } else {
            try {
                EventQueue.invokeAndWait(runnable);
            } catch (Throwable t) {
                throw new GdxRuntimeException("Creating window failed", t);
            }
        }
    }

    private GraphicsDevice getScreen(Frame frame, GraphicsEnvironment genv) {
        Point point = frame.getLocation();

        GraphicsDevice[] allScreens = genv.getScreenDevices();
        for (int i = 0; i < allScreens.length; i++) {
            Rectangle bounds = allScreens[i].getDefaultConfiguration().getBounds();
            if (bounds.x < point.x && bounds.x + bounds.width > point.x) {
                return allScreens[i];
            }
        }
        return allScreens[0];
    }

    final WindowAdapter windowListener = new WindowAdapter() {
        @Override
        public void windowOpened(WindowEvent arg0) {
            ((JoglAwtGraphics) graphics).getCanvas().requestFocus();
            ((JoglAwtGraphics) graphics).getCanvas().requestFocusInWindow();
        }

        @Override
        public void windowIconified(WindowEvent arg0) {
        }

        @Override
        public void windowDeiconified(WindowEvent arg0) {
        }

        @Override
        public void windowClosing(WindowEvent arg0) {
            graphics.setContinuousRendering(true);
            graphics.pause();
            graphics.destroy();
            audio.dispose();
            frame.setVisible(false);
            frame.remove(((JoglAwtGraphics) graphics).getCanvas());
            frame.dispose();
        }
    };

    /**
     * @return the drawable of the application.
     */
    @Override
    public GLCanvas getGLCanvas() {
        return ((JoglAwtGraphics) graphics).getCanvas();
    }

    /**
     * @return the Frame of the application.
     */
    public Frame getFrame() {
        return frame;
    }

    @Override
    public Clipboard getClipboard() {
        return new JoglAwtClipboard();
    }

    @Override
    protected JoglAwtGraphics createGraphics(ApplicationListener listener, JoglApplicationConfiguration config) {
        return new JoglAwtGraphics(listener, (JoglAwtApplicationConfiguration) config);
    }

    @Override
    protected Input createInput(JoglGraphicsBase graphics) {
        return new JoglAwtInput(((JoglAwtGraphics) graphics).getCanvas());
    }

    @Override
    public void exit() {
        postRunnable(new Runnable() {
            @Override
            public void run() {
                frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
                JoglAwtApplication.super.exit();
            }
        });
    }
}
