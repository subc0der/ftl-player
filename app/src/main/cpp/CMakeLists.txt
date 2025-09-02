# CMakeLists.txt for FTL Audio Engine
cmake_minimum_required(VERSION 3.22.1)

project("ftl_audio_engine")

# Add the audio engine library
add_library(
    ftl_audio_engine
    SHARED
    ftl_audio_engine.cpp
)

# Find required libraries
find_library(log-lib log)
find_library(android-lib android)

# Link libraries
target_link_libraries(
    ftl_audio_engine
    ${log-lib}
    ${android-lib}
)