LOCAL_PATH_TOP := $(call my-dir)
#EASYAR_PACKAGE_PATH := $(LOCAL_PATH_TOP)/../../../../../../package
EASYAR_PACKAGE_PATH := $(LOCAL_PATH_TOP)../libs

include $(CLEAR_VARS)
LOCAL_PATH := $(LOCAL_PATH_TOP)/libs/armeabi
LOCAL_MODULE:=libEasyAR
LOCAL_SRC_FILES:=libEasyAR.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_PATH := $(LOCAL_PATH_TOP)
LOCAL_C_INCLUDES +=  EASYAR_PACKAGE_PATH
LOCAL_CPPFLAGS += -DANDROID
LOCAL_LDLIBS += -llog -lGLESv2
LOCAL_SHARED_LIBRARIES += libEasyAR
LOCAL_CPP_EXTENSION := .cc
LOCAL_MODULE := libHelloARNative
LOCAL_SRC_FILES := ar.cc helloar.cc renderer.cc
include $(BUILD_SHARED_LIBRARY)