cmake_minimum_required(VERSION 2.4)
if(COMMAND cmake_policy)
      cmake_policy(SET CMP0003 NEW)
endif(COMMAND cmake_policy)

set(CMAKE_CXX_FLAGS "-g -Wall")

PROJECT(peislogger)

ADD_EXECUTABLE(peislogger
  peislogger.c libs/tpllist.c libs/tpllist.h
)

TARGET_LINK_LIBRARIES(peislogger -lpeiskernel_mt -lpeiskernel -lpthread)
TARGET_LINK_LIBRARIES(peislogger ${CMAKE_THREAD_LIBS_INIT})

INSTALL(
  TARGETS peislogger 
  RUNTIME DESTINATION /usr/local/bin
  LIBRARY DESTINATION /usr/local/lib
  ARCHIVE DESTINATION /usr/local/lib
)

