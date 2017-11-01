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

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Clipboard;
import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;

/** An implementation of the {@link Application} interface based on Jogl for Windows, Linux and Mac. Instantiate this class with
 * appropriate parameters and then register {@link ApplicationListener} or {@link InputProcessor} instances.
 *
 * @author mzechner */
public class JoglNewtApplication extends JoglApplicationBase {

	/** Creates a new {@link JoglNewtApplication} with the given title and dimensions.
	 *
	 * @param listener the ApplicationListener implementing the program logic
	 * @param title the title of the application
	 * @param width the width of the surface in pixels
	 * @param height the height of the surface in pixels*/
	public JoglNewtApplication (final ApplicationListener listener, final String title, final int width, final int height) {
		this(listener, new JoglNewtApplicationConfiguration(title, width, height));
	}

	public JoglNewtApplication (final ApplicationListener listener) {
		this(listener, null, 640, 480);
	}

	public JoglNewtApplication (final ApplicationListener listener, final JoglNewtApplicationConfiguration config) {
		super(listener, config);
		final JoglNewtGraphics newtGraphics = (JoglNewtGraphics) graphics;
		newtGraphics.setResizable(config.resizable);
		newtGraphics.setUndecorated(config.undecorated);
		newtGraphics.addWindowListener(windowListener);
		newtGraphics.setVisible(true);
	}

	@Override
    protected JoglNewtGraphics createGraphics(ApplicationListener listener, JoglApplicationConfiguration config) {
		return new JoglNewtGraphics(listener, (JoglNewtApplicationConfiguration) config);
	}

	@Override
	protected Input createInput(JoglGraphicsBase graphics) {
		return new JoglNewtInput(((JoglNewtGraphics)graphics).getCanvas());
	}

	WindowAdapter windowListener = new WindowAdapter() {
		public void windowDestroyNotify(WindowEvent e) {
			end();
		}
	};

	/** @return the drawable of the application. */
	@Override
	public GLWindow getGLCanvas() {
		return ((JoglNewtGraphics)graphics).getCanvas();
	}

	@Override
	public Clipboard getClipboard() {
		return new JoglNewtClipboard();
	}
}
