//
// Created by Niharika3.Sharma on 22-06-2020.
//


#ifndef MASK_DETECTION_DEMO_MAIN_H
#define MASK_DETECTION_DEMO_MAIN_H


#endif //MASK_DETECTION_DEMO_MAIN_H

#include <stdarg.h>

#include <string>
#if !defined(_WIN32)
#include <sys/time.h>
#endif
#if defined(__ANDROID__)
#include <android/native_activity.h>
#include <jni.h>
#elif defined(__APPLE__)
extern "C" {
#include <objc/objc.h>
}  // extern "C"
#endif  // __ANDROID__

// Defined using -DTESTAPP_NAME=some_app_name when compiling this
// file.
#ifndef TESTAPP_NAME
#define TESTAPP_NAME "android_main"
#endif  // TESTAPP_NAME

namespace app_framework {

// Cross platform logging method.
// Implemented by android/android_main.cc or ios/ios_main.mm.
    void LogMessage(const char* format, ...);
    void LogMessageV(const char* format, va_list list);

// Platform-independent method to flush pending events for the main thread.
// Returns true when an event requesting program-exit is received.
    bool ProcessEvents(int msec);

// Returns a path to a file suitable for the given platform.
    std::string PathForResource();

#if defined(_WIN32)
    // Windows requires its own version of time-handling code.
int64_t WinGetCurrentTimeInMicroseconds();
#endif

// Returns the number of microseconds since the epoch.
    static int64_t GetCurrentTimeInMicroseconds() {
#if !defined(_WIN32)
        struct timeval now;
        gettimeofday(&now, nullptr);
        return now.tv_sec * 1000000LL + now.tv_usec;
#else
        return WinGetCurrentTimeInMicroseconds();
#endif
    }

// WindowContext represents the handle to the parent window.  It's type
// (and usage) vary based on the OS.
#if defined(__ANDROID__)
    typedef jobject WindowContext;  // A jobject to the Java Activity.
#elif defined(__APPLE__)
    typedef id WindowContext;  // A pointer to an iOS UIView.
#else
typedef void* WindowContext;  // A void* for any other environments.
#endif

#if defined(__ANDROID__)
// Get the JNI environment.
    JNIEnv* GetJniEnv();
// Get the activity.
    jobject GetActivity();
#endif  // defined(__ANDROID__)

// Returns a variable that describes the window context for the app. On Android
// this will be a jobject pointing to the Activity. On iOS, it's an id pointing
// to the root view of the view controller.
    WindowContext GetWindowContext();

// Run the given function on a detached background thread.
    void RunOnBackgroundThread(void* (*func)(void* data), void* data);

}  // namespace app_framework



