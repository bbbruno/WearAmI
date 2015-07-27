#include <iostream>

#ifdef __cplusplus
extern "C"
{
#endif	
	#include <peiskernel/peiskernel_mt.h>
#ifdef __cplusplus
}
#endif

using namespace std;

void callbackLivingroom(PeisTuple* t, void* arg)
{
    // publish "Livingroom" as current location
    peiskmt_setStringTuple("angen1_location", "Livingroom");	
}

void callbackKitchen(PeisTuple* t, void* arg)
{
    // publish "Kitchen" as current location
    peiskmt_setStringTuple("angen1_location", "Kitchen");	
}

int main(int argc, char* argv[])
{
	// instantiate & initialize the PEIS component
	peiskmt_initialize(&argc, argv);
	// retrieve componentID from the component
	int componentID = peiskmt_peisid();
	printf("componentID: %d\n", componentID);

    // define the keys of the tuples to monitor
	string key1="angen1_bool.livingroom.pir01.*";
    string key2="angen1_bool.livingroom.pir02.*";
    string key3="angen1_bool.kitchen.pir01.*";

    // subscribe to the tuples
	PeisTuple prototype1;
    PeisTuple prototype2;
    PeisTuple prototype3;
    // key1
    peisk_initAbstractTuple(&prototype1);
	prototype1.owner = -1;
	peisk_setTupleName(&prototype1,key1.c_str());
    peisk_subscribeByAbstract(&prototype1);
	peisk_registerTupleCallbackByAbstract(&prototype1, NULL,callbackLivingroom);
    // key2
    peisk_initAbstractTuple(&prototype2);
	prototype2.owner = -1;
	peisk_setTupleName(&prototype2,key2.c_str());
    peisk_subscribeByAbstract(&prototype2);
	peisk_registerTupleCallbackByAbstract(&prototype2, NULL,callbackLivingroom);
    // key3
    peisk_initAbstractTuple(&prototype3);
	prototype3.owner = -1;
	peisk_setTupleName(&prototype3,key3.c_str());
    peisk_subscribeByAbstract(&prototype3);
	peisk_registerTupleCallbackByAbstract(&prototype3, NULL,callbackKitchen);

    // hold on forever
    while(peiskmt_isRunning())
    {
        sleep(1);
    }
}
