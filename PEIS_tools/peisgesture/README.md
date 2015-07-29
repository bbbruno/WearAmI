# peisgesture Installation Guide
The following installation guide has been tested on Ubuntu 12.04.5 LTS.

The following installation guide has been tested with Boost 1.58.0

The following installation guide has been tested with Armadillo 5.200.2

*peisgesture requires PEIS middleware to be installed.*

## 1. Boost library installation guide
Please check the installation guide provided inside the **peisposture** folder

## 2. Armadillo library installation guide
1. Download Armadillo library from: http://arma.sourceforge.net/
2. Move it to the desired folder (e.g. 'Documents/') and extract it
3. Open a terminal window and issue the following commands:
4. `cd [extraction_folder]`
5. `cmake .`
6. `make`
7. `sudo make install`

## 3. peisgesture installation guide
1. Download peisgesture
2. Move it to the desired folder (e.g. 'Documents/') and extract it
3. Open a terminal window and issue the following commands:
4. `cd [extraction_folder]/peisgesture`
5. `cmake .`
6. `make`

To run, open a terminal window and issue the following commands:

1. `cd [extraction_folder]/peisgesture`
2. `sudo ./peisgesture [op_code] [parameters]`

To display a list of all available functions, issue: `sudo ./peisgesture --help`

Tipically:

`sudo ./peisgesture -B /dev/ttyACM0` --> if the sensing device is directly connected to the port

`sudo ./peisgesture -B /dev/ttyUSB0` --> if the sensing device communicates with an XBee receiver, connected to the port