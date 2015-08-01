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

// global variables for possible person locations
bool inLivingroom = false;
bool inKitchen = false;

void callbackLivingroom(PeisTuple* t, void* arg)
{
    // publish "Livingroom" as current location
    string here = t->data;
    if(here.compare("true") == 0)
    {
        inLivingroom = true;
        cout<<"Livingroom"<<endl;
    }
    else
        inLivingroom = false;
}

void callbackKitchen(PeisTuple* t, void* arg)
{
    // publish "Kitchen" as current location
    string here = t->data;
    if(here.compare("true") == 0)
    {
        inKitchen = true;
        cout<<"Kitchen"<<endl;
    }
    else
        inKitchen = false;	
}

int main(int argc, char* argv[])
{
    // instantiate & initialize the PEIS component
    peiskmt_initialize(&argc, argv);
    // retrieve componentID from the component
    int componentID = peiskmt_peisid();
    printf("componentID: %d\n", componentID);

    // create the location tuple
    string location = "Unknown";
    string previousLocation = "Unknown";
    peiskmt_setStringTuple("Location", location.c_str());	

    // define the keys of the tuples to monitor
    char key1[] = "angen1_bool.livingroom.pir01";
    char key2[] = "angen1_bool.kitchen.pir01";

    // subscribe to the tuples to monitor
    PeisTuple prototype1;
    peisk_initAbstractTuple(&prototype1);
    prototype1.owner = -1;
    peisk_setTupleName(&prototype1, key1);
    peisk_subscribeByAbstract(&prototype1);
    peisk_registerTupleCallbackByAbstract(&prototype1, &location, callbackLivingroom);

    PeisTuple prototype2;
    peisk_initAbstractTuple(&prototype2);
    prototype2.owner = -1;
    peisk_setTupleName(&prototype2, key2);
    peisk_subscribeByAbstract(&prototype2);
    peisk_registerTupleCallbackByAbstract(&prototype2, &location, callbackKitchen);
    
    // check whether the person location has changed
    while(peiskmt_isRunning())
    {
        previousLocation = location;
        if(inLivingroom == false && inKitchen == false)
            location = "Unknown";
        else if(inLivingroom == false && inKitchen == true)
            location = "Kitchen";
        else
            location = "Livingroom";
        if(location != previousLocation)
        {
            peiskmt_setStringTuple("Location", location.c_str());
            cout<<"Location: " <<location <<endl;
        }
        sleep(1);
    }
}
