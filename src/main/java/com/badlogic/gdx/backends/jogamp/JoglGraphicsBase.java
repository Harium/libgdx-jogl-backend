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
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.jogamp.audio.OpenALAudio;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLCapabilitiesImmutable;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.Animator;

public abstract class JoglGraphicsBase implements Graphics, GLEventListener {
	static GLVersion glVersion;
	static int major, minor;

	ApplicationListener listener = null;
	boolean created = false;
	String extensions;
	volatile boolean isContinuous = true;
	volatile boolean requestRendering = false;
	volatile boolean cancelRendering = false;
	GLAutoDrawable canvas;
	Animator animator;
	long frameStart = System.nanoTime();
	long lastFrameTime = System.nanoTime();
	float deltaTime = 0;
	int fps;
	int frames;
	boolean paused = true, disposed = false;
	JoglApplicationConfiguration config;

	long frameId = -1;
	GL20 gl20;
	GL30 gl30;


	void initialize (ApplicationListener listener, JoglApplicationConfiguration config) {
		if (listener == null) throw new GdxRuntimeException("RenderListener must not be null");
		this.listener = listener;
		this.config = config;

		GLCapabilities caps;
		if(config.useGL30){
			caps = new GLCapabilities(GLProfile.getMaxProgrammable(true));
		} else {
			// GL20 uses glDrawElements and glVertexAttribPointer
			// passing buffers, these functions are removed in
			// OpenGL core only contexts.
			// libgdx shaders are currently only GLES2 and GL2
			// compatible.
			// try to allocate an GL2 or GLES2 context.
			// core only contexts are not supported for GL20.
			try {
				caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));
			} catch (GLException e) {
				caps = new GLCapabilities(GLProfile.get(GLProfile.GLES2));
			}
		}

		caps.setRedBits(config.r);
		caps.setGreenBits(config.g);
		caps.setBlueBits(config.b);
		caps.setAlphaBits(config.a);
		caps.setDepthBits(config.depth);
		caps.setStencilBits(config.stencil);
		caps.setNumSamples(config.samples);
		caps.setSampleBuffers(config.samples > 0);
		caps.setDoubleBuffered(true);

		canvas = createCanvas(caps);

		//canvas.setBackground(Color.BLACK);
		canvas.addGLEventListener(this);
	}

	protected abstract GLAutoDrawable createCanvas(final GLCapabilities caps);

	GLAutoDrawable getCanvas () {
		return canvas;
	}

	void create () {
		disposed = false;
		frameStart = System.nanoTime();
		lastFrameTime = frameStart;
		deltaTime = 0;
		animator = new Animator(canvas);
		animator.start();
	}

	void pause () {
		cancelRendering = true;
		synchronized (this) {
			paused = true;
		}
		// stop here if not yet fully initialized
		if (!created) return;
		animator.stop();
		if (!canvas.getContext().isCurrent()) {
		    canvas.getContext().makeCurrent();
		}
		listener.pause();
	}

	void resume () {
		paused = false;
		cancelRendering = false;
		if (!created) return;
		if (!canvas.getContext().isCurrent()) {
	    canvas.getContext().makeCurrent();
	  }
		listener.resume();
		frameStart = System.nanoTime();
		lastFrameTime = frameStart;
		deltaTime = 0;
		animator.resume();
		animator.setRunAsFastAsPossible(true);
		animator.start();
	}

	@Override
	public void init (GLAutoDrawable drawable) {
		initializeGLInstances(drawable);
		initGLVersion();
		setVSync(config.vSyncEnabled);

		if (!created) {
			listener.create();
			synchronized (this) {
				paused = false;
			}
			created = true;
		}
	}

	@Override
	public void reshape (GLAutoDrawable drawable, int x, int y, int width, int height) {
		listener.resize(width, height);
	}

	@Override
	public void display (GLAutoDrawable arg0) {
		synchronized (this) {
			if (!paused) {
				final boolean shouldRender = ((JoglApplicationBase)Gdx.app).executeRunnables() | shouldRender();
				if (shouldRender && !cancelRendering) {
					updateTime();
					((JoglInput) (Gdx.input)).processEvents();
					frameId++;
					listener.render();
					if (Gdx.audio != null) {
					    ((OpenALAudio) Gdx.audio).update();
					}
				}
			}
		}
	}

	void destroy () {
		if (!canvas.getContext().isCurrent()) {
		    canvas.getContext().makeCurrent();
		}

		pause();
		// prevent animator from touching the dying drawable context
		animator.remove(canvas);
		disposed = true;
		listener.dispose();
		canvas.destroy();
	}

	@Override
	public void setVSync (boolean vsync) {
		if (vsync)
			canvas.getGL().setSwapInterval(1);
		else
			canvas.getGL().setSwapInterval(0);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		setContinuousRendering(true);
		created = false;
	}

	@Override
	public boolean supportsExtension (String extension) {
		if (extensions == null) extensions = Gdx.gl.glGetString(GL20.GL_EXTENSIONS);
		return extensions.contains(extension);
	}

	@Override
	public void setContinuousRendering (boolean isContinuous) {
		this.isContinuous = isContinuous;
	}

	@Override
	public boolean isContinuousRendering () {
		return isContinuous;
	}

	@Override
	public void requestRendering () {
		synchronized (this) {
			requestRendering = true;
		}
	}

	public boolean shouldRender () {
		synchronized (this) {
			boolean rq = requestRendering;
			requestRendering = false;
			return rq || isContinuous /*|| isDirty()*/;
		}
	}

	@Override
	public BufferFormat getBufferFormat () {
		GLCapabilitiesImmutable caps = canvas.getChosenGLCapabilities();
		return new BufferFormat(caps.getRedBits(), caps.getGreenBits(), caps.getBlueBits(), caps.getAlphaBits(),
			caps.getDepthBits(), caps.getStencilBits(), caps.getNumSamples(), false);
	}

	void initializeGLInstances (GLAutoDrawable drawable) {
		major = drawable.getGL().getContext().getGLVersionNumber().getMajor();
		minor = drawable.getGL().getContext().getGLVersionNumber().getMinor();

		if (config.useGL30 && major >= 3) {
			gl30 = new JoglGL30();
			gl20 = gl30;
		} else {
			gl20 = new JoglGL20();
		}

		Gdx.gl = gl20;
		Gdx.gl20 = gl20;
		Gdx.gl30 = gl30;

		if (major <= 1)
			throw new GdxRuntimeException("OpenGL 2.0 or higher with the FBO extension is required. OpenGL version: " + major + "." + minor);
		if (major == 2 && !drawable.getGL().isGLES2Compatible()) {
			if (!supportsExtension("GL_EXT_framebuffer_object") && !supportsExtension("GL_ARB_framebuffer_object")) {
				final String vendor = drawable.getGL().glGetString(GL.GL_VENDOR);
				final String renderer = drawable.getGL().glGetString(GL.GL_RENDERER);
				final String version = drawable.getGL().glGetString(GL.GL_VERSION);
				final String glInfo = vendor + "\n" + renderer + "\n" + version;
				throw new GdxRuntimeException("OpenGL 2.0 or higher with the FBO extension is required. OpenGL version: " + major + "." + minor
					+ ", FBO extension: false" + (glInfo.isEmpty() ? "" : ("\n" + glInfo)));
			}
		}
	}

	@Override
	public float getPpiX () {
		return config.getScreenResolution();
	}

	@Override
	public float getPpiY () {
		return config.getScreenResolution();
	}

	@Override
	public float getPpcX () {
		return (config.getScreenResolution() / 2.54f);
	}

	@Override
	public float getPpcY () {
		return (config.getScreenResolution() / 2.54f);
	}

	@Override
	public float getDensity () {
		return (config.getScreenResolution() / 160f);
	}

	@Override
	public DisplayMode[] getDisplayModes () {
		return config.getDisplayModes();
	}

	void updateTime () {
		deltaTime = (System.nanoTime() - lastFrameTime) / 1000000000.0f;
		lastFrameTime = System.nanoTime();

		if (System.nanoTime() - frameStart > 1000000000) {
			fps = frames;
			frames = 0;
			frameStart = System.nanoTime();
		}
		frames++;
	}

	@Override
	public float getDeltaTime () {
		return deltaTime;
	}

	@Override
	public float getRawDeltaTime () {
		return deltaTime;
	}

	@Override
	public int getFramesPerSecond () {
		return fps;
	}

	@Override
	public GL20 getGL20 () {
		return gl20;
	}

	@Override
	public void setGL20 (GL20 gl20) {
		this.gl20 = gl20;
		if (gl30 == null) {
			Gdx.gl = gl20;
			Gdx.gl20 = gl20;
		}
	}

	@Override
	public GL30 getGL30 () {
		return gl30;
	}

	@Override
	public void setGL30 (GL30 gl30) {
		this.gl30 = gl30;
		if (gl30 != null) {
			this.gl20 = gl30;

			Gdx.gl = gl20;
			Gdx.gl20 = gl20;
			Gdx.gl30 = gl30;
		}
	}

	@Override
	public boolean isGL30Available () {
		return gl30 != null;
	}

	@Override
	public GraphicsType getType () {
		// TODO Change to JoGL when (if) it is available
		return GraphicsType.LWJGL;
	}

	@Override
	public long getFrameId() {
		return frameId;
	}

	@Override
	public int getBackBufferWidth() {
		return getWidth();
	}

	@Override
	public int getBackBufferHeight() {
		return getHeight ();
	}

	@Override
	public DisplayMode getDisplayMode() {
		return getDisplayMode(getMonitor());
	}

	@Override
	public GLVersion getGLVersion () {
		return glVersion;
	}

	private static void initGLVersion () {
		String versionString = GLContext.getCurrent().getGLVersionNumber().toString();
		String vendorString = GLContext.getCurrentGL().glGetString(GL.GL_VENDOR);
		String rendererString = GLContext.getCurrentGL().glGetString(GL.GL_RENDERER);
		glVersion = new GLVersion(Application.ApplicationType.Desktop, versionString, vendorString, rendererString);
	}
}
