//===============================================================================//
// Name			: utils.hpp
// Author(s)	: Barbara Bruno, Antonello Scalmato
// Affiliation	: University of Genova, Italy - dept. DIBRIS
// Version		: 1.1
// Description	: Frequently used functions (for Creator and Classifier)
//===============================================================================//

#include <armadillo>
#include <string>

using namespace std;
using namespace arma;

#ifndef UTILS_HPP_
#define UTILS_HPP_

//===============================================================================//
// BASIC MATRIX-HANDLING FUNCTIONS
// create a row-vector of the form: start:1:stop
mat createInterval(int start, int stop);
// convert a matrix in MAT format to float format
float** matToFloat(mat &matrix);
// convert a matrix in float format to MAT format
mat floatToMat(float** matrix, int Nrows, int Ncols);
//===============================================================================//

//===============================================================================//
// FILTERING FUNCTIONS
// compute the median value of a vector
double median(rowvec &vector);
// perform median filtering on a matrix
void medianFilter(mat &matrix, int size);
// apply ChebyshevI filter on a matrix
mat ChebyshevFilter(int sF, mat matrix);
//===============================================================================//

//! definition of the class "device" to define the sensing device specifications
class Device
{
	public:
        string datasetFolder;   //!< folder containing the device config file
        string name;            //!< name of the device
		float sensingRange;     //!< sensing range of the accelerometer (in m/s^2)
		int codedRange;	        //!< bit values used to represent the sensing range
		int samplingFrequency;	//!< sampling frequency of the accelerometer (in Hz)

		//! constructor
		Device(string dF);

		//! print device information
		void printInfo();

		//! destructor
		virtual ~Device(){}
};

#endif
