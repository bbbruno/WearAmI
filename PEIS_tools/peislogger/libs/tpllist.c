#include "tpllist.h"


/* initialize an empty list */
void initializeList(TupleList* list) {
	list->head = NULL;
	list->tail = NULL;
	list->queue_length = 0;
}



int enQueue (PeisTuple* tuple, TupleList* list) {
	
	if (list->tail == NULL) {
		list->tail = (TupleElement *) malloc(sizeof(TupleElement));
		list->tail->next = NULL;
		list->head = list->tail;
	} else {
		list->tail->next = (TupleElement *) malloc(sizeof(TupleElement));
		list->tail = list->tail->next;
		list->tail->next = NULL;
	}
	list->tail->tuple = peiskmt_cloneTuple(tuple);
	// memcpy (list->tail->tuple, tuple, strlen(tuple)+1);
	list->queue_length++;

	return 1;
}



int isNotEmpty(TupleList* list) {
	
	if(list->queue_length == 0) return 0;
	return 1;
}
	


PeisTuple* getAndDeleteFirstElement(TupleList* list){
	
	TupleElement* pointer;
	PeisTuple* tuple;
	
	tuple = peiskmt_cloneTuple(list->head->tuple);

	pointer = list->head;
	/* only one element? */
	if (list->queue_length == 1){
		list->head = list->tail = NULL;
	} else {
		list->head = pointer->next;
	}
	list->queue_length--;
	peiskmt_freeTuple(pointer->tuple);
	free(pointer);

	return tuple;	
}

	

int emptyQueue(TupleList *list) {
	
		if(list->queue_length == 0) {
		return 0;
	}
	TupleElement* pointer;
		
	if (list->queue_length == 0) {
		return 1;
	}

	/* free the list until you have only one element */
	while (list->queue_length > 1) {
		pointer = list->head;
		while (pointer->next != list->tail) {
			pointer = pointer->next;
		}
		list->tail = pointer;
		peiskmt_freeTuple(list->tail->next->tuple);
		free(list->tail->next);
		list->queue_length--;
	}
	
	/* last element */
	peiskmt_freeTuple(list->tail->tuple);
	free(list->tail);
	list->tail = list->head = NULL;
	list->queue_length--;
	
	return 1;
}
