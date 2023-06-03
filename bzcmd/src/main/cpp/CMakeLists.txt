# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)

project("bzffmpegcmd")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        bzffmpegcmd

        SHARED

        cmdutilt/cmdutils.c;
        cmdutilt/ffmpeg_demux.c;
        cmdutilt/ffmpeg_filter.c;
        cmdutilt/ffmpeg_hw.c;
        cmdutilt/ffmpeg_mux.c;
        cmdutilt/ffmpeg_mux_init.c;
        cmdutilt/ffmpeg_opt.c;
        cmdutilt/objpool.c;
        cmdutilt/opt_common.c;
        cmdutilt/sync_queue.c;
        cmdutilt/thread_queue.c;
        cmdutilt/ffmpeg.c;

        cmdutilt/ffmpeg_cmd.c;
        cmdutilt/demo_test.c;
        )


# Searches for a specified prebuilt library and stores the path as a
# variable. Because system libraries are included in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.


find_library(log-lib log)
find_library(m-lib m)
find_library(z-lib z)

configure_file(${bzffmpegcmd_SOURCE_DIR}/lib/${ANDROID_ABI}/libbzffmpeg.so ${bzffmpegcmd_BINARY_DIR}/libbzffmpeg.so COPYONLY)


#ffmpeg
add_library(bzffmpeg-lib
        SHARED
        IMPORTED)
set_target_properties(bzffmpeg-lib
        PROPERTIES IMPORTED_LOCATION
        libbzffmpeg.so)


include_directories(
        ./
        ./include/${ANDROID_ABI}
        ./include/${ANDROID_ABI}/
        ./include/${ANDROID_ABI}/libavcodec
        ./include/${ANDROID_ABI}/libavdevice
        ./include/${ANDROID_ABI}/libavfilter
        ./include/${ANDROID_ABI}/libavformat
        ./include/${ANDROID_ABI}/libavutil
        ./include/${ANDROID_ABI}/libswresample
        ./include/${ANDROID_ABI}/libswscale
        ./include/${ANDROID_ABI}/libpostproc
        cmdutilt/ffmpeg_cmd.h
)


# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in the
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        bzffmpegcmd

        # Links the target library to the log library
        # included in the NDK.
        bzffmpeg-lib ${log-lib} ${m-lib} ${z-lib})
