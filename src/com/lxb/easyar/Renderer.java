package com.lxb.easyar;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Created by Joker on 2017-06-21.
 */
public class Renderer implements GLSurfaceView.Renderer {
	private Context context;
	// �˴�������static
	private static ShowRedEnvelopeInterFace showRedEnvelopeInterFace;

	public Renderer(Context context,
			ShowRedEnvelopeInterFace showRedEnvelopeInterFace) {
		this.context = context;
		this.showRedEnvelopeInterFace = showRedEnvelopeInterFace;
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		EasyARUtil.nativeInitGL();
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		EasyARUtil.nativeResizeGL(w, h);
	}

	public void onDrawFrame(GL10 gl) {
		EasyARUtil.nativeRender();
	}

	public void showRedEnvelope(boolean tracked, String targetName) {
		System.out.println("----tracked:" + tracked);
		System.out.println("----targetName:" + targetName);
		showRedEnvelopeInterFace.isShowRedEnvelope(tracked, targetName);
		// return "";
	}

}
