
cmake_minimum_required(VERSION 3.4.1)

file(GLOB source_file src/main/cpp/*.cpp)
add_library( # Sets the name of the library.
        yulian
        SHARED
        ${source_file}
        )


include_directories(${CMAKE_SOURCE_DIR}/src/main/cpp/include)
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/src/main/cpp/jniLibs/${CMAKE_ANDROID_ARCH_ABI} ")

message("${CMAKE_SOURCE_DIR}/src/main/cpp/jniLibs/${CMAKE_ANDROID_ARCH_ABI}")

target_link_libraries(
        yulian
        log
        z
        android)