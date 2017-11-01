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

import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.jogamp.JoglNewtGraphics.JoglNewtDisplayMode;
import com.jogamp.nativewindow.util.DimensionImmutable;
import com.jogamp.newt.Display;
import com.jogamp.newt.MonitorMode;
import com.jogamp.newt.NewtFactory;
import com.jogamp.newt.Screen;

public class JoglNewtApplicationConfiguration extends JoglApplicationConfiguration {

	public JoglNewtApplicationConfiguration() {
		super();
	}

	public JoglNewtApplicationConfiguration(final String title, final int width, final int height) {
		super(title, width, height);
	}

	@Override
	public DisplayMode[] getDisplayModes() {
        Display display = NewtFactory.createDisplay(null);
        Screen screen = NewtFactory.createScreen(display,0);
        screen.addReference();
        List<MonitorMode> screenModes = screen.getMonitorModes();
		DisplayMode[] displayModes = new DisplayMode[screenModes.size()];
		for (int modeIndex = 0 ; modeIndex < displayModes.length ; modeIndex++) {
			MonitorMode mode = screenModes.get(modeIndex);
			displayModes[modeIndex] = JoglNewtDisplayMode.from(mode);
		}
		screen.removeReference();
		return displayModes;
	}

	@Override
	public JoglNewtDisplayMode getDesktopDisplayMode () {
		Display display = NewtFactory.createDisplay(null);
        Screen screen = NewtFactory.createScreen(display,0);
        screen.addReference();
        MonitorMode mode = screen.getPrimaryMonitor().getCurrentMode();
        JoglNewtDisplayMode desktopMode = JoglNewtDisplayMode.from(mode);
        screen.removeReference();
        return desktopMode;
	}

	@Override
	public float getScreenResolution() {
		Display display = NewtFactory.createDisplay(null);
        Screen screen = NewtFactory.createScreen(display,0);
        screen.addReference();
		MonitorMode mmode = screen.getPrimaryMonitor().getCurrentMode();
		final DimensionImmutable sdim = screen.getPrimaryMonitor().getSizeMM();
		final DimensionImmutable spix = mmode.getSurfaceSize().getResolution();
        float screenResolution = (float)spix.getWidth() / (float)sdim.getWidth();
        screen.removeReference();
        return(screenResolution);
	}
}
