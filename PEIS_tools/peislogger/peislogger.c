#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>
#include <time.h>
#include <ctype.h>

#include "libs/tpllist.h"

#define CONFIGFILE	"./configs/angen1.conf"
#define LOGFILE		"log.txt"
#define MAXKEYS		50
#define MAXLENGTH	200

/* Semaphores - mutually exclusive */
pthread_mutex_t tuple_processing;

/* list of tuples to subscribe */
char* keys[MAXKEYS]; 
int keyscounter;

/* tuple queue */ 
TupleList tuples;

void eventCallback(PeisTuple* tuple, void* userdata)
{
  	PeisTuple* mytuple;
	mytuple = peiskmt_cloneTuple(tuple);
	
	pthread_mutex_lock(&tuple_processing);
	enQueue(mytuple, &tuples);	
	pthread_mutex_unlock(&tuple_processing);
	
	peiskmt_freeTuple(mytuple);
}

void printUsage(FILE *stream,short argc,char **args)
{
  	fprintf(stream,"%s options:\n",args[0]);
	fprintf(stream," --conf <filename> \t\tSpecify the absolute position of the configuration file\n");
  	fprintf(stream," --log <filename>  \t\tSpecify the name of the log to be produced\n");
	fprintf(stream," --skip            \t\tSkip writing the header in the log file\n");
  	fprintf(stream," --help            \t\tPrint usage information\n");
	fprintf(stream,"\n");
  	
	peiskmt_printUsage(stream,argc,args);
	exit(0);	
}

/* read from configuration file
 * return 1 in case of success, 0 in case of error */
int readConfiguration(char* configfilename )
{ 	
	FILE *loggerconf;
	char line[MAXLENGTH];
	char key[MAXLENGTH];

	keyscounter = 0;

	// Read the configuration file 
	printf("Retrieving keys to subscribe from configuration file %s\n", configfilename);
	if ( !(loggerconf = fopen(configfilename, "r") ) )
    {
		printf("Cannot open configuration file %s \n", configfilename);
		return 0;
	}
	
	// if the configuration file is open, read the configuration parameters 
	while( fgets(line, sizeof(line), loggerconf) )
    {
		if (line[0] != ';')
        {
			sscanf(line, "%s", key);
    		keys[keyscounter] = (char *)malloc(sizeof(char)*(strlen(key)+1)); 
    		memcpy(keys[keyscounter], key, strlen(key)+1);
			keyscounter ++;
		}
	}
	if (keyscounter == 0)
    {
		// no keys found
		printf("no keys found in configuration file\n");
		return 0;
	}
	
	fclose(loggerconf);
	return 1;
}


int main(int argc,char **args)
{	
	char configfilename[MAXLENGTH];
	char logfilename[MAXLENGTH];
	FILE* logfile;
	int skipHeader = 0;
	char tuplename[MAXLENGTH];
	int isRunning;
	int element_ready;
	PeisTuple* mytuple;	
	int i;
	int ID, len;
	char* value;
	time_t now;
    struct tm ts;
  	char date[MAXLENGTH];
	float kernel_time;
  
	// handlers
  	PeisSubscriberHandle subHandler[MAXKEYS];
	PeisCallbackHandle callHandler[MAXKEYS];
	  	
	// init isRunning
	isRunning = 1;
	// init the semaphore
	pthread_mutex_init(&tuple_processing, NULL);
	// init the tuple list
	initializeList(&tuples);
	
	// initial check for arguments
  	memcpy(configfilename, CONFIGFILE, strlen(CONFIGFILE)+1);
  	memcpy(logfilename, LOGFILE, strlen(CONFIGFILE)+1);

	for ( i = 0; i < argc; i++ )
    {
		if (strcmp(args[i],"--help") == 0) { printUsage(stderr,argc,args); }
		if (strcmp(args[i],"--conf") == 0) { memcpy(configfilename, args[i+1], strlen(args[i+1])+1); }
		if (strcmp(args[i],"--log") == 0)  { memcpy(logfilename, args[i+1], strlen(args[i+1])+1); }
		if (strcmp(args[i],"--skip") == 0) { skipHeader = 1; }
	}

  	// instantiate & initialize the PEIS component
	peiskmt_initialize(&argc, args);

	// prepare the header of the log file
  	// time: "ddd yyyy-mm-dd hh:mm:ss zzz"
 	time(&now);
  	ts = *localtime(&now);
  	strftime(date, sizeof(date), "%a %Y-%m-%d %H:%M:%S %Z", &ts);
  	kernel_time = peiskmt_gettimef();

  	// Printing the header for the log file
  	if (skipHeader == 0)
	{
        if( !(logfile = fopen(logfilename, "w") ) )
        {
            printf("Error opening the log file %s \n", logfilename);
            isRunning = 0;
        }
        ID = peiskmt_peisid();
        PeisTuple* kernelTuple = peiskmt_getTuple(ID,"kernel.version",PEISK_KEEP_OLD|PEISK_NON_BLOCKING);
        fprintf(logfile, "; Kernel version %s\n", kernelTuple->data);
        fprintf(logfile, "; peislogger version 1.0\n");
        fprintf(logfile, "; starting at %f kernel time, %s\n", kernel_time, date);
        fprintf(logfile, "%f\n", kernel_time);
        fprintf(logfile, "; structure of recorded data:\n");
        fprintf(logfile, "; kernel_ts,write_ts,user_ts,expire_ts,owner,creator,keyDepth,keys,datalenght,data\n");
        fclose(logfile);
	}

  	// Reading the keys of the tuples to subscribe
  	if (!readConfiguration(configfilename))
    {
		printf("Problem reading from configuration file %s \n", configfilename);
		isRunning = 0;
	}
	
  	// Subscribe to all the keys specified in the configuration file and attach a callback
  	for( i = 0 ; i < keyscounter ; i++)
    {
        printf("Subscribing to tuple: \"%s\" ", keys[i]);
        printf("...");
        subHandler[i] = peiskmt_subscribe(-1, keys[i]);
        callHandler[i] = peiskmt_registerTupleCallback(-1, keys[i], NULL, (void*)eventCallback);
        printf("DONE \n");
  	}
 	
  	while( isRunning && peiskmt_isRunning() )
    { 
        // get the tuple to write
  		// not very elegant, but this way I can use the semaphores
		pthread_mutex_lock(&tuple_processing);
  		element_ready = isNotEmpty(&tuples);
		pthread_mutex_unlock(&tuple_processing);

		if(element_ready)
        {
			pthread_mutex_lock(&tuple_processing);
			mytuple = getAndDeleteFirstElement(&tuples);
			pthread_mutex_unlock(&tuple_processing);
			
			peiskmt_printTuple(mytuple);
			printf("\n");
            if ( !(logfile = fopen(logfilename, "a") ) )
            {
				printf("Error appending to logfile file %s \n", logfilename);
				exit(0);
			}
            peiskmt_getTupleName(mytuple, tuplename, sizeof(tuplename));
            fprintf(logfile, "%f %d.%d %d.%d %d.%d %d %d %d %d %d %s %d %s\n", 
			peiskmt_gettimef(), mytuple->ts_write[0], mytuple->ts_write[1], 
			mytuple->ts_user[0], mytuple->ts_user[1], mytuple->ts_expire[0], mytuple->ts_expire[1],
			mytuple->owner, mytuple->creator, mytuple->alloclen, mytuple->isNew, mytuple->keyDepth, 
			tuplename, strlen(mytuple->data)+1, (char*)mytuple->data);
    		fclose(logfile);
		}		
	    usleep(10000);
  	}

	// unsubscribe
	for( i = 0 ; i < keyscounter ; i++)
    {
        peiskmt_unsubscribe(subHandler[i]);
		peiskmt_unregisterTupleCallback(callHandler[i]);
	}

	// free the memory of both the queue and the variables
	peiskmt_freeTuple(mytuple);
	emptyQueue(&tuples);
	pthread_mutex_destroy(&tuple_processing);
  	
	peiskmt_shutdown();
}
