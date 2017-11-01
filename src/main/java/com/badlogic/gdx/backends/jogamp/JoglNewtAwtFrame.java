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

import java.awt.Dimension;

import javax.swing.JFrame;

import com.badlogic.gdx.ApplicationListener;

@SuppressWarnings("unused")
public class JoglNewtAwtFrame extends JFrame {
	
	final JoglNewtAwtCanvas joglNewtAwtCanvas;

	public JoglNewtAwtFrame(ApplicationListener listener, String title, int width, int height) {
		super(title);
		
		joglNewtAwtCanvas = new JoglNewtAwtCanvas(listener, title, width, height) {
			protected void stopped () {
				JoglNewtAwtFrame.this.dispose();
			}

			protected void setTitle (String title) {
				JoglNewtAwtFrame.this.setTitle(title);
			}

			protected void setDisplayMode (int width, int height) {
				JoglNewtAwtFrame.this.getContentPane().setPreferredSize(new Dimension(width, height));
				JoglNewtAwtFrame.this.getContentPane().invalidate();
				JoglNewtAwtFrame.this.pack();
				JoglNewtAwtFrame.this.setLocationRelativeTo(null);
				updateSize(width, height);
			}

			protected void resize (int width, int height) {
				updateSize(width, height);
			}

			protected void start () {
				JoglNewtAwtFrame.this.start();
			}
		};
		getContentPane().add(joglNewtAwtCanvas.getCanvas());

		joglNewtAwtCanvas.setHaltOnShutdown(true);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		getContentPane().setPreferredSize(new Dimension(width, height));
		initialize();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		joglNewtAwtCanvas.getCanvas().requestFocus();
	}

	/** Called before the JFrame is shown. */
	protected void initialize () {
	}

	/** Called after {@link ApplicationListener} create and resize, but before the game loop iteration. */
	protected void start () {
	}

	/** Called when the canvas size changes. */
	public void updateSize (int width, int height) {
	}

	public JoglNewtAwtCanvas getJoglAWTCanvas () {
		return joglNewtAwtCanvas;
	}
}
