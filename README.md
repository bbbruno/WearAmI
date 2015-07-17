# WearAmI
 Wearable and Ambient Intelligence Make Assistive Robots Smarter
 
## What is it?
WearAmI is a joint research project between the University of Genoa, 
Genoa, IT and Örebro University, Örebro, SWE funded by the Italian 
Ministry of Foreign Affairs and International Cooperation (MAECI) and 
the Italian Ministry of Education, Universities and Research (MIUR) 
under grant no. PGR00193.

## What is it for?
The goal of the WearAmI project is to design, develop and test 
technologies to provide in-home assistive robots with accurate 
information about the status of the person, to be used for: (a) robot 
planning of pro-active actions; (b) validation of the robot actions, on 
the basis of the subsequent person actions.

## Repository overview
TO DO

## Installation
The following installation guide has been tested on Ubuntu 12.04.5 LTS.
### 1. PEIS middleware installation guide
1. Download the middleware from the GitHub repository **mbrx/peisecology**
2. Move it to the desired folder (e.g. 'Documents/') and extract it
3. Open a terminal window and issue the following commands:
4. `sudo apt-get install build-essential`
5. `sudo apt-get install libtool`
6. `sudo apt-get install autotools-dev`
7. `sudo apt-get install automake`
8. `sudo apt-get install zlib1g-dev`
8. `cd [extraction_folder]/peisecology-master/peiskernel/G6`
9. `./autogen.sh`
10. `./configure`
11. `make`
12. `sudo make install`
13. `sudo ldconfig`

To test whether installation was successfull, issue the command:
`peismaster`

### 2. Tupleview installation guide
1. Open a terminal window and issue the following commands:
2. `sudo apt-get install libglade2-dev`
3. `cd [extraction_folder]/peisecology-master/tupleview/G6`
4. `./autogen.sh`
5. `make`
6. `sudo make install`

To test whether installation was successfull, issue the command:
`peismaster & tupleview`

You should see the peismaster component in tupleview.
