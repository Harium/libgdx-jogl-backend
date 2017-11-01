/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
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

package com.badlogic.gdx.backends.jogamp.audio;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.jogamp.common.nio.Buffers;
import com.jogamp.openal.ALConstants;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;

/** @author Nathan Sweet */
public abstract class OpenALMusic implements Music {
	static private final int bufferSize = 4096 * 10;
	static private final int bufferCount = 3;
	static private final int bytesPerSample = 2;
	static private final byte[] tempBytes = new byte[bufferSize];
	static private final ByteBuffer tempBuffer = Buffers.newDirectByteBuffer(bufferSize);

	private final OpenALAudio audio;
	private IntBuffer buffers;
	private IntBuffer ib = Buffers.newDirectIntBuffer(1);
	private FloatBuffer fb = Buffers.newDirectFloatBuffer(1);
	private int sourceID = -1;
	private int format, sampleRate;
	private boolean isLooping, isPlaying;
	private float volume = 1;
	private float pan = 0;
	private float renderedSeconds, secondsPerBuffer;

	protected final FileHandle file;
	protected int bufferOverhead = 0;

	private OnCompletionListener onCompletionListener;

	public OpenALMusic (OpenALAudio audio, FileHandle file) {
		this.audio = audio;
		this.file = file;
		this.onCompletionListener = null;
	}

	protected void setup (int channels, int sampleRate) {
		this.format = channels > 1 ? ALConstants.AL_FORMAT_STEREO16 : ALConstants.AL_FORMAT_MONO16;
		this.sampleRate = sampleRate;
		secondsPerBuffer = (float)(bufferSize - bufferOverhead)  / (bytesPerSample * channels * sampleRate);
	}

	public void play () {
		if (audio.noDevice) return;
		if (sourceID == -1) {
			sourceID = audio.obtainSource(true);
			if (sourceID == -1) return;

			audio.music.add(this);

			if (buffers == null) {
				buffers = Buffers.newDirectIntBuffer(bufferCount);
				audio.getAL().alGenBuffers(buffers.limit(), buffers);
				if (audio.getAL().alGetError() != ALConstants.AL_NO_ERROR) throw new GdxRuntimeException("Unabe to allocate audio buffers.");
			}
			audio.getAL().alSourcei(sourceID, ALConstants.AL_LOOPING, ALConstants.AL_FALSE);
			setPan(pan, volume);

			boolean filled = false; // Check if there's anything to actually play.
			for (int i = 0; i < bufferCount; i++) {
				int bufferID = buffers.get(i);
				if (!fill(bufferID)) break;
				filled = true;
				ib.put(0, bufferID).rewind();
				audio.getAL().alSourceQueueBuffers(sourceID, ib.limit(), ib);
			}
			if (!filled && onCompletionListener != null) onCompletionListener.onCompletion(this);
			
			if (audio.getAL().alGetError() != ALConstants.AL_NO_ERROR) {
				stop();
				return;
			}
		}
		if (!isPlaying) {
		    audio.getAL().alSourcePlay(sourceID);
		    isPlaying = true;
		}
	}

	public void stop () {
		if (audio.noDevice) return;
		if (sourceID == -1) return;
		audio.music.removeValue(this, true);
		reset();
		audio.freeSource(sourceID);
		sourceID = -1;
		renderedSeconds = 0;
		isPlaying = false;
	}

	public void pause () {
		if (audio.noDevice) return;
		if (sourceID != -1) audio.getAL().alSourcePause(sourceID);
		isPlaying = false;
	}

	public boolean isPlaying () {
		if (audio.noDevice) return false;
		if (sourceID == -1) return false;
		return isPlaying;
	}

	public void setLooping (boolean isLooping) {
		this.isLooping = isLooping;
	}

	public boolean isLooping () {
		return isLooping;
	}

	public void setVolume (float volume) {
		this.volume = volume;
		if (audio.noDevice) return;
		if (sourceID != -1) audio.getAL().alSourcef(sourceID, ALConstants.AL_GAIN, volume);
	}

	public float getVolume () {
		return this.volume;
	}

	public void setPan (float pan, float volume) {
		this.volume = volume;
		this.pan = pan;
		if (audio.noDevice) return;
		if (sourceID == -1) return;
		audio.getAL().alSource3f(sourceID, ALConstants.AL_POSITION, MathUtils.cos((pan - 1) * MathUtils.PI / 2), 0,
			MathUtils.sin((pan + 1) * MathUtils.PI / 2));
		audio.getAL().alSourcef(sourceID, ALConstants.AL_GAIN, volume);
	}

	public void setPosition (float position) {
		if (audio.noDevice) return;
		if (sourceID == -1) return;
		boolean wasPlaying = isPlaying;
		isPlaying = false;
		audio.getAL().alSourceStop(sourceID);
		audio.getAL().alSourceUnqueueBuffers(sourceID, buffers.limit(), buffers);
		renderedSeconds += (secondsPerBuffer * bufferCount);
		if (position <= renderedSeconds) {
			reset();
			renderedSeconds = 0;
		}
		while (renderedSeconds < (position - secondsPerBuffer)) {
			if (read(tempBytes) <= 0) break;
			renderedSeconds += secondsPerBuffer;
		}
		boolean filled = false;
		for (int i = 0; i < bufferCount; i++) {
			int bufferID = buffers.get(i);
			if (!fill(bufferID)) break;
			filled = true;
			ib.put(0, bufferID).rewind();
			audio.getAL().alSourceQueueBuffers(sourceID, ib.limit(), ib);
		}
		if (!filled) {
			stop();
			if (onCompletionListener != null) onCompletionListener.onCompletion(this);
		}
		audio.getAL().alSourcef(sourceID, ALConstants.AL_SEC_OFFSET, position - renderedSeconds);
		if (wasPlaying) {
			audio.getAL().alSourcePlay(sourceID);
			isPlaying = true;
		}
	}

	public float getPosition () {
		if (audio.noDevice) return 0;
		if (sourceID == -1) return 0;
		audio.getAL().alGetSourcef(sourceID, ALConstants.AL_SEC_OFFSET, fb);
		return renderedSeconds + fb.get(0);
	}

	/** Fills as much of the buffer as possible and returns the number of bytes filled. Returns <= 0 to indicate the end of the
	 * stream. */
	abstract public int read (byte[] buffer);

	/** Resets the stream to the beginning. */
	abstract public void reset ();

	/** By default, does just the same as reset(). Used to add special behaviour in Ogg.Music. */
	protected void loop () {
		reset();
	}

	public int getChannels () {
		return format == ALConstants.AL_FORMAT_STEREO16 ? 2 : 1;
	}

	public int getRate () {
		return sampleRate;
	}

	public void update () {
		if (audio.noDevice) return;
		if (sourceID == -1) return;

		boolean end = false;
		audio.getAL().alGetSourcei(sourceID, ALConstants.AL_BUFFERS_PROCESSED, ib);
		int buffers = ib.get(0);
		while (buffers-- > 0) {
			ib.put(0, buffers).rewind();
			audio.getAL().alSourceUnqueueBuffers(sourceID, ib.limit(), ib);
			int bufferID = ib.get(0);
			if (bufferID == ALConstants.AL_INVALID_VALUE) break;
			renderedSeconds += secondsPerBuffer;
			if (end) continue;
			if (fill(bufferID))
				{ib.put(0, bufferID).rewind();
				 audio.getAL().alSourceQueueBuffers(sourceID, ib.limit(), ib);
				}
			else
				end = true;
		}
		if (end && isCurrentSourceWithNoBuffersQueued()) {
			stop();
			if (onCompletionListener != null) onCompletionListener.onCompletion(this);
		}

		// A buffer underflow will cause the source to stop.
		if (isPlaying && !isCurrentSourcePlaying()) audio.getAL().alSourcePlay(sourceID);
	}
	
	private boolean isCurrentSourceWithNoBuffersQueued() {
		audio.getAL().alGetSourcei(sourceID, ALConstants.AL_BUFFERS_QUEUED, ib);
		return(ib.get(0) == 0);
	}
	
	private boolean isCurrentSourcePlaying() {
		audio.getAL().alGetSourcei(sourceID, ALConstants.AL_SOURCE_STATE, ib);
		return(ib.get(0) == ALConstants.AL_PLAYING);
	}

	private boolean fill (int bufferID) {
		tempBuffer.clear();
		int length = read(tempBytes);
		if (length <= 0) {
			if (isLooping) {
				loop();
				renderedSeconds = 0;
				length = read(tempBytes);
				if (length <= 0) return false;
			} else
				return false;
		}
		tempBuffer.put(tempBytes, 0, length).flip();
		audio.getAL().alBufferData(bufferID, format, tempBuffer, tempBuffer.remaining(), sampleRate);
		return true;
	}

	public void dispose () {
		stop();
		if (audio.noDevice) return;
		if (buffers == null) return;
		audio.getAL().alDeleteBuffers(buffers.limit(), buffers);
		buffers = null;
		onCompletionListener = null;
	}
	
	@Override
	public void setOnCompletionListener (OnCompletionListener listener) {
		onCompletionListener = listener;
	}
	
	public int getSourceId () {
		return sourceID;
	}
}
