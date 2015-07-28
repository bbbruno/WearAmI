//===============================================================================//
// Name			: peisposture.cpp
// Author(s)	: Barbara Bruno
// Affiliation	: University of Genova, Italy - dept. DIBRIS
// Version		: 1.0
// Description	: Posture analysis and Fall detection system
//===============================================================================//

#include "SensingBelt.hpp"
#include "libs/SerialStream.h"

using namespace boost::posix_time;

int main(int argc, char* argv[])
{
	string report;		// generic report transmitted by the waist device

	// instantiate & initialize the PEIS component
	peiskmt_initialize(&argc, argv);
	// retrieve componentID from the component
	int componentID = peiskmt_peisid();
	printf("PostureID: %d\n", componentID);

	// instantiate & initialize a SensingBelt object
    SensingBelt one_sensingBelt;

    // setup the serial communication (read-only)
    cout<<endl <<"Setting up serial communication...";
	SerialOptions options;
	options.setDevice(argv[1]);
	options.setBaudrate(9600);
	options.setTimeout(seconds(0));
	options.setParity(SerialOptions::noparity);
	options.setCsize(8);
	options.setFlowControl(SerialOptions::noflow);
	options.setStopBits(SerialOptions::one);
	SerialStream serial(options);
	serial.exceptions(ios::badbit | ios::failbit);
    cout<<endl <<"DONE" <<endl;

	// check the sensing device for reports
	while(peiskmt_isRunning())
	{
		try
		{
			// read the report
			getline(serial,report);

			// fall report in the form: F Fall [Motion]
			if(report[0] == 'F')
				// publish the fall info on PEIS
				one_sensingBelt.publishFall(report);
			// posture report in the form: P [Angle]
			else if(report[0] == 'P')
				// publish the posture info on PEIS
				one_sensingBelt.publishPosture(report);
		}
		catch(TimeoutException&)
		{
			serial.clear();
			cerr<<"Error occurred"<<endl;
		}
	}
}
