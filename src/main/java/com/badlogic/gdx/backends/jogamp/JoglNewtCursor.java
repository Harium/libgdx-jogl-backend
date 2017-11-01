package com.badlogic.gdx.backends.jogamp;

import java.nio.ByteBuffer;

import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.jogamp.nativewindow.util.Dimension;
import com.jogamp.nativewindow.util.DimensionImmutable;
import com.jogamp.nativewindow.util.PixelFormat;
import com.jogamp.nativewindow.util.PixelRectangle;
import com.jogamp.newt.Display.PointerIcon;
import com.jogamp.newt.Window;

public class JoglNewtCursor implements Cursor {

	private Window window;

	private PointerIcon internalJoglNewtCursor;

	public JoglNewtCursor(Pixmap pixmap, int xHotspot, int yHotspot, Window window) {
		super();
		this.window = window;
		if (xHotspot < 0 || xHotspot >= pixmap.getWidth()) {
	        throw new GdxRuntimeException ("xHotspot coordinate of " + xHotspot  + " is not within image width bounds: [0, " + pixmap.getWidth() + ").");
	    }

	    if (yHotspot < 0 || yHotspot >= pixmap.getHeight()) {
	        throw new GdxRuntimeException ("yHotspot coordinate of " + yHotspot  + " is not within image height bounds: [0, " + pixmap.getHeight() + ").");
	    }
		final DimensionImmutable size = new Dimension(pixmap.getWidth(), pixmap.getHeight());
		PixelFormat pixFormat = null;
        switch(pixmap.getFormat()) {
        case Alpha:
        case Intensity:
        case LuminanceAlpha:
        case RGB565:
        case RGBA4444:
        	throw new GdxRuntimeException("invalid cursor pixmap format: " + pixmap.getFormat());
        case RGB888:
        	pixFormat = PixelFormat.RGB888;
        	break;
        case RGBA8888:
        	pixFormat = PixelFormat.RGBA8888;
        	break;
        }

    final ByteBuffer pixels = pixmap.getPixels();
    PixelRectangle.GenericPixelRect rec = new PixelRectangle.GenericPixelRect(pixFormat, size, 0, false, pixels);
		internalJoglNewtCursor = window.getScreen().getDisplay().createPointerIcon(rec, xHotspot, yHotspot);
	}

	public void setSystemCursor() {
		window.setPointerIcon(internalJoglNewtCursor);
	}

	@Override
	public void dispose() {
		internalJoglNewtCursor.destroy();
	}
}
