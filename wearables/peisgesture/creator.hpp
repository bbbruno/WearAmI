//===============================================================================//
// Name			: creator.hpp
// Author(s)	: Barbara Bruno, Antonello Scalmato
// Affiliation	: University of Genova, Italy - dept. DIBRIS
// Version		: 1.1
// Description	: Human Motion Primitives models creator module (off-line only)
//===============================================================================//

#include "utils.hpp"

#ifndef CREATOR_HPP_
#define CREATOR_HPP_

//! definition of the class "model" of an HMP - static modelling parameters
class STmodel
{
	public:
		string name;			//!< name of the motion primitive
		int nbModellingTrials;	//!< number of trials in the modelling folder
		int nbGravityGaussians;	//!< number of Gaussians modelling gravity
		int nbBodyGaussians;	//!< number of Gaussians modelling body acc.

		//! constructor
		STmodel(string n, int nbMT, int nbGG, int nbBG);

		//! print model information
		void printInfo();

		//! destructor
		~STmodel()
		{
			//DEBUG:cout<<endl <<"Destroying STmodel object" <<endl;
		}
};

//! definition of the class "Creator"
class Creator
{
	private:
		//! concatenate the trials of the modelling dataset along the three axes
		void createSet(int &cR, float &sR, mat &actual_sample, mat &set);

		//! extract gravity and body acc. components from the dataset
		void getFeatures(Device &one_device, string name, int nTr, mat &totGravity, mat &totBody);

		//! create the model of one motion primitive (with GMM+GMR)
		void generateModel(Device &one_device, STmodel &motion);
		
	public:
		string datasetFolder;		//!< folder containing the modelling dataset

		//! constructor
		Creator(string dF);

		//! set datasetFolder
		void setDatasetFolder(string dF);

		//! create the models of all motion primitives
		void generateAllModels(Device &one_device);		

		//! destructor
		~Creator()
		{
			//DEBUG:cout<<endl <<"Destroying Creator object" <<endl;
		}
};

#endif
