cmake_minimum_required(VERSION 2.4)
if(COMMAND cmake_policy)
      cmake_policy(SET CMP0003 NEW)
endif(COMMAND cmake_policy)

set(CMAKE_CXX_FLAGS "-g -Wall")

PROJECT(peisloggerplayer)

ADD_EXECUTABLE(peisloggerplayer
  peisloggerplayer.c libs/buffer.c libs/buffer.h libs/parser.c libs/parser.h
)

TARGET_LINK_LIBRARIES(peisloggerplayer -lpeiskernel_mt -lpeiskernel -lpthread)
TARGET_LINK_LIBRARIES(peisloggerplayer ${CMAKE_THREAD_LIBS_INIT})

INSTALL(
  TARGETS peisloggerplayer 
  RUNTIME DESTINATION /usr/local/bin
  LIBRARY DESTINATION /usr/local/lib
  ARCHIVE DESTINATION /usr/local/lib
)

