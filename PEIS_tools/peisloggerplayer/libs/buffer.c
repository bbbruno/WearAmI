#include "buffer.h"

void initializeTupleBuffer( TupleBuffer* buffer )
{
  	buffer->head = NULL;
  	buffer->tail = NULL;
  	buffer->current = NULL;
  	buffer->elements_in_buffer = 0;
}

int emptyTupleBuffer(TupleBuffer* buffer)
{
	if (buffer->elements_in_buffer == 0)
		return 1;	
	
	/* free the list until you have only one element */
	while (buffer->elements_in_buffer > 1)
    {
		buffer->current = buffer->head;
		while (buffer->current->next != buffer->tail)
			buffer->current = buffer->current->next;
		buffer->tail = buffer->current;
		free(buffer->tail->next);
		buffer->elements_in_buffer--;
	}
	
	/* last element */
	free(buffer->tail);
	buffer->tail = buffer->head = buffer->current = NULL;
	buffer->elements_in_buffer--;
	return 1;
}

int enQueueTuple( TupleBuffer* buffer, char* name, int len, char* dt, long int ts)
{
	if (buffer->tail == NULL)
    {
		buffer->tail = (TupleElement*) malloc(sizeof(TupleElement));
		buffer->tail->next = NULL;
		buffer->head = buffer->tail;
	}
    else
    {
		buffer->tail->next = (TupleElement*) malloc(sizeof(TupleElement));
		buffer->tail = buffer->tail->next;
		buffer->tail->next = NULL;
	}	
	buffer->tail->time_stamp = ts;
    buffer->tail->tuplename = (char*)malloc(strlen(name)*sizeof(char));
    strcpy(buffer->tail->tuplename, name);
    buffer->tail->datalen = len;
    buffer->tail->data = (char*)malloc(strlen(dt)*sizeof(char));
    strcpy(buffer->tail->data, dt);
    buffer->elements_in_buffer++;

	return 1;
}

int readTuple(TupleBuffer* buffer, char* tuplename, int* datalength, char* data, long int* ts)
{
	TupleElement* pointer;

	if (buffer->elements_in_buffer == 0)
		return 0;
	
	pointer = buffer->head;
	memcpy(tuplename, pointer->tuplename, strlen(pointer->tuplename)+1);
    printf("%s\n",pointer->tuplename);
	memcpy(data, pointer->data, strlen(pointer->data)+1);
    printf("%s\n",pointer->data);
	*datalength = pointer->datalen;
	*ts = pointer->time_stamp;
	buffer->head =pointer->next; 
	free(pointer);
	buffer->elements_in_buffer--;
	return 1;
}


int isEmpty(TupleBuffer* buffer)
{
	if (buffer->elements_in_buffer > 0)
		return 0;

	return 1;
}
