#include "com_andrea_wearami_MainActivity.h"

#include "peiskernel_mt.h"
#include "peiskernel.h"

JNIEXPORT void JNICALL Java_com_andrea_wearami_MainActivity_peiskernel_1init
  (JNIEnv *env, jobject obj){
    char args[0] = "a";
    peiskmt_initialize(0,args);
}


JNIEXPORT void JNICALL Java_com_andrea_wearami_MainActivity_peiskernel_1setTuple
        (JNIEnv *env, jobject obj, jstring key, jstring data){
  const char *keyString = (*env)->GetStringUTFChars(env, key, 0);
  const char *dataString = (*env)->GetStringUTFChars(env, data, 0);
  peiskmt_setStringTuple(keyString,dataString);
  return;
}


JNIEXPORT jstring JNICALL Java_com_andrea_wearami_MainActivity_peiskernel_1getTuple
        (JNIEnv *env, jobject obj, jint owner, jstring key) {
  char *keyString = (*env)->GetStringUTFChars(env, key, 0);
  peiskmt_subscribe(owner, keyString);

  while(peiskmt_isRunning()) {
    PeisTuple *tuple = peiskmt_getTuple(owner, keyString, NULL);
    if (tuple != NULL) {
      char *buf = tuple->data;
      return (*env)->NewStringUTF(env, buf);
    }
  }
}


JNIEXPORT jstring JNICALL Java_com_andrea_wearami_MainActivity_peiskernel_1getTupleByAbstract
        (JNIEnv *env, jobject obj, jstring key){

  char *keystring = (*env)->GetStringUTFChars(env, key,0);
  int lenght = (*env)->GetStringUTFLength(env, key);
  char pattern[lenght];

  strcpy(pattern,keystring);

  PeisTupleResultSet *rs;
  rs=peisk_createResultSet();
  peisk_resultSetReset(rs);

  PeisTuple proto;
  PeisTuple *result;

  peisk_initAbstractTuple(&proto);
  proto.owner=-1;
  peisk_setTupleName(&proto, pattern);

  peiskmt_subscribeByAbstract(&proto);

  while(1){
    peisk_resultSetReset(rs);
    peisk_getTuplesByAbstract(&proto,rs);

    for(;peisk_resultSetNext(rs);){
      result=peisk_resultSetValue(rs);
      char *buf = result->data;
      return (*env)->NewStringUTF(env, buf);
    }

  }
}

