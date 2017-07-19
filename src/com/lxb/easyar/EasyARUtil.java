package com.lxb.easyar;

import android.app.Activity;
import android.widget.Toast;
import cn.easyar.engine.EasyAR;

public class EasyARUtil {
	static String key = "GbRktIntVe0Eu6V4x4b111Qoj6CVLwTEwJ2BWL1bDtIbW9Vcn4DFQRNBd8jksA3YhFMPWWn84tfLS5va669zW5gWnGEjIOmX5dYU87075fded0dcba02ee81a6c08871826ei6CIC1yqGYRApYitEfttAGzXr8QSTM5dXI34TclDu0t1kkoTb6MFaV7YX9sMjE9DiJLV";

	static {
		System.loadLibrary("EasyAR");
		System.loadLibrary("HelloARNative");
	}

	public EasyARUtil() {
		// TODO Auto-generated constructor stub
	}

	private static EasyARUtil instance;

	public static EasyARUtil getInstance() {
		synchronized (EasyARUtil.class) {
			if (instance == null) {
				instance = new EasyARUtil();
			}
		}
		return instance;
	}

	public static native void nativeInitGL();

	public static native void nativeResizeGL(int w, int h);

	public static native void nativeRender();

	private native boolean nativeInit();

	private native void nativeDestory();

	private native void nativeRotationChange(boolean portrait);

	private native void nativeStop();

	private native void nativeStart();

	private native boolean loadPicture(String picPath);

	public static native boolean isSweep(boolean portrait);

	// 准备
	public void prepare(Activity activity) {
		try {
			EasyAR.initialize(activity, key);
		} catch (Throwable ex) {
			ex.printStackTrace();
			Toast.makeText(activity, "AR的SO库加载错误", Toast.LENGTH_SHORT).show();
			// 错误可能:1、key不对
		}
		nativeInit();
	}

	// true开始识别,false关闭识别
	public void setStatus(boolean b) {
		isSweep(b);
	}

	// 加载sd里的识别比对图片
	public void loadSDPath(String picPath) {
		loadPicture(picPath);
	}

	public void rotationChange(boolean portrait) {
		nativeRotationChange(portrait);
	}

	// activity销毁时候调用
	public void destory() {
		nativeDestory();
	}
}
