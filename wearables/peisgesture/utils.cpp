//===============================================================================//
// Name			: utils.cpp
// Author(s)	: Barbara Bruno, Antonello Scalmato
// Affiliation	: University of Genova, Italy - dept. DIBRIS
// Version		: 1.1
// Description	: Frequently used functions (for Creator and Classifier)
//===============================================================================//

#include "libs/DspFilters/ChebyshevI.h"
#include "utils.hpp"


// create a row-vector of the form: start:1:stop
// @param start:	starting value of the interval (fixed increment by 1)
// @param stop:		ending value of the interval
// @return:			row vector of (stop-start+1) elements in [start;stop]
mat createInterval(int start, int stop)
{
	mat vector = zeros<mat>(stop-start+1,1);

	for(int i = 0; i <= stop-start; i++)
		vector(i,0) = start + i;

	return vector;
}

// convert a matrix in MAT format to float format
// @param &matrix:	reference to the matrix to be converted
// @return:			pointer to float format matrix
float** matToFloat(mat &matrix)
{
	int Nrows = matrix.n_rows;
	int Ncols = matrix.n_cols;

	float **floatMatrix = new float*[Nrows];

	for (int i = 0; i < Nrows; i++)
	{
		floatMatrix[i] = new float[Ncols];
		for (int j = 0; j < Ncols; j++)
			floatMatrix[i][j] = matrix(i, j);
	}

	return floatMatrix;
}

// convert a matrix in float format to MAT format
// @param **matrix:	pointer to the matrix to be converted
// @param Nrows:	number of rows in the matrix
// @param Ncols:	number of columns in the matrix
// @return:			MAT format matrix
mat floatToMat(float** matrix, int Nrows, int Ncols)
{
	mat matMatrix = zeros<mat>(Nrows, Ncols);

	for (int i = 0; i < Nrows; i++)
		for (int j = 0; j < Ncols; j++)
			matMatrix(i, j) = matrix[i][j];

	return matMatrix;
}

// compute the median value of a vector
// @param &vector:	reference to the vector to get the median from
// @return:			median of vector
double median(rowvec &vector)
{
	int min = 0;
	rowvec tempVec = vector;

	for (unsigned int i = 0; i < vector.n_cols; i++)
	{
		min = i;
		for (unsigned int j = i; j < vector.n_cols; j++)
		{
			if (tempVec(min) > tempVec(j))
				min = j;
		}

		double temp = tempVec(i);
		tempVec(i) = tempVec(min);
		tempVec(min) = temp;
	}

	return tempVec(vector.n_cols / 2);
}

// perform median filtering on a matrix
// @param &matrix:	reference to the matrix to be filtered
// @param size:		size of the median filter
// @return:			---
void medianFilter(mat &matrix, int size)
{
	unsigned int step = size / 2;
	mat tempMat = matrix;

	for (unsigned int r = 0; r < matrix.n_rows; r++)
	{
		for (unsigned int i = 0; i < matrix.n_cols; i++)
		{
			mat window = zeros<mat>(1, size);

			if (i >= step && i <= (matrix.n_cols - 1 - step))
				window = matrix.submat(r, i - step, r, i + step);
			else
			{
				if (i < step)
					window.submat(0,step,0,size-1) = matrix.submat(r,0,r,step);
				else
					if (i > (matrix.n_cols - 1 - step))
						window.submat(0, 0, 0, step) = 
							matrix.submat(r, i - step, r, matrix.n_cols - 1);
			}
			rowvec medianRow = window.row(0);
			tempMat(r, i) = median(medianRow);
		}
	}

	matrix = tempMat;
}

// apply ChebyshevI filter on a matrix
// @param sF:	    sampling frequency of the accelerometer (in Hz)
// @param matrix:	matrix to be filtered
// @return:			filtered matrix
mat ChebyshevFilter(int sF, mat matrix)
{
	float **floatMatrix = matToFloat(matrix);
	Dsp::SimpleFilter<Dsp::ChebyshevI::LowPass<5>,3> filter;
	int filterOrder = 2;
	int samplingFreq = sF;
	float cutFreq = 0.25;
	float passRipple = 0.001;

	filter.setup(filterOrder, samplingFreq, cutFreq, passRipple);
	filter.process(matrix.n_cols, floatMatrix);
	mat lowpassComponent = floatToMat(floatMatrix, matrix.n_rows, matrix.n_cols);

	return lowpassComponent;
}

//! constructor of class Device
//! @param dF:	folder containing the device config file
Device::Device(string dF)
{
    string one_n;
	float one_sR;
	int one_cR;
	int one_sF;

    datasetFolder = "./Models/" + dF + "/";
	string fileName = datasetFolder + "Deviceconfig.txt";
	//DEBUG:cout<<"config file: " <<fileName <<endl;
	ifstream configFile(fileName.c_str());
	configFile>>one_n >>one_sR >>one_cR >>one_sF;
	//DEBUG:cout<<"Device: " <<one_n <<endl;
	name = one_n;
	sensingRange = one_sR;
	codedRange = one_cR;
	samplingFrequency = one_sF;
}

//! print device information
//! @param:		---
//! @return:	---
void Device::printInfo()
{
	cout<<"Device object information:" <<endl;
	cout<<"name = " <<name <<endl;
	cout<<"sensingRange = " <<sensingRange <<endl;
	cout<<"codedRange = " <<codedRange <<endl;
	cout<<"samplingFrequency = " <<samplingFrequency <<endl;
}
