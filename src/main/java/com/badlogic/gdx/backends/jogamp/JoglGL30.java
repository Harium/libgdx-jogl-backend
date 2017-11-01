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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.jogamp.opengl.GLContext;

public class JoglGL30 extends JoglGL20 implements GL30 {

	@Override
	public void glReadBuffer (int mode) {		
		GLContext.getCurrentGL().getGL2ES3().glReadBuffer(mode);
	}

	@Override
	public void glDrawRangeElements (int mode, int start, int end, int count, int type, Buffer indices) {
		if(indices instanceof ByteBuffer) GLContext.getCurrentGL().getGL2().glDrawRangeElements(mode, start, end, count, type, indices);
		else if(indices instanceof ShortBuffer) GLContext.getCurrentGL().getGL2().glDrawRangeElements(mode, start, end, count, type, indices);
		else if(indices instanceof IntBuffer) GLContext.getCurrentGL().getGL2().glDrawRangeElements(mode, start, end, count, type, indices);
		else throw new GdxRuntimeException("indices must be byte, short or int buffer");
	}

	@Override
	public void glDrawRangeElements (int mode, int start, int end, int count, int type, int offset) {
		GLContext.getCurrentGL().getGL2ES3().glDrawRangeElements(mode, start, end, count, type, offset);
	}

	@Override
	public void glTexImage3D (int target, int level, int internalformat, int width, int height, int depth, int border, int format,
		int type, Buffer pixels) {
		if(pixels instanceof ByteBuffer) GLContext.getCurrentGL().getGL2ES2().glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, pixels);
		else throw new GdxRuntimeException("pixels must be byte buffer");
	}

	@Override
	public void glTexImage3D (int target, int level, int internalformat, int width, int height, int depth, int border, int format,
		int type, int offset) {
		GLContext.getCurrentGL().getGL2ES2().glTexImage3D(target, level, internalformat, width, height, depth, border, format, type, offset);
	}

	@Override
	public void glTexSubImage3D (int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth,
		int format, int type, Buffer pixels) {
		if(pixels instanceof ByteBuffer) GLContext.getCurrentGL().getGL2ES2().glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, pixels);
		else throw new GdxRuntimeException("pixels must be byte buffer");
	}

	@Override
	public void glTexSubImage3D (int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth,
		int format, int type, int offset) {
		GLContext.getCurrentGL().getGL2ES2().glTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, offset);
	}

	@Override
	public void glCopyTexSubImage3D (int target, int level, int xoffset, int yoffset, int zoffset, int x, int y, int width,
		int height) {
		GLContext.getCurrentGL().getGL2ES2().glCopyTexSubImage3D(target, level, xoffset, yoffset, zoffset, x, y, width, height);
	}

	@Override
	public void glGenQueries (int n, int[] ids, int offset) {
		GLContext.getCurrentGL().getGL2ES2().glGenQueries(n, ids, offset);
	}

	@Override
	public void glGenQueries (int n, IntBuffer ids) {
		GLContext.getCurrentGL().getGL2ES2().glGenQueries(n, ids);
	}

	@Override
	public void glDeleteQueries (int n, int[] ids, int offset) {
		GLContext.getCurrentGL().getGL2ES2().glGenQueries(n, ids, offset);
	}

	@Override
	public void glDeleteQueries (int n, IntBuffer ids) {
		GLContext.getCurrentGL().getGL2ES2().glGenQueries(n, ids);
	}

	@Override
	public boolean glIsQuery (int id) {
		return GLContext.getCurrentGL().getGL2ES2().glIsQuery(id);
	}

	@Override
	public void glBeginQuery (int target, int id) {
		GLContext.getCurrentGL().getGL2ES2().glBeginQuery(target, id);
	}

	@Override
	public void glEndQuery (int target) {
		GLContext.getCurrentGL().getGL2ES2().glEndQuery(target);
	}

	@Override
	public void glGetQueryiv (int target, int pname, IntBuffer params) {
		GLContext.getCurrentGL().getGL2ES2().glGetQueryiv(target, pname, params);
	}

	@Override
	public void glGetQueryObjectuiv (int id, int pname, IntBuffer params) {
		GLContext.getCurrentGL().getGL2ES2().glGetQueryObjectuiv(id, pname, params);
	}

	@Override
	public boolean glUnmapBuffer (int target) {
		return GLContext.getCurrentGL().glUnmapBuffer(target);
	}

	@Override
	public Buffer glGetBufferPointerv (int target, int pname) {
		//FIXME glGetBufferPointerv should be in GL4 like glGetNamedBufferPointerv
		//return GLContext.getCurrentGL().getGL4().glGetBufferPointerv(target, pname);
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public void glDrawBuffers (int n, IntBuffer bufs) {
		GLContext.getCurrentGL().getGL2ES2().glDrawBuffers(n, bufs);
	}

	@Override
	public void glUniformMatrix2x3fv (int location, int count, boolean transpose, FloatBuffer value) {
		GLContext.getCurrentGL().getGL2ES3().glUniformMatrix2x3fv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix3x2fv (int location, int count, boolean transpose, FloatBuffer value) {
		GLContext.getCurrentGL().getGL2ES3().glUniformMatrix3x2fv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix2x4fv (int location, int count, boolean transpose, FloatBuffer value) {
		GLContext.getCurrentGL().getGL2ES3().glUniformMatrix2x4fv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix4x2fv (int location, int count, boolean transpose, FloatBuffer value) {
		GLContext.getCurrentGL().getGL2ES3().glUniformMatrix4x2fv(location, count, transpose, value);
	}

	@Override
	public void glUniformMatrix3x4fv (int location, int count, boolean transpose, FloatBuffer value) {
		GLContext.getCurrentGL().getGL2ES3().glUniformMatrix3x4fv(location, count, transpose, value);
	}


	@Override
	public void glUniformMatrix4x3fv (int location, int count, boolean transpose, FloatBuffer value) {
		GLContext.getCurrentGL().getGL2ES3().glUniformMatrix4x3fv(location, count, transpose, value);
	}

	@Override
	public void glBlitFramebuffer (int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0, int dstX1, int dstY1,
		int mask, int filter) {
		GLContext.getCurrentGL().getGL2ES3().glBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
	}

	@Override
	public void glRenderbufferStorageMultisample (int target, int samples, int internalformat, int width, int height) {
		GLContext.getCurrentGL().glRenderbufferStorageMultisample(target, samples, internalformat, width, height);
	}

	@Override
	public void glFramebufferTexture2D (int target, int attachment, int textarget, int texture, int level) {
		GLContext.getCurrentGL().glFramebufferTexture2D(target, attachment, textarget, texture, level);
	}

	@Override
	public void glFramebufferRenderbuffer (int target, int attachment, int renderbuffertarget, int renderbuffer) {
		GLContext.getCurrentGL().glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer);
	}

	@Override
	public void glFramebufferTextureLayer (int target, int attachment, int texture, int level, int layer) {
		GLContext.getCurrentGL().getGL2ES3().glFramebufferTextureLayer(target, attachment, texture, level, layer);
	}

	@Override
	public void glFlushMappedBufferRange (int target, int offset, int length) {
		GLContext.getCurrentGL().glFlushMappedBufferRange(target, offset, length);
	}

	@Override
	public void glBindVertexArray (int array) {
		GLContext.getCurrentGL().getGL2ES3().glBindVertexArray(array);
	}

	@Override
	public void glDeleteVertexArrays (int n, int[] arrays, int offset) {
	    GLContext.getCurrentGL().getGL2ES3().glDeleteVertexArrays(n, arrays, offset);
	}

	@Override
	public void glDeleteVertexArrays (int n, IntBuffer arrays) {
		GLContext.getCurrentGL().getGL2ES3().glDeleteVertexArrays(n, arrays);
	}

	@Override
	public void glGenVertexArrays (int n, int[] arrays, int offset) {
		GLContext.getCurrentGL().getGL2ES3().glGenVertexArrays(n, arrays, offset);
	}

	@Override
	public void glGenVertexArrays (int n, IntBuffer arrays) {
		GLContext.getCurrentGL().getGL2ES3().glGenVertexArrays(n, arrays);
	}

	@Override
	public boolean glIsVertexArray (int array) {
		return GLContext.getCurrentGL().getGL2ES3().glIsVertexArray(array);
	}

	@Override
	public void glBeginTransformFeedback (int primitiveMode) {
		GLContext.getCurrentGL().getGL2ES3().glBeginTransformFeedback(primitiveMode);
	}

	@Override
	public void glEndTransformFeedback () {
		GLContext.getCurrentGL().getGL2ES3().glEndTransformFeedback();
	}

	@Override
	public void glBindBufferRange (int target, int index, int buffer, int offset, int size) {
		GLContext.getCurrentGL().getGL2ES3().glBindBufferRange(target, index, buffer, offset, size);
	}

	@Override
	public void glBindBufferBase (int target, int index, int buffer) {
		GLContext.getCurrentGL().getGL2ES3().glBindBufferBase(target, index, buffer);
	}

	@Override
	public void glTransformFeedbackVaryings (int program, String[] varyings, int bufferMode) {
		//FIXME count should be passed as an input in GL30
		final int count = varyings == null ? 0 : varyings.length;
		GLContext.getCurrentGL().getGL2ES3().glTransformFeedbackVaryings(program, count, varyings, bufferMode);
	}

	@Override
	public void glVertexAttribIPointer (int index, int size, int type, int stride, int offset) {
		GLContext.getCurrentGL().getGL2ES3().glVertexAttribIPointer(index, size, type, stride, offset);
	}

	@Override
	public void glGetVertexAttribIiv (int index, int pname, IntBuffer params) {
		GLContext.getCurrentGL().getGL2ES3().glGetVertexAttribIiv(index, pname, params);
	}

	@Override
	public void glGetVertexAttribIuiv (int index, int pname, IntBuffer params) {
		GLContext.getCurrentGL().getGL2ES3().glGetVertexAttribIuiv(index, pname, params);
	}

	@Override
	public void glVertexAttribI4i (int index, int x, int y, int z, int w) {
		GLContext.getCurrentGL().getGL2ES3().glVertexAttribI4i(index, x, y, z, w);
	}

	@Override
	public void glVertexAttribI4ui (int index, int x, int y, int z, int w) {
		GLContext.getCurrentGL().getGL2ES3().glVertexAttribI4ui(index, x, y, z, w);
	}
	
	@Override
	public void glGetUniformuiv (int program, int location, IntBuffer params) {
		GLContext.getCurrentGL().getGL2ES3().glGetUniformuiv(program, location, params);
	}

	@Override
	public int glGetFragDataLocation (int program, String name) {
		return GLContext.getCurrentGL().getGL2ES3().glGetFragDataLocation(program, name);
	}

	@Override
	public void glUniform1uiv (int location, int count, IntBuffer value) {
		GLContext.getCurrentGL().getGL2ES3().glUniform1uiv(location, count, value);
	}

	@Override
	public void glUniform3uiv (int location, int count, IntBuffer value) {
		GLContext.getCurrentGL().getGL2ES3().glUniform3uiv(location, count, value);
	}

	@Override
	public void glUniform4uiv (int location, int count, IntBuffer value) {
		GLContext.getCurrentGL().getGL2ES3().glUniform4uiv(location, count, value);
	}

	@Override
	public void glClearBufferiv (int buffer, int drawbuffer, IntBuffer value) {
		GLContext.getCurrentGL().getGL2ES3().glClearBufferiv(buffer, drawbuffer, value);
	}

	@Override
	public void glClearBufferuiv (int buffer, int drawbuffer, IntBuffer value) {
		GLContext.getCurrentGL().getGL2ES3().glClearBufferuiv(buffer, drawbuffer, value);
	}

	@Override
	public void glClearBufferfv (int buffer, int drawbuffer, FloatBuffer value) {
		GLContext.getCurrentGL().getGL2ES3().glClearBufferfv(buffer, drawbuffer, value);
	}

	@Override
	public void glClearBufferfi (int buffer, int drawbuffer, float depth, int stencil) {
		GLContext.getCurrentGL().getGL2ES3().glClearBufferfi(buffer, drawbuffer, depth, stencil);
	}

	@Override
	public String glGetStringi (int name, int index) {
		return GLContext.getCurrentGL().getGL2ES3().glGetStringi(name, index);
	}

	@Override
	public void glCopyBufferSubData (int readTarget, int writeTarget, int readOffset, int writeOffset, int size) {
		GLContext.getCurrentGL().getGL2ES3().glCopyBufferSubData(readTarget, writeTarget, readOffset, writeOffset, size);
	}

	@Override
	public void glGetUniformIndices (int program, String[] uniformNames, IntBuffer uniformIndices) {
		//FIXME uniformCount should be passed as an input in GL30
		final int uniformCount = uniformNames ==null ? 0 : uniformNames.length;
		GLContext.getCurrentGL().getGL2ES3().glGetUniformIndices(program, uniformCount, uniformNames, uniformIndices);
	}

	@Override
	public void glGetActiveUniformsiv (int program, int uniformCount, IntBuffer uniformIndices, int pname, IntBuffer params) {
		GLContext.getCurrentGL().getGL2ES3().glGetActiveUniformsiv(program, uniformCount, uniformIndices, pname, params);
	}

	@Override
	public int glGetUniformBlockIndex (int program, String uniformBlockName) {
		return GLContext.getCurrentGL().getGL2ES3().glGetUniformBlockIndex(program, uniformBlockName);
	}

	@Override
	public void glGetActiveUniformBlockiv (int program, int uniformBlockIndex, int pname, IntBuffer params) {
		GLContext.getCurrentGL().getGL2ES3().glGetActiveUniformBlockiv(program, uniformBlockIndex, pname, params);
	}

	@Override
	public void glGetActiveUniformBlockName (int program, int uniformBlockIndex, Buffer length, Buffer uniformBlockName) {
		//FIXME bufSize should be passed as an input in GL30
		final int bufSize = uniformBlockName.limit();
		GLContext.getCurrentGL().getGL2ES3().glGetActiveUniformBlockName(program, uniformBlockIndex, bufSize, (IntBuffer)length, (ByteBuffer)uniformBlockName);
	}

	@Override
	public String glGetActiveUniformBlockName (int program, int uniformBlockIndex) {
		//FIXME
		//return GLContext.getCurrentGL().getGL2ES3().glGetActiveUniformBlockName(program, uniformBlockIndex, 1024);
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public void glUniformBlockBinding (int program, int uniformBlockIndex, int uniformBlockBinding) {
		GLContext.getCurrentGL().getGL2ES3().glUniformBlockBinding(program, uniformBlockIndex, uniformBlockBinding);
	}

	@Override
	public void glDrawArraysInstanced (int mode, int first, int count, int instanceCount) {
		GLContext.getCurrentGL().getGL2ES3().glDrawArraysInstanced(mode, first, count, instanceCount);
	}

	@Override
	public void glDrawElementsInstanced (int mode, int count, int type, int indicesOffset, int instanceCount) {
		GLContext.getCurrentGL().getGL2ES3().glDrawElementsInstanced(mode, count, type, indicesOffset, instanceCount);
		
	}

	@Override
	public void glGetInteger64v (int pname, LongBuffer params) {
		GLContext.getCurrentGL().getGL3ES3().glGetInteger64v(pname, params);
	}

	@Override
	public void glGetBufferParameteri64v (int target, int pname, LongBuffer params) {
		GLContext.getCurrentGL().getGL3ES3().glGetBufferParameteri64v(target, pname, params);
	}

	@Override
	public void glGenSamplers (int count, int[] samplers, int offset) {
		GLContext.getCurrentGL().getGL3ES3().glGenSamplers(count, samplers, offset);
	}

	@Override
	public void glGenSamplers (int count, IntBuffer samplers) {
		GLContext.getCurrentGL().getGL3ES3().glGenSamplers(count, samplers);
	}

	@Override
	public void glDeleteSamplers (int count, int[] samplers, int offset) {
		GLContext.getCurrentGL().getGL3ES3().glDeleteSamplers(count, samplers, offset);
	}

	@Override
	public void glDeleteSamplers (int count, IntBuffer samplers) {
		GLContext.getCurrentGL().getGL3ES3().glDeleteSamplers(count, samplers);
	}

	@Override
	public boolean glIsSampler (int sampler) {
		return GLContext.getCurrentGL().getGL3ES3().glIsSampler(sampler);
	}

	@Override
	public void glBindSampler (int unit, int sampler) {
		GLContext.getCurrentGL().getGL3ES3().glBindSampler(unit, sampler);
	}

	@Override
	public void glSamplerParameteri (int sampler, int pname, int param) {
		GLContext.getCurrentGL().getGL3ES3().glSamplerParameteri(sampler, pname, param);
	}

	@Override
	public void glSamplerParameteriv (int sampler, int pname, IntBuffer param) {
		GLContext.getCurrentGL().getGL3ES3().glSamplerParameteriv(sampler, pname, param);
	}

	@Override
	public void glSamplerParameterf (int sampler, int pname, float param) {
		GLContext.getCurrentGL().getGL3ES3().glSamplerParameterf(sampler, pname, param);
	}

	@Override
	public void glSamplerParameterfv (int sampler, int pname, FloatBuffer param) {
		GLContext.getCurrentGL().getGL3ES3().glSamplerParameterfv(sampler, pname, param);
	}

	@Override
	public void glGetSamplerParameteriv (int sampler, int pname, IntBuffer params) {
		GLContext.getCurrentGL().getGL3ES3().glGetSamplerParameteriv(sampler, pname, params);
	}

	@Override
	public void glGetSamplerParameterfv (int sampler, int pname, FloatBuffer params) {
		GLContext.getCurrentGL().getGL3ES3().glGetSamplerParameterfv(sampler, pname, params);
	}

	@Override
	public void glVertexAttribDivisor (int index, int divisor) {
		GLContext.getCurrentGL().getGL2ES3().glVertexAttribDivisor(index, divisor);
	}

	@Override
	public void glBindTransformFeedback (int target, int id) {
		GLContext.getCurrentGL().getGL2ES3().glBindTransformFeedback(target, id);
	}

	@Override
	public void glDeleteTransformFeedbacks (int n, int[] ids, int offset) {
		GLContext.getCurrentGL().getGL2ES3().glDeleteTransformFeedbacks(n, ids, offset);
	}

	@Override
	public void glDeleteTransformFeedbacks (int n, IntBuffer ids) {
		GLContext.getCurrentGL().getGL2ES3().glDeleteTransformFeedbacks(n, ids);
	}

	@Override
	public void glGenTransformFeedbacks (int n, int[] ids, int offset) {
		GLContext.getCurrentGL().getGL2ES3().glGenTransformFeedbacks(n, ids, offset);
	}

	@Override
	public void glGenTransformFeedbacks (int n, IntBuffer ids) {
		GLContext.getCurrentGL().getGL2ES3().glGenTransformFeedbacks(n, ids);
	}

	@Override
	public boolean glIsTransformFeedback (int id) {
		return GLContext.getCurrentGL().getGL2ES3().glIsTransformFeedback(id);
	}

	@Override
	public void glPauseTransformFeedback () {
		GLContext.getCurrentGL().getGL2ES3().glPauseTransformFeedback();
	}

	@Override
	public void glResumeTransformFeedback () {
		GLContext.getCurrentGL().getGL2ES3().glResumeTransformFeedback();
	}

	@Override
	public void glProgramParameteri (int program, int pname, int value) {
		GLContext.getCurrentGL().getGL2ES2().glProgramParameteri(program, pname, value);
	}

//	@Override
//	public void glInvalidateFramebuffer (int target, int numAttachments, int[] attachments, int offset) {
//		throw new UnsupportedOperationException("not implemented");
//	}

	@Override
	public void glInvalidateFramebuffer (int target, int numAttachments, IntBuffer attachments) {
		GLContext.getCurrentGL().getGL2ES3().glInvalidateFramebuffer(target, numAttachments, attachments);
	}

//	@Override
//	public void glInvalidateSubFramebuffer (int target, int numAttachments, int[] attachments, int offset, int x, int y,
//		int width, int height) {
//		throw new UnsupportedOperationException("not implemented");
//	}

	@Override
	public void glInvalidateSubFramebuffer (int target, int numAttachments, IntBuffer attachments, int x, int y, int width,
		int height) {
		GLContext.getCurrentGL().getGL2ES3().glInvalidateSubFramebuffer(target, numAttachments, attachments, x, y, width, height);
	}
	
}
