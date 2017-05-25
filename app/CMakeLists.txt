# Sets the minimum version of CMake required to build the native
# library. You should either keep the default value or only pass a
# value of 3.4.0 or lower.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds it for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
             ffmpegCmd

             SHARED


             src/main/cpp/cmdutilt/cmdutils.c;
             src/main/cpp/cmdutilt/ffmpeg.c;
             src/main/cpp/cmdutilt/ffmpeg_filter.c;
             src/main/cpp/cmdutilt/ffmpeg_opt.c;
             src/main/cpp/cmdutilt/ffmpeg_cmd.c;
            )



# Searches for a specified prebuilt library and stores the path as a
# variable. Because system libraries are included in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.


find_library(log-lib log)
find_library(m-lib m)
find_library(z-lib z)

#ffmpeg
add_library( bzffmpeg-lib
             SHARED
             IMPORTED )
set_target_properties(bzffmpeg-lib
                       PROPERTIES IMPORTED_LOCATION
                       ../../../../src/main/jniLibs/armeabi-v7a/libbzffmpeg.so )

include_directories(
                src/main/cpp/cmdutilt
                src/main/cpp/include
                /Users/luoye/Git/GitLab/FFmpeg-Android-build/ffmpeg
                )


# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in the
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       ffmpegCmd

                       # Links the target library to the log library
                       # included in the NDK.
                      bzffmpeg-lib ${log-lib} ${m-lib} ${z-lib})
