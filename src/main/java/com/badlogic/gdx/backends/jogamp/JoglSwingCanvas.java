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
import com.badlogic.gdx.utils.Clipboard;
import com.jogamp.opengl.awt.GLJPanel;

public class JoglSwingCanvas extends JoglApplicationBase {

	public JoglSwingCanvas(final ApplicationListener listener, final String title, final int width, final int height) {
		this(listener, title, width, height, null);
	}

	public JoglSwingCanvas(final ApplicationListener listener, final String title, final int width, final int height,
			JoglSwingCanvas shared) {
		this(listener, new JoglAwtApplicationConfiguration(title, width, height), null);
	}

	public JoglSwingCanvas(ApplicationListener listener, JoglAwtApplicationConfiguration config, JoglSwingCanvas shared) {
		super(listener, config);
		//FIXME take the shared canvas into account
	}

	@Override
	public GLJPanel getGLCanvas() {
		return ((JoglSwingGraphics) graphics).getCanvas();
	}

	@Override
	public Clipboard getClipboard() {
		return new JoglAwtClipboard();
	}

	@Override
	protected JoglGraphicsBase createGraphics(ApplicationListener listener, JoglApplicationConfiguration config) {
		return new JoglSwingGraphics(listener, (JoglAwtApplicationConfiguration) config);
	}

	@Override
	protected Input createInput(JoglGraphicsBase graphics) {
		return new JoglAwtInput(((JoglSwingGraphics) graphics).getCanvas());
	}

}