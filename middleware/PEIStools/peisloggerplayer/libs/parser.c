#include "parser.h"

void initializeParserBuffer( ParserBuffer* buffer )
{
  	buffer->head = NULL;
  	buffer->tail = NULL;
  	buffer->current = NULL;
  	buffer->timeLine = NULL;
  	buffer->elements_in_buffer = 0;
}

int emptyParserBuffer(ParserBuffer* buffer)
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


int enQueueParserElementRel(ParserBuffer* buffer, int lineNum, long int ts)
{	
	if (buffer->tail == NULL)
    {
		buffer->tail = (ParserElement*) malloc(sizeof(ParserElement));
		buffer->tail->next = NULL;
		buffer->head = buffer->tail;
		buffer->current = buffer->head;
	}
    else
    {
		buffer->tail->next = (ParserElement*) malloc(sizeof(ParserElement));
		buffer->tail = buffer->tail->next;
		buffer->tail->next = NULL;
	}	
	buffer->tail->time_stamp = ts;
    buffer->tail->line = lineNum;
    buffer->elements_in_buffer++;

	return 1;
}

int enQueueParserElementAbs(ParserBuffer* buffer, int lineNum, long int ts)
{
  	ParserElement* pointer;
	ParserElement* pointer_before;
	ParserElement* new_element;
	
	if (buffer->head == NULL)
    {
        buffer->head = (ParserElement*)malloc(sizeof(ParserElement));
        buffer->head->time_stamp = ts;
    	buffer->head->line = lineNum;
    	buffer->head->next = NULL;
    	buffer->current = buffer->head;
    	buffer->tail = buffer->head;
    	buffer->timeLine = buffer->head;
    	buffer->elements_in_buffer++;
		buffer->current = buffer->head;
		return 1;
  	}
	
	if (buffer->head->time_stamp > ts)
    {
        pointer = (ParserElement*)malloc(sizeof(ParserElement));
        pointer->time_stamp = ts;
        pointer->line = lineNum;
        pointer->next = buffer->head;
        buffer->head = pointer;
        buffer->elements_in_buffer++;
		buffer->current = buffer->head;
  	}

	pointer_before = buffer->head;
	pointer = buffer->head;

	while (pointer->next)
    {
		pointer_before = pointer;
		pointer = pointer->next;
		if (pointer->time_stamp >= ts)
        {
			new_element = (ParserElement*)malloc(sizeof(ParserElement));
			new_element->time_stamp = ts;
            new_element->line = lineNum;
            new_element->next = buffer->head;
            pointer_before->next = new_element;
			new_element->next = pointer;
    		buffer->elements_in_buffer++;
			buffer->current = buffer->head;
			return 1;
		}
	}

	// the element should be placed at the tail
	new_element = (ParserElement*)malloc(sizeof(ParserElement));
	new_element->time_stamp = ts;
    new_element->line = lineNum;
    new_element->next = NULL;
	buffer->tail->next = new_element;
	buffer->tail = new_element;
	buffer->current = buffer->head;
    buffer->elements_in_buffer++;
	return 1;
}


double readFirstTimeStamp(ParserBuffer* buffer)
{
	if (buffer->elements_in_buffer == 0)
		return -1;
    return buffer->head->time_stamp;
}

/* here I use buffer current. Ugly! */
int getLineOfFirstElement(ParserBuffer* buffer)
{	
	int res;
	if (buffer->elements_in_buffer == 0)
		return 0;
	if (!(buffer->current))
		return 0;	

 	res = buffer->current->line;
	buffer->current = buffer->current->next;
    return res;
}


