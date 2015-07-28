//===============================================================================//
// Name			: SensingBelt.hpp
// Author(s)	: Barbara Bruno
// Affiliation	: University of Genova, Italy - dept. DIBRIS
// Version		: 2.0
// Description	: Posture and Fall detector module (on-line only)
//===============================================================================//

#include <iostream>
#include <sstream>

#ifdef __cplusplus
extern "C"
{
#endif	
	#include <peiskernel/peiskernel_mt.h>
#ifdef __cplusplus
}
#endif

using namespace std;

#ifndef SENSINGBELT_HPP_
#define SENSINGBELT_HPP_

#define STANDING_THRESHOLD 155.0
#define SITTING_THRESHOLD 105.0

//! definition of the class "SensingBelt"
class SensingBelt
{
	public:
		double angle;		// user torso angle
		string posture;		// user posture (Standing/Sitting/Lying)
		string oldPosture;	// previous user posture (avoid repetitions)

		//! constructor
		SensingBelt();

		// publish the fall detection info on PEIS
		void publishFall(string report);

		// publish the posture detection info on PEIS
		void publishPosture(string report);

		//! destructor
		~SensingBelt()
		{
			//DEBUG: cout<<endl <<"Destroying SensingBelt object" <<endl;
		}
};

#endif
