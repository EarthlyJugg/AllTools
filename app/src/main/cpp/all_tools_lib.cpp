#include <jni.h>
#include <string>
#include<stdio.h>
#include<unistd.h>
#include<string.h>
#include<errno.h>

extern "C"
JNIEXPORT void JNICALLgetString(JNIEnv *env, jobject thiz) {
    int fd[2];
    int ret = pipe(fd);
    if (ret == -1) {
        perror("pipe error\n");
    }
//    write(fd[1],child,strlen(child)+1);
    pid_t id = fork();
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_lingtao_alltools_MainActivity_getNativeString(JNIEnv *env, jobject thiz) {
    return 0;
}