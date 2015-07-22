#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

typedef struct TupleElementType {
	long int time_stamp;
  	char* tuplename;
  	int datalen;
  	char* data;
  	struct TupleElementType* next;
} TupleElement;

typedef struct {
  	TupleElement* head;
  	TupleElement* tail;
  	TupleElement* current;
  	int elements_in_buffer;
} TupleBuffer;

void initializeTupleBuffer( TupleBuffer* buffer);

int emptyTupleBuffer(TupleBuffer* buffer);

int enQueueTuple(TupleBuffer* buffer, char* name, int len, char *dt, long int ts);

int readTuple(TupleBuffer* buffer, char* tuplename, int* datalength, char *data, long int* ts);

int isEmpty(TupleBuffer* buffer);
