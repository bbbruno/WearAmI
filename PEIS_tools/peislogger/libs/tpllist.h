#include <stdio.h>
#include <stdlib.h>
#include <string.h>
//#include "peiskernel/peiskernel_mt.h"
#include "pthread.h"
#include <peiskernel/peiskernel_mt.h>

/* type definition */
/* dynamic list */
typedef struct tuple_element {
	PeisTuple* tuple;
	struct tuple_element* next;
} TupleElement;

typedef struct tuple_list {
	TupleElement* head;
	TupleElement* tail;
	int queue_length;
} TupleList;


/***********************************/
/*    list management functions    */
/***********************************/

/* init the list of objects */
void initializeList(TupleList* list);

/* insert a new element into the list */
int enQueue (PeisTuple* tuple, TupleList* list); 

/* empty the object list */
int emptyQueue(TupleList* list);

/* check if there are still elements in the list */
int isNotEmpty(TupleList* list);

/* get and remove the head of the queue, returning a 
 * pointer to its tuple */
PeisTuple* getAndDeleteFirstElement(TupleList* list);
