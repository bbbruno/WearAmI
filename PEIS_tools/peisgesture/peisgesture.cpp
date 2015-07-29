//===============================================================================//
// Name			: peisgesture.cpp
// Author(s)	: Barbara Bruno
// Affiliation	: University of Genova, Italy - dept. DIBRIS
// Version		: 2.0
// Description	: Human Motion Primitives, Posture and Fall detection system
//===============================================================================//

#include <getopt.h>

#include "creator.hpp"
#include "classifier.hpp"
#include "SensingBracelet.hpp"
#include "libs/SerialStream.h"

using namespace boost::posix_time;

//! Program help 
void print_help()
{
	cout<<endl;
	cout<<"\t\t -------------- peisgesture --------------" <<endl;
	cout<<"Typical calls use the following instructions:" <<endl;
	cout<<"01) -h --help \t\t\t   : program help." <<endl;
    cout<<"02) -m --model [dataset] \t   : [dataset] models creation." <<endl;
	cout<<"03) -v --validate [model] [set] [n]:"
		<<" validate [model] with [n] trials of [set]." <<endl;
	cout<<"04) -t --test [trial] \t\t   :"
		<<" off-line classification of [trial]." <<endl;
	cout<<"05) -c --classify [port] \t   :"
		<<" on-line classification of [port] stream." <<endl;
	cout<<"06) -r --reason [path] [possFile]  :"
		<<" off-line reasoning on [path]/[possFile]." <<endl;
	cout<<"07) -B --Bracelet [port] \t   :"
		<<" on-line HMP analysis on [port] stream." <<endl;

	cout<<endl;
	cout<<"Functions calls examples:" <<endl;
	cout<<"01)   ./HMPdetector -h" <<endl;
	cout<<"02.1) ./HMPdetector -m" <<endl;
	cout<<"02.2) ./HMPdetector -m Ovada" <<endl;
	cout<<"03)   ./HMPdetector -v climb Ovada 6" <<endl;
	cout<<"04)   ./HMPdetector -t drink_drink_stand_sit_drink.txt" <<endl;
	cout<<"05)   ./HMPdetector -c /dev/ttyUSB0" <<endl;
	cout<<"06)   ./HMPdetector -r "
		<<"./Results/longTest/ res_drink_drink_stand_sit_drink.txt" <<endl;
	cout<<"07)   ./HMPdetector -B /dev/ttyUSB0" <<endl;

	cout<<endl;
	cout<<"Enjoy!"<<endl;

	cout<<endl;
}

int main(int argc, char* argv[])
{
	// instantiate & initialize the PEIS component
	peiskmt_initialize(&argc, argv);
	// retrieve componentID from the component
	int componentID = peiskmt_peisid();
	//DEBUG: printf("componentID: %d\n", componentID);

	// instantiate & initialize the peisWearable components
	string dF = "Sweden";
    cout<<endl <<"Default datasetFolder: " <<dF;
	cout<<endl <<"--- Initialization ---" <<endl;
    Device one_device(dF);
    one_device.printInfo();
	Creator one_creator(dF);
	Classifier one_classifier(dF);
	one_classifier.printSetInfo();
	SensingBracelet one_sensingBracelet(dF);
	one_sensingBracelet.printSetInfo();
	cout<<endl <<"--- DONE ---" <<endl;

    // available options (short-form)
	const char *short_options = "v:r:Bct:mh";
	// available options (long-form)
	static struct option long_options[] = 
	{
		{"validate", required_argument, 0, 'v'},
		{"reason", required_argument, 0, 'r'},
		{"Bracelet", required_argument, 0, 'B'},
		{"classify", required_argument, 0, 'c'},
		{"test", required_argument, 0, 't'},
		{"model", optional_argument, 0, 'm'},
		{"help", no_argument, 0, 'h'},
		{0, 0, 0, 0} //required line
	};

	// retrieve & execute the chosen option
	char c = getopt_long(argc, argv, short_options, long_options, NULL);
	switch (c)
	{
		case 'h':
			print_help();
			return EXIT_SUCCESS;
			break;
		case 'm':
			if(argv[2])
				one_creator.setDatasetFolder(argv[2]);				
			one_creator.generateAllModels(one_device);
			cout<<"modelling dataset in: "<<one_creator.datasetFolder <<endl;
			return EXIT_SUCCESS;
			break;
		case 'v':
			one_classifier.validateModel(one_device, argv[2], argv[3], atoi(argv[4]));
			cout<<"results in: ./Results/" <<argv[3] <<"/" <<endl;
			return EXIT_SUCCESS;
			break;
		case 't':
			one_classifier.offlineTest(one_device, argv[2]);
			cout<<"results in: ./Results/longTest/" <<endl;
			return EXIT_SUCCESS;
			break;
		case 'c':
			cout<<"use 'tupleview' to monitor the system" <<endl;
			one_classifier.onlineTest(one_device, argv[2]);
			return EXIT_SUCCESS;
			break;
		case 'r':
			one_sensingBracelet.offlineReasoning(argv[2], argv[3]);
			cout<<"results in: " <<argv[2] <<endl;
			return EXIT_SUCCESS;
			break;
		case 'B':
			cout<<"use 'tupleview' to monitor the system" <<endl;
			one_sensingBracelet.onlineSensingBracelet(one_device, argv[2]);
			return EXIT_SUCCESS;
			break;
		default:
				print_help();
				return EXIT_SUCCESS;
				break;
		}
}
