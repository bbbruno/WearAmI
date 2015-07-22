#include <stdio.h>
#include <stdlib.h>
#include <string.h>


typedef struct ParsingElementType {
  	long int time_stamp;
  	int line;
  	struct ParsingElementType* next;
} ParserElement;

typedef struct {
  	ParserElement *head;
  	ParserElement *tail;
  	ParserElement *current;
  	ParserElement *timeLine;
  	int elements_in_buffer;
}ParserBuffer;


void initializeParserBuffer (ParserBuffer* buffer);

int emptyParserBuffer(ParserBuffer* buffer);

int enQueueParserElementAbs(ParserBuffer *buffer, int lineNum, long int ts);

int enQueueParserElementRel(ParserBuffer *buffer, int lineNum, long int ts);

double readFirstTimeStamp(ParserBuffer* buffer);

int readParserElement(ParserBuffer *buffer);
