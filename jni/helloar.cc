/**
 * Copyright (c) 2015-2016 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
 * EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
 * and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
 */

#include <GLES2/gl2.h>
#include <jni.h>

#include "ar.hpp"
#include "easyar/augmenter.hpp"
#include "easyar/frame.hpp"
#include "easyar/matrix.hpp"
#include "easyar/target.hpp"
#include "renderer.hpp"

//#define JNIFUNCTION_NATIVE(sig) Java_com_hel_ar_HelSweepRedEnvelopActivity_##sig
#define JNIFUNCTION_NATIVE(sig) Java_com_lxb_easyar_EasyARUtil_##sig
//#define JNIFUNCTION_NATIVE(sig) Java_com_speed_arredpackage_ar_SweepRedPackageActivity_##sig
extern "C" {
JNIEXPORT jboolean JNICALL JNIFUNCTION_NATIVE(loadPicture(JNIEnv* env, jobject object, jstring s));
JNIEXPORT jboolean JNICALL JNIFUNCTION_NATIVE(isSweep(JNIEnv* env, jobject object, jboolean s));
JNIEXPORT jboolean JNICALL JNIFUNCTION_NATIVE(nativeInit(JNIEnv* env, jobject object));
JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeDestory(JNIEnv* env, jobject object));
JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeInitGL(JNIEnv* env, jobject object));
JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeResizeGL(JNIEnv* env, jobject object, jint w, jint h));
JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeRender(JNIEnv* env, jobject obj));
JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeRotationChange(JNIEnv* env, jobject obj, jboolean portrait));
};

namespace EasyAR {
namespace samples {

class HelloAR: public AR {
public:
	HelloAR();
	virtual void initGL();
	virtual void resizeGL(int width, int height);
	virtual void render(JNIEnv * env);
private:
	Vec2I view_size;
	Renderer renderer;

};

HelloAR::HelloAR() {
	view_size[0] = -1;
}

void HelloAR::initGL() {
	renderer.init();
	augmenter_ = Augmenter();
	augmenter_.attachCamera(camera_);
}

void HelloAR::resizeGL(int width, int height) {
	view_size = Vec2I(width, height);
}

void HelloAR::render(JNIEnv * env) {
	glClearColor(0.f, 0.f, 0.f, 1.f);
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	Frame frame = augmenter_.newFrame();
	if (view_size[0] > 0) {
		AR::resizeGL(view_size[0], view_size[1]);
		if (camera_ && camera_.isOpened())
			view_size[0] = -1;
	}
	augmenter_.setViewPort(viewport_);
	augmenter_.drawVideoBackground();
	glViewport(viewport_[0], viewport_[1], viewport_[2], viewport_[3]);
	bool tracked = false;
	for (int i = 0; i < frame.targets().size(); ++i) {
		AugmentedTarget::Status status = frame.targets()[i].status();
		if (status == AugmentedTarget::kTargetStatusTracked) {
			if (mIsSweep_) {
				tracked = true;
				jclass clazz = env->FindClass("com/lxb/easyar/Renderer");
				jobject obj1 = env->AllocObject(clazz);
				jmethodID id = env->GetMethodID(clazz, "showRedEnvelope",
						"(ZLjava/lang/String;)V");
				jstring param = env->NewStringUTF(
						frame.targets()[i].target().name());
				env->CallVoidMethod(obj1, id, tracked, param);
			}
			//http://www.cnblogs.com/tianshuai11/archive/2012/03/26/2477175.html
			/*Matrix44F projectionMatrix = getProjectionGL(camera_.cameraCalibration(), 0.2f, 500.f);
			 Matrix44F cameraview = getPoseGL(frame.targets()[i].pose());
			 ImageTarget target = frame.targets()[i].target().cast_dynamic<ImageTarget>();
			 renderer.render(projectionMatrix, cameraview, target.size());*/
		}
	}
	if (mIsSweep_) {
		if (!tracked) {
			jclass clazz = env->FindClass("com/lxb/easyar/Renderer");
			jobject obj1 = env->AllocObject(clazz);
			jmethodID id = env->GetMethodID(clazz, "showRedEnvelope",
					"(ZLjava/lang/String;)V");
			jstring param = env->NewStringUTF("");
			env->CallVoidMethod(obj1, id, tracked, param);
		}
	}
}

}
}
EasyAR::samples::HelloAR ar;

JNIEXPORT jboolean JNICALL JNIFUNCTION_NATIVE(nativeInit(JNIEnv*, jobject)){
bool status = ar.initCamera();
/*ar.loadFromJsonFile("targets.json", "argame");
 ar.loadFromJsonFile("targets.json", "idback");
 ar.loadAllFromJsonFile("targets2.json");
 ar.loadFromImage("namecard.jpg")*/;
status &= ar.start();
return status;
}
//loadPicture
JNIEXPORT jboolean JNICALL JNIFUNCTION_NATIVE(loadPicture(JNIEnv* env, jobject, jstring picPath)){
const char* s = env->GetStringUTFChars(picPath, NULL);
ar.loadFromSDImage(s,0);
env->ReleaseStringUTFChars(picPath, s);
return true;
}

JNIEXPORT jboolean JNICALL JNIFUNCTION_NATIVE(isSweep(JNIEnv* env, jobject, jboolean issweep)){
ar.setSweep(issweep);
return true;
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeDestory(JNIEnv*, jobject))
{
    ar.clear();
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeInitGL(JNIEnv*, jobject))
{
    ar.initGL();
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeResizeGL(JNIEnv*, jobject, jint w, jint h))
{
    ar.resizeGL(w, h);
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeRender(JNIEnv* env, jobject))
{
	ar.render(env);
}

JNIEXPORT void JNICALL JNIFUNCTION_NATIVE(nativeRotationChange(JNIEnv*, jobject, jboolean portrait))
{
	ar.setPortrait(portrait);
}
