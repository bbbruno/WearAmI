#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "peiskernel/peiskernel_mt.h"
#include "pthread.h"
#include "libs/buffer.h"
#include "libs/parser.h"

#define TUPLE_MAX_SIZE 	1000000
#define ABS		1
#define	REL		2

/* Global Variables */
int mode;			            // playing mode 
long int logger_ts;		        // initial time stamp
char filename[50];		        // name of the log to play
TupleBuffer  tuple_buffer;	    // tuples ready to be fired
ParserBuffer ordering_buffer;	// order of the tuples to be played

void getLoggerTS()
{
	logger_ts = readFirstTimeStamp(&ordering_buffer);
  	if (logger_ts == -1 )
    {
        printf("Error: time stamp not defined\n");
        exit(0);
  	}
}

int orderLogFile()
{
	FILE* log;
	char line[100000];
	long int peis_ts1, peis_ts2, ts_write1, ts_write2, ts_user1, ts_user2, ts_expire1, ts_expire2, owner, creator, alloclen, isnew, keydepth, tuplelength;
	char tuplename[100];
	char data [1000000];

	int line_counter = 1;

	if (!(log = fopen(filename, "r")) )
    {
        printf("Error - unable to open log file\n");
        exit(0);
  	}

	while( fgets(line, sizeof(line), log) )
    {
		if (line[0] != ';')
        {
			sscanf(line, "%ld.%ld %ld.%ld %ld.%ld %ld.%ld %ld %ld %ld %ld %ld %s %ld %s\n", 
				&peis_ts1, &peis_ts2, &ts_write1, &ts_write2, &ts_user1, &ts_user2, &ts_expire1, &ts_expire2, &owner, &creator, &alloclen, &isnew, &keydepth, tuplename, &tuplelength, data);
			if (mode == REL)
				enQueueParserElementRel(&ordering_buffer, line_counter, peis_ts1);
			else
				enQueueParserElementAbs(&ordering_buffer, line_counter, ts_write1);
			line_counter ++;
		}
	}

	fclose(log);
	return 1;
} 

void putTupleIntoBuffer(int line_requested)
{	
	FILE* log;
	char line[1000000];
	long int peis_ts1, peis_ts2, ts_write1, ts_write2, ts_user1, ts_user2, ts_expire1, ts_expire2, owner, creator, alloclen, isnew, keydepth, tuplelength;
	char tuplename[100];
	char data [1000000];

	int line_counter = 1;

	if (!(log = fopen(filename, "r")) )
    {
        printf("Error - unable to open log file\n");
        exit(0);
  	}
	
	while( fgets(line, sizeof(line), log) )
    {
		if (line[0] != ';')
        {
			if (line_counter == line_requested)
            {
				sscanf(line, "%ld.%ld %ld.%ld %ld.%ld %ld.%ld %ld %ld %ld %ld %ld %s %ld %s\n", 
					&peis_ts1, &peis_ts2, &ts_write1, &ts_write2, &ts_user1, &ts_user2, &ts_expire1, &ts_expire2, &owner, &creator, &alloclen, &isnew, &keydepth, tuplename, &tuplelength, data);
				if (mode == REL)
					enQueueTuple(&tuple_buffer, tuplename, tuplelength, data, peis_ts1);
				else
					enQueueTuple(&tuple_buffer, tuplename, tuplelength, data, ts_write1);
				break;
			}
			line_counter ++;
		}
	}
	fclose(log);
}

void printUsage(FILE *stream, short argc, char **args)
{
    fprintf(stream,"PeisLoggerPlayer options:\n");
    fprintf(stream," --help                            Print usage information\n");
    fprintf(stream," --file <FileName> <OpeningMode>   Put the log file name and the opening mode (rel or abs)\n");
    peisk_printUsage(stream, argc, args);
    exit(0);
}

int main(int argc, char **args) 
{
 	int line, i = 0, len;
  	char *value;

  	long int player_ts, microseconds;
  	long int delta, old_delta;

	initializeTupleBuffer(&tuple_buffer);
  	initializeParserBuffer(&ordering_buffer); 

	char tuplename[512];
	int datalength;
	char data[2000000];
	long int element_ts;
	
  	peisk_gettime2(&player_ts, &microseconds);
	delta = 0;
	old_delta = 0;

  	// Initialize everything
  	peiskmt_initialize(&argc, args);

	mode = REL;
	for ( i = 0; i < argc; i++ )
    {
		if (strcmp(args[i],"--help") == 0) { printUsage(stderr,argc,args); }
		if (strcmp(args[i],"--file") == 0) { memcpy(filename, args[i+1], strlen(args[i+1])+1); }; 
		if (strcmp(args[i],"rel") == 0)  { mode = REL; }
		if (strcmp(args[i],"abs") == 0)  { mode = ABS; }
	}

	if (!orderLogFile())
    {
		printf("Error loading file... \n");
		exit(0);
	}

	getLoggerTS();

	// TO_CHANGE: now the whole log file is put in memory!!
	line = getLineOfFirstElement(&ordering_buffer);
  	while (line != 0)
    {
		putTupleIntoBuffer(line);
	  	line = getLineOfFirstElement(&ordering_buffer);
	}

  	while(peiskmt_isRunning())
    {
        readTuple(&tuple_buffer, tuplename, &datalength, data, &element_ts);
        printf("%s ",tuplename);
        printf("%s\n", data);
        
        delta = element_ts - logger_ts;
		printf("\nElement < %s , %s > ready to be fired in %ld seconds\n",tuplename, data, delta - old_delta);
        while (peiskmt_gettime() - player_ts < delta)
        {
            usleep(100);
        }
        //peiskmt_setTuple(tuplename, datalength, data, NULL, PEISK_ENCODING_WILDCARD);
        peiskmt_setStringTuple(tuplename,data);        
        printf("------------------------------------------------------------------\n");
		printf("TUPLE FIRED:\n");
		printf("TupleName < %s >	Data < %s >\n",tuplename,data);
    	printf("------------------------------------------------------------------\n");
        old_delta = delta;
        if (isEmpty(&tuple_buffer))
            break;
  	}
  	printf("Execution Over - ending program\n");
  	emptyParserBuffer(&ordering_buffer);
  	emptyTupleBuffer(&tuple_buffer);
 	peiskmt_shutdown();
}


