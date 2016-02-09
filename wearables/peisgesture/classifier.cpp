//===============================================================================//
// Name			: classifier.hpp
// Author(s)	: Barbara Bruno, Antonello Scalmato
// Affiliation	: University of Genova, Italy - dept. DIBRIS
// Version		: 1.1
// Description	: Human Motion Primitives classifier module (on-line / off-line)
//===============================================================================//

#include <fstream>

#include "classifier.hpp"
#include "libs/SerialStream.h"

using namespace arma;
using namespace boost::posix_time;

//! constructor with variables initialization
//! @param HMPn:	name of the motion primitive (within the dataset)
//! @param gW:		weight of gravity feature for classification
//! @param bW:		weight of body acc. feature for classification
//! @param th:		max distance for possible motion occurrence
DYmodel::DYmodel(string HMPn, float gW, float bW, float th)
{
	// initialize the class variables
	cout<<"Loading model: " <<HMPn <<"...";
	HMPname = HMPn;
	gravityWeight = gW;
	bodyWeight = bW;
	threshold = th;

	// load the model (initialization of gP, gS, bP, bS)
	bP = loadMu(HMPname, "Body");		//DEBUG: cout<<"MuBody-";
	bS = loadSigma(HMPname, "Body");	//DEBUG: cout<<"SigmaBody-";
	gP = loadMu(HMPname, "Gravity");	//DEBUG: cout<<"MuGravity-";
	gS = loadSigma(HMPname, "Gravity");	//DEBUG: cout<<"SigmaGravity-";

	// compute the size of the model
	size = gP.n_cols;

	cout<<"DONE"<<endl;
}

//! print model information
//! @return:	---
void DYmodel::printInfo()
{
	cout<<"DYmodel object information:" <<endl;
	cout<<"HMPname = " <<HMPname <<endl;
	cout<<"gravityWeight = " <<gravityWeight <<endl;
	cout<<"bodyWeight = " <<bodyWeight <<endl;
	cout<<"threshold = " <<threshold <<endl;
	cout<<"size = " <<size <<endl;
}

//! load the expected points (Mu) of one feature
//! @param HMPname:		name of the model (within the dataset)
//! @param component:	name of the feature
//! @return:			matrix of the expected points of the feature
mat DYmodel::loadMu(string HMPname, string component)
{
	int row;
	int col;

	string fileName = HMPname + "Mu" + component + ".txt";
	FILE *pf = fopen(fileName.c_str(), "r");
	fscanf(pf, "%d,%d\n", &col, &row);
	mat mod = zeros<mat>(row, col);
	for (int r = 0; r < row; r++)
	{
		int c;
		float fr;
		for (c = 0; c < col - 1; c++)
		{
			fscanf(pf, "%f,", &fr);
			mod(r, c) = fr;
		}
		fscanf(pf, "%f\n", &fr);
		mod(r, c) = fr;
	}
	mod = mod.t();
	fclose(pf);

	return mod;
}

//! load the expected variances (Sigma) of one feature
//! @param HMPname:		name of the model (within the dataset)
//! @param component:	name of the feature
//! @return:			matrix of the expected variances of the feature
cube DYmodel::loadSigma(string HMPname, string component)
{
	int row;
	int col;
	int slice;

	string fileName = HMPname + "Sigma" + component + ".txt";
	FILE *pf = fopen(fileName.c_str(), "r");
	fscanf(pf, "%d,%d,%d\n", &row, &col, &slice);
	cube mod = zeros<cube>(row, col, slice);
	for (int s = 0; s < slice; s++)
	{
		for (int r = 0; r < row; r++)
		{
			int c;
			float fr;
			for (c = 0; c < col - 1; c++)
			{
				fscanf(pf, "%f,", &fr);
				mod(r, c, s) = fr;
			}
			fscanf(pf, "%f\n", &fr);
			mod(r, c, s) = fr;
		}
	}
	fclose(pf);

	return mod;
}

//! set all the model variables and load the model
//! @param HMPn:	name of the motion primitive (within the dataset)
//! @param gW:		weight of gravity feature for classification
//! @param bW:		weight of body acc. feature for classification
//! @param th:		max distance for possible motion occurrence
//! @return:		---
void DYmodel::build(string HMPn, float gW, float bW, float th)
{
	// initialize the class variables
	cout<<"Loading model: " <<HMPn <<"...";
	HMPname = HMPn;
	gravityWeight = gW;
	bodyWeight = bW;
	threshold = th;

	// load the model (initialization of gP, gS, bP, bS)
	bP = loadMu(HMPname, "Body");		//DEBUG: cout<<"MuBody-";
	bS = loadSigma(HMPname, "Body");	//DEBUG: cout<<"SigmaBody-";
	gP = loadMu(HMPname, "Gravity");	//DEBUG: cout<<"MuGravity-";
	gS = loadSigma(HMPname, "Gravity");	//DEBUG: cout<<"SigmaGravity-";

	// compute the size of the model
	size = gP.n_cols;

	cout<<"DONE"<<endl;
}

//! constructor
//! @param dF:	folder containing the modelling dataset
Classifier::Classifier(string dF)
{	
    string one_HMPn;
	float one_gW;
	float one_bW;
	float one_th;
	DYmodel *one_model;

	datasetFolder = "./Models/" + dF + "/";

    // load the classifier configuration
	string fileName = datasetFolder + "Classifierconfig.txt";
	//DEBUG:cout<<"config file: " <<fileName <<endl;
	ifstream configFile(fileName.c_str());
	configFile >>nbM;
	//DEBUG:cout<<"nbM: " <<nbM <<endl;
	for(int i=0; i< nbM; i++)
	{
		configFile>>one_HMPn >>one_gW >>one_bW >>one_th;
		one_HMPn = datasetFolder + one_HMPn;
		//DEBUG:cout<<"DYmodel: " <<one_HMPn <<endl;
		one_model = new DYmodel(one_HMPn, one_gW, one_bW, one_th);
		//DEBUG:one_model->printInfo();
		set.push_back(*one_model);
	}
	configFile.close();

	// compute the size of the window
	int temp_ws = set[0].size;
	for(int i=1; i< nbM; i++)
	{
		if(set[i].size > temp_ws)
			temp_ws = set[i].size;
	}
	window_size = temp_ws;

	// publish the static information (number & names of models) on PEIS
	publishStatic();
}

//! print set information
//! @return:	---
void Classifier::printSetInfo()
{
	for(int i=0; i<nbM; i++)
		set[i].printInfo();
}

//! create a window of samples
//! @param &cR:         reference to the coded range of the device
//! @param &sR:         reference to the sensing range of the device
//! @param &one_sample:	reference to the sample to be added to the window
//! @param &window:		reference to the window
//! @param &N:			reference to the size of the window
//! @param &numWritten:	reference to the number of samples in the window
//! @return:			---
void Classifier::createWindow(int &cR,float &sR,mat &one_sample,mat &window,int &N,int &numWritten)
{
	mat noisy_sample = zeros<mat>(1, 3);

	// convert the coded values into real acceleration values
    noisy_sample(0, 0) = (one_sample(0, 0) / cR) * (sR);
	noisy_sample(0, 1) = (one_sample(0, 1) / cR) * (sR);
	noisy_sample(0, 2) = (one_sample(0, 2) / cR) * (sR);

	// update the window content
	if (numWritten < N)
	{
		window.row(numWritten) = noisy_sample.row(0);
		numWritten = numWritten + 1;
	}
	else
	{
		for (int i = 0; i < N - 1; i++)
			window.row(i) = window.row(i + 1);
		window.row(N - 1) = noisy_sample;
		numWritten = numWritten + 1;
	}
}

//! get gravity and body acc. components of the window
//! @param &sF:         reference to the sampling frequency
//! @param &window:		reference to the window
//! @param &gravity:	reference to the gravity comp. extracted from the window
//! @param &body:		reference to the body acc. comp. extracted from the window
//! @return:			---
void Classifier::analyzeWindow(int &sF, mat &window, mat &gravity, mat &body)
{
	// perform median filtering to reduce the noise
	int n = 3;
	mat clean_window = window.t();
	medianFilter(clean_window, n);
	clean_window = clean_window.t();

	// discriminate between gravity and body acc. components
	mat tempgr = clean_window.t();
	gravity = ChebyshevFilter(sF, tempgr);
	gravity = gravity.t();
	body = clean_window - gravity;
}

//! compute (trial)point-to-(model)point Mahalanobis distance
//! @param index:		index of the points (in trial and model) to be compared
//! @param &trial:		reference to the trial
//! @param &model:		reference to the model
//! @param &variance:	reference to the model variance
//! @return:			Mahalanobis distance between trial-point and model-point
float Classifier::mahalanobisDist(int index,mat &trial,mat &model,cube &variance)
{
	mat difference = trial.col(index) - model.col(index);
	mat distance = (difference.t() * (variance.slice(index)).i()) * difference;

	return distance(0,0);
}

//! compute the overall distance between the trial and one model
//! @param &Tgravity:	reference to the gravity component of the trial
//! @param &Tbody:		reference to the body acc. component of the trial
//! @param &MODEL:		reference to the model
//! @return:			Mahalanobis overall distance between trial and model
float Classifier::compareOne(mat &Tgravity, mat &Tbody, DYmodel &MODEL)
{
	// extract the subwindow of interest from the trial (same size of the model)
	mat gravity = Tgravity.rows(0, MODEL.size-1);
	mat body = Tbody.rows(0, MODEL.size-1);

	// acquire the relevant data from the model class
	mat MODELgP = MODEL.gP;
	cube MODELgS = MODEL.gS;
	mat MODELbP = MODEL.bP;
	cube MODELbS = MODEL.bS;

	// discard the "time" row from the models
	int numPoints = MODELgS.n_slices;
	gravity = gravity.t();
	body = body.t();

	mat reference_G = zeros<mat>(3, MODELgP.n_cols);
	mat reference_B = zeros<mat>(3, MODELbP.n_cols);
	for (int i = 0; i < 3; i++)
	{
		reference_G.row(i) = MODELgP.row(i + 1);
		reference_B.row(i) = MODELbP.row(i + 1);
	}

	// compute the components distances (gravity; body acc.)
	mat dist = zeros<mat>(numPoints,2);
	for (int i = 0; i < numPoints; i++)
	{
		dist(i,0) = mahalanobisDist(i, gravity, reference_G, MODELgS);
		dist(i,1) = mahalanobisDist(i, body, reference_B, MODELbS);

	}

	// compute the overall distance
	float distanceG = mean(dist.col(0));
	float distanceB = mean(dist.col(1));
	float overall = (MODEL.gravityWeight*distanceG)+(MODEL.bodyWeight*distanceB);

	return overall;
}

//! compute the matching possibility of all the models
//! @param &gravity:		reference to the gravity component of the trial
//! @param &body:			reference to the body acc. component of the trial
//! @param &possibilities:	reference to the models possibilities
//! @return:				---
void Classifier::compareAll(mat &gravity,mat &body, vector<float> &possibilities)
{
	float distance[nbM];

	// compare the features of the trial with those of each model
	for(int i = 0; i < nbM; i++)
    {
		distance[i] = compareOne(gravity, body, set[i]);
        //DEBUG: cout<<distance[i] <<endl;
    }

	// compute the possibilities from the trial_to_model distances
	for(int i = 0; i < nbM; i++)
	{
		possibilities[i] = 1 - (distance[i] / set[i].threshold);
		if (possibilities[i] < 0)
			possibilities[i] = 0;
	}
}

//! set all the classifier variables and load the models
//! @param dF:		name of the dataset to be loaded
//! @return:		---
void Classifier::buildSet(string dF)
{
	string one_HMPn;
	float one_gW;
	float one_bW;
	float one_th;
	DYmodel *one_model;

	// delete the existing models
	set.clear();

	// load the new set of models
	datasetFolder = "./Models/" + dF + "/";
	string fileName = datasetFolder + "Classifierconfig.txt";
	//DEBUG:cout<<"config file: " <<fileName <<endl;
	ifstream configFile(fileName.c_str());
	configFile >>nbM;
	//DEBUG:cout<<"nbM: " <<nbM <<endl;
	for(int i=0; i< nbM; i++)
	{
		configFile>>one_HMPn >>one_gW >>one_bW >>one_th;
		one_HMPn = datasetFolder + one_HMPn;
		//DEBUG:cout<<"DYmodel: " <<one_HMPn <<endl;
		one_model = new DYmodel(one_HMPn, one_gW, one_bW, one_th);
		//DEBUG:one_model->printInfo();
		set.push_back(*one_model);
	}
	configFile.close();

	// compute the size of the window
	int temp_ws = set[0].size;
	for(int i=1; i< nbM; i++)
	{
		if(set[i].size > temp_ws)
			temp_ws = set[i].size;
	}
	window_size = temp_ws;

	// publish the static information (number & names of models) on PEIS
	publishStatic();
}

//! test one file (off-line)
//! @param &one_device: reference to the device used for collecting the data
//! @param testFile:	name of the test file
//! @param resultFile:	name of the result file
//! @return:			---
void Classifier::singleTest(Device &one_device, string testFile, string resultFile)
{
	int nSamples = 0;				// number of samples acquired by the system
	mat actsample;					// current sample in matrix format
	int ax, ay, az;					// accelerometer current sample components
	int gx, gy, gz;					// gyroscope current sample components
	vector<float> possibilities;	// models possibilities

	mat window = zeros<mat>(window_size, 3);
	mat gravity = zeros<mat>(window_size, 3);
	mat body = zeros<mat>(window_size, 3);

	// initialize the possibilities
	for (int i = 0; i < nbM; i++)
		possibilities.push_back(0);

	// create result file
	ofstream outputFile;
	outputFile.open(resultFile.c_str());

	// read recorded data
	char dev;
	string motion;
	ifstream tf(testFile.c_str());
	cout <<"Reading trial: " <<testFile <<endl;
	while (!tf.eof())
	{
		// create the window of samples to be analyzed
		tf>>dev >>ax >>ay >>az >>gx >>gy >>gz >>motion;
		if (!tf)
			break;
		actsample<<ax <<ay <<az;
		createWindow(one_device.codedRange, one_device.sensingRange, actsample, window, window_size, nSamples);
		if (nSamples >= window_size)
		{
			analyzeWindow(one_device.samplingFrequency, window, gravity, body);
			compareAll(gravity, body, possibilities);
			
			// report the possibility values in the results file
			for (int i = 0; i < nbM; i++)
				outputFile<<possibilities[i] <<" ";
			outputFile<<endl;
		}
	}
	tf.close();
	outputFile.close();
}

//! validate one model with given validation trials
//! @param &one_device: reference to the device used for collecting the data
//! @param model:		name of the model to be validated
//! @param dataset:		name of the referring dataset
//! @param numTrials:	number of validation trials to be used
//! @return:			---
void Classifier::validateModel(Device &one_device, string model, string dataset, int numTrials)
{
	// analyze all validating trials one by one
	for (int i = 0; i < numTrials; i++)
	{
		stringstream itos;
		itos<<i+1;
		string trial = model + "_test (" + itos.str() + ").txt";
		string tf = "Validation/" + dataset + "/" + trial;
		string rf = "Results/" + dataset + "/res_" + trial;
		singleTest(one_device, tf, rf);
  	}
}

//! test one recording (off-line real application scenario test)
//! @param &one_device: reference to the device used for collecting the data
//! @param testFile:	name of the test file
//! @return:			---
void Classifier::offlineTest(Device &one_device, string testFile)
{
	string tf = "Validation/longTest/" + testFile;
	string rf = "Results/longTest/res_" + testFile;
	singleTest(one_device, tf, rf);
}

//! publish the static tuples on PEIS
//! @return:	---
void Classifier::publishStatic()
{
	// numModels
	stringstream ntos;
	ntos<<nbM;
	const char* sNumModels = ntos.str().c_str();
	peiskmt_setStringTuple("Bracelet.numModels", sNumModels);
	//DEBUG: cout<<"numModels: " <<sNumModels <<endl;

	// nameModels
	string short_name;
	string allNames;
	int nRemove = datasetFolder.size();
	for(int i=0; i<nbM; i++)
	{
		short_name = set[i].HMPname.erase(0,nRemove);
		//DEBUG: cout<<short_name <<endl;
		allNames = allNames + short_name + " ";
	}
	const char* sNameModels = allNames.c_str();
	peiskmt_setStringTuple("Bracelet.nameModels", sNameModels);
	//DEBUG: cout<<"nameModels: " <<sNameModels <<endl;
}

//! publish the dynamic tuples on PEIS
//! @param &possibilities:	reference to the models possibilities
//! @return:				---
void Classifier::publishDynamic(vector<float> &possibilities)
{
	// possibilities
	string p;
	for(int i=0; i<nbM; i++)
	{
		stringstream ptos;
		ptos<<possibilities[i];
		p = p + " " + ptos.str();
	}
	const char* sPossibilities = p.c_str();
	peiskmt_setStringTuple("Bracelet.possibilities", sPossibilities);
	//DEBUG: cout<<"possibilities: " <<sPossibilities <<endl;

	// identify the models with highest and second-highest possibility
	int best = 0;
	int secondBest = 0;
	for (int i = 1; i < nbM; i++)
	{
		if (possibilities[i] > possibilities[best])
		{
			secondBest = best;
			best = i;
		}
		else if (possibilities[i] > possibilities[secondBest])
			secondBest = i;
	}
	if (possibilities[best] == 0)
		best = -1;
	if (possibilities[secondBest] == 0)
		secondBest = -1;

	// highest
	string highest;				
	if (best == -1)
		highest = "NONE";
	else
		highest = set[best].HMPname;
	const char* sHighest = highest.c_str();
	peiskmt_setStringTuple("Bracelet.highest", sHighest);
	//DEBUG: cout<<"highest: " <<sHighest <<endl;

	// other
	float other;
	if (best == -1)
		other = 1;
	else
		other = 1 - possibilities[best];
	stringstream str_other;
	str_other<<other;
	const char* sOther = str_other.str().c_str();
	peiskmt_setStringTuple("Bracelet.other", sOther);
	//DEBUG: cout<<"highest: " <<sHighest <<" other: " <<sOther <<endl;

	// entropy
	float entropy;
	if (best == -1)
		entropy = -1;
	else if (secondBest == -1)
		entropy = possibilities[best];
	else
		entropy = possibilities[best] - possibilities[secondBest];
	stringstream str_entropy;
	str_entropy<<entropy;
	const char* sEntropy = str_entropy.str().c_str();
	peiskmt_setStringTuple("Bracelet.entropy", sEntropy);
	//DEBUG: cout<<"highest: " <<sHighest <<" entropy: " <<sEntropy <<endl;
}

//! classify real-time raw acceleration samples acquired via USB
//! @param &one_device: reference to the device used for collecting the data
//! @param port:	    USB port for data acquisition
//! @return:		    ---
void Classifier::onlineTest(Device &one_device, char* port)
{
	int nSamples = 0;				// number of samples acquired by the system
	string sample;					// current sample acquired via USB
	mat actsample;					// current sample in matrix format
	int ax, ay, az;					// accelerometer current sample components
	int gx, gy, gz;					// gyroscope current sample components
	char dev;						// flag --> device type
	string motion;					// flag --> level of motion at the wrist
	vector<float> possibilities;	// models possibilities

	mat window = zeros<mat>(window_size, 3);
	mat gravity = zeros<mat>(window_size, 3);
	mat body = zeros<mat>(window_size, 3);

	// initialize the possibilities
	for (int i = 0; i < nbM; i++)
		possibilities.push_back(0);
	
	// set up the serial communication (read-only)
	SerialOptions options;
	options.setDevice(port);
	options.setBaudrate(9600);
	options.setTimeout(seconds(1));
	options.setParity(SerialOptions::noparity);
	options.setCsize(8);
	options.setFlowControl(SerialOptions::noflow);
	options.setStopBits(SerialOptions::one);
	SerialStream serial(options);
	serial.exceptions(ios::badbit | ios::failbit);
	
	// classify the stream of raw acceleration & gyroscope data
	while(peiskmt_isRunning())
	{
		try
		{
			// read the current sample
			getline(serial,sample);
			istringstream stream(sample);
			stream >>dev >>ax >> ay >>az >>gx >>gy >>gz >>motion;
			actsample <<ax <<ay <<az;
			//DEBUG: cout<<"Acquired: " <<ax <<" " <<ay <<" " <<az <<" ";

			// update the window of samples to be analyzed
			createWindow(one_device.codedRange, one_device.sensingRange, actsample, window, window_size, nSamples);
			if (nSamples >= window_size)
			{
				// analyze the window and compute the models possibilities
				analyzeWindow(one_device.samplingFrequency, window, gravity, body);
				compareAll(gravity, body, possibilities);

				// publish the dynamic tuples
				publishDynamic(possibilities); 
			}
		}
		catch(TimeoutException&)
		{
			serial.clear();
			cerr<<"Timeout occurred"<<endl;
		}
	}
}
