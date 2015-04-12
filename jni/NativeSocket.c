#include <jni.h>
#include<assert.h>
#include<stdlib.h>
#include<stdio.h>

#include"HTTPGet.h"

JNIEXPORT jobjectArray JNICALL native_socket_getStructArray(JNIEnv *env,
		jobject _obj) {
	const char *server = "143.205.176.132";
	const char *url =
			"/ftp/datasets/DASHDataset2014/BigBuckBunny/6sec/bunny_3858484bps/BigBuckBunny_6s16.m4s";
	struct simple_tcp_info *pstcp_info;
	int test_count = 5; // 测试次数

	pstcp_info = (struct simple_tcp_info*) malloc(
			test_count * sizeof(struct simple_tcp_info));

	jobjectArray tcpInfoArray = NULL;  //jobjectArray 为指针类型
	jclass clsTcpInfo = NULL; //jclass为指针类型
	jobject obj = NULL;

	//获取class对象
	clsTcpInfo = (*env)->FindClass(env, "commlab/weejang/demo/TCPInfo");
	//创建返回结构体对象
	tcpInfoArray = (*env)->NewObjectArray(env, test_count, clsTcpInfo, NULL);
	//获取类中变量定义
	//rtt ....
	jfieldID rtt = (*env)->GetFieldID(env, clsTcpInfo, "rtt", "I");
	jfieldID rttvar = (*env)->GetFieldID(env, clsTcpInfo, "rttvar", "I");
	jfieldID rcv_rtt = (*env)->GetFieldID(env, clsTcpInfo, "rcv_rtt", "I");
	jfieldID last_data_sent = (*env)->GetFieldID(env, clsTcpInfo,
			"last_data_sent", "I");
	jfieldID last_data_recv = (*env)->GetFieldID(env, clsTcpInfo,
			"last_data_recv", "I");
	jfieldID last_data_size = (*env)->GetFieldID(env, clsTcpInfo,
			"last_data_size", "I");

	//实例化操作
	jmethodID consID = (*env)->GetMethodID(env, clsTcpInfo, "<init>", "()V");

	//rtt,thoughtput测试
	if (HttpGet(server, url, pstcp_info, test_count) < 0) {
		//测试失败
		return NULL;
	}

	for (jint i = 0; i < test_count; i++) {
		struct simple_tcp_info *ptr_n = pstcp_info + i;

		obj = (*env)->NewObject(env, clsTcpInfo, consID);
		(*env)->SetIntField(env, obj, rtt, (jint)(ptr_n->rtt));
		(*env)->SetIntField(env, obj, rttvar, (jint)(ptr_n->rttvar));
		(*env)->SetIntField(env, obj, rcv_rtt, (jint)(ptr_n->rcv_rtt));
		(*env)->SetIntField(env, obj, last_data_sent,
				(jint)(ptr_n->last_data_sent));
		(*env)->SetIntField(env, obj, last_data_recv,
				(jint)(ptr_n->last_data_recv));
		(*env)->SetIntField(env, obj, last_data_size,
				(jint)(ptr_n->last_data_size));

		(*env)->SetObjectArrayElement(env, tcpInfoArray, i, obj);
	}

	free(pstcp_info);

	return tcpInfoArray;
}

static JNINativeMethod tiMethods[] = { { "native_socket_getStructArray",
		"()[Lcommlab/weejang/demo/TCPInfo;",
		(void*) native_socket_getStructArray }, };

/**
 * Register several native methods for one class
 */
static int registerNativeMethods(JNIEnv* env, const char* className,
		JNINativeMethod* nsMethods, int numMethods) {
	jclass clazz;
	clazz = (*env)->FindClass(env, className);
	if (clazz == NULL) {
		return JNI_FALSE;
	}
	if ((*env)->RegisterNatives(env, clazz, nsMethods, numMethods) < 0) {
		return JNI_FALSE;
	}
	return JNI_TRUE;
}

/**
 * Register native methods for all classes we know about
 */
static int registerNatives(JNIEnv *env) {

	if (!registerNativeMethods(env, "commlab/weejang/demo/TCPInfo", tiMethods,
			sizeof(tiMethods) / sizeof(tiMethods[0]))) {
		return JNI_FALSE;
	}
	return JNI_TRUE;
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
	JNIEnv* env = NULL;
	jint result = -1;
	if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_6) != JNI_OK) {
		return -1;
	}
	assert(env != NULL);
	if (!registerNatives(env)) {
		return -1;
	}
	/* success -- return valid version number */
	result = JNI_VERSION_1_6;
	return result;
}

