package com.lei.wanandroid.jetpack.livedata;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class LiveDataHelper {

    public static void hookObserver(LiveData o, @NonNull Observer observer) throws Exception {
        //get wrapper's version
        Class<LiveData> classLiveData = LiveData.class;
        Field fieldObservers = classLiveData.getDeclaredField("mObservers");
        fieldObservers.setAccessible(true);
        Object objectObservers = fieldObservers.get(o);
        Class<?> classObservers = objectObservers.getClass();
        Method methodGet = classObservers.getDeclaredMethod("get", Object.class);
        methodGet.setAccessible(true);
        Object objectWrapperEntry = methodGet.invoke(objectObservers, observer);
        Object objectWrapper = null;
        if (objectWrapperEntry instanceof Map.Entry) {
            objectWrapper = ((Map.Entry) objectWrapperEntry).getValue();
        }
        if (objectWrapper == null) {
            throw new NullPointerException("Wrapper can not be bull!");
        }
        Class<?> classObserverWrapper = objectWrapper.getClass().getSuperclass();
        Field fieldLastVersion = classObserverWrapper.getDeclaredField("mLastVersion");
        fieldLastVersion.setAccessible(true);
        //get livedata's version
        Field fieldVersion = classLiveData.getDeclaredField("mVersion");
        fieldVersion.setAccessible(true);
        Object objectVersion = fieldVersion.get(o);
        //set wrapper's version
        fieldLastVersion.set(objectWrapper, objectVersion);
    }
}

