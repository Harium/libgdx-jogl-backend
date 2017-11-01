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

import java.awt.Canvas;
import java.awt.Cursor;

import com.badlogic.gdx.ApplicationListener;
import com.jogamp.newt.awt.NewtCanvasAWT;

/**
 * An OpenGL surface on a NEWT canvas linked to an AWT peer, allowing OpenGL to
 * be embedded in a Swing application
 *
 * @author Julien Gouesse
 *
 */
public class JoglNewtAwtCanvas extends JoglNewtApplication {

	private NewtCanvasAWT canvas;

	public JoglNewtAwtCanvas(final ApplicationListener listener, final String title, final int width, final int height) {
		this(listener, title, width, height, null);
	}

	public JoglNewtAwtCanvas(final ApplicationListener listener, final String title, final int width, final int height,
			JoglNewtAwtCanvas shared) {
		super(listener, title, width, height);
		// FIXME it is too early to get the canvas. It was working with JOGL 1 but
		// the context is available later with JOGL 2
		/*
		 * if (shared != null && shared.getGLCanvas().getContext() != null) {
		 * getGLCanvas().setSharedContext(shared.getGLCanvas().getContext()); }
		 */
		canvas = new NewtCanvasAWT(getGLCanvas());
	}

	@Override
	protected JoglNewtGraphics createGraphics(ApplicationListener listener, JoglApplicationConfiguration config) {
		return new JoglNewtGraphics(listener, (JoglNewtApplicationConfiguration) config) {

			@Override
			public void setTitle(String title) {
				super.setTitle(title);
				JoglNewtAwtCanvas.this.setTitle(title);
			}

			@Override
			public boolean setWindowedMode(int width, int height) {
				if (!super.setWindowedMode(width, height)) return false;
				return true;
			}

			@Override
			public boolean setFullscreenMode(DisplayMode displayMode) {
				if (!super.setFullscreenMode(displayMode))
					return false;
				JoglNewtAwtCanvas.this.setDisplayMode(displayMode.width, displayMode.height);
				return true;
			}
		};
	}

	protected void setDisplayMode(int width, int height) {
	}

	protected void setTitle(String title) {
	}

	public void setCursor(Cursor cursor) {
		canvas.setCursor(cursor);
	}

	public Canvas getCanvas() {
		return (canvas);
	}
}
