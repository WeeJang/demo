LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_LDLIBS := -lm -llog
LOCAL_SHARED_LIBRARIES := liblog libcutils


LOCAL_MODULE := NativeSocket
LOCAL_SRC_FILES :=  NativeSocket.c 	HTTPGet.h  	HTTPGet.c 
											

ARCH=$(ANDROID_ABI)

LOCAL_CFLAGS := -std=gnu99
ifeq ($(ARCH), armeabi)
	LOCAL_CFLAGS += -DHAVE_ARMEABI
	# Needed by ARMv6 Thumb1 (the System Control coprocessor/CP15 is mandatory on ARMv6)
	# On newer ARM architectures we can use Thumb2
	LOCAL_ARM_MODE := arm
endif
ifeq ($(ARCH), armeabi-v7a)
	LOCAL_CFLAGS += -DHAVE_ARMEABI_V7A
endif


include $(BUILD_SHARED_LIBRARY)  
