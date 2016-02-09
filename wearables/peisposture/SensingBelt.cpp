//===============================================================================//
// Name			: SensingBelt.cpp
// Author(s)	: Barbara Bruno
// Affiliation	: University of Genova, Italy - dept. DIBRIS
// Version		: 2.0
// Description	: Posture and Fall detector module (on-line only)
//===============================================================================//

#include "SensingBelt.hpp"

using namespace std;

// global variables to filter posture transitions
int lyingCounter = 0;
int sittingCounter = 0;
int standingCounter = 0;

//! constructor
SensingBelt::SensingBelt()
{
	// initialize the class variables
	//DEBUG: cout<<endl <<"Creating SensingBelt object" <<endl;
	angle = -1;
	posture = "Standing";
	oldPosture = "Lying";		
}

// publish the fall detection info on PEIS
//! @param report:	fall report acquired from the waist device
//! @return:		---
void SensingBelt::publishFall(string report)
{
	if(report.substr(2) == "Fall")
	{
		// report the new fall occurrence on PEIS
		peiskmt_setStringTuple("Safety.fall.event", "true");
		cout<<"Safety.fall.event: " <<report.substr(2).c_str() <<endl;
	}
	else
	{
		// report user motion after the fall
		peiskmt_setStringTuple("Safety.fall.waist_motion", report.substr(2).c_str());
		cout<<"Waist_motion: " <<report.substr(2).c_str() <<endl;
        peiskmt_setStringTuple("Safety.fall.event", "false");
	}
}

// publish the posture detection info on PEIS
//! @param report:	posture report acquired from the waist device
//! @return:		---
void SensingBelt::publishPosture(string report)
{
	// compute the user posture given the torso angle
	istringstream stream(report.substr(2));
	stream >>angle;
	if(angle > STANDING_THRESHOLD)
    {
        standingCounter++;
        sittingCounter = 0;
        lyingCounter = 0;
    }
	else if(angle > SITTING_THRESHOLD)
    {
        sittingCounter++;
        standingCounter = 0;
        lyingCounter = 0;
    }
	else
    {
		lyingCounter++;
        standingCounter = 0;
        sittingCounter = 0;
    }

	// report the posture on PEIS if a posture change occurred
    //DEBUG: cout<<"lyingCounter = " <<lyingCounter <<" - ";
    //DEBUG: cout<<"sittingCounter = " <<sittingCounter <<" - ";
    //DEBUG: cout<<"standingCounter = " <<standingCounter <<endl;
	if(standingCounter >= COUNTER_THRESHOLD)
        posture = "Standing";
    else if(sittingCounter >= COUNTER_THRESHOLD)
        posture = "Sitting";
    else
        posture = "Lying";
    if(posture != oldPosture)
    {
        peiskmt_setStringTuple("Belt.posture", posture.c_str());
        cout<<"Posture: " <<posture.c_str() <<endl;
        oldPosture = posture;
	}
}
