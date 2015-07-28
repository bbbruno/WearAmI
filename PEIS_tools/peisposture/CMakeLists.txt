cmake_minimum_required(VERSION 2.4)
if(COMMAND cmake_policy)
      cmake_policy(SET CMP0003 NEW)
endif(COMMAND cmake_policy)

set(CMAKE_CXX_FLAGS "-g -Wall")

PROJECT(peisposture)

# find Boost (thread, date-time, system) - required by SerialStream
include_directories(${Boost_INCLUDE_DIRS})
link_directories(${Boost_LIBRARY_DIRS})
if(USE_STATIC_BOOST)
    set(Boost_USE_STATIC_LIBS FALSE)
endif()
set(BOOST_LIBS ${BOOST_LIBS} thread date_time system)
find_package(Boost REQUIRED COMPONENTS ${BOOST_LIBS})

# find Threads - required by SerialStream
find_package(Threads REQUIRED)

ADD_EXECUTABLE(peisposture
  ./peisposture.cpp
  ./SensingBelt.cpp ./SensingBelt.hpp
  ./libs/SerialStream.cpp ./libs/SerialStream.h)

TARGET_LINK_LIBRARIES(peisposture -lpeiskernel_mt -lpeiskernel -lpthread)
TARGET_LINK_LIBRARIES(peisposture ${Boost_LIBRARIES} ${CMAKE_THREAD_LIBS_INIT})

INSTALL(
  TARGETS peisposture 
  RUNTIME DESTINATION /usr/local/bin
  LIBRARY DESTINATION /usr/local/lib
  ARCHIVE DESTINATION /usr/local/lib
)

