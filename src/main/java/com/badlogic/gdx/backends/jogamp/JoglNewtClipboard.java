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

import com.badlogic.gdx.utils.Clipboard;

/**
 * Clipboard based on NEWT. As this toolkit doesn't support this kind of feature yet, this class is a dummy implementation.
 * 
 * @author Julien Gouesse
 *
 */
public class JoglNewtClipboard implements Clipboard {
	
	public JoglNewtClipboard() {
		super();
	}

	@Override
	public String getContents() {
		return null;
	}

	@Override
	public void setContents(String content) {
	}

}
