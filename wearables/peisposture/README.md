# peisposture Installation Guide
The following installation guide has been tested on Ubuntu 12.04.5 LTS.

The following installation guide has been tested with Boost 1.58.0

*peisposture requires PEIS middleware to be installed.*

## 1. Boost library installation guide
1. Download Boost library from: http://www.boost.org/
2. Move it to the desired folder (e.g. 'Documents/') and extract it
3. Open a terminal window and issue the following commands:
4. `cd [extraction_folder]`
5. `sudo mv [boost_folder_name] /usr/local`
6. Extract again the Boost package
7. In the terminal window, issue the following commands:
8. `cd [boost_folder_name]`
9. `./bootstrap.sh --with-libraries=thread,system,date_time --prefix=/usr/local`
10. `sudo ./bjam install`

## 2. peisposture installation guide
1. Download peisposture
2. Move it to the desired folder (e.g. 'Documents/') and extract it
3. Open a terminal window and issue the following commands:
4. `cd [extraction_folder]/peisposture`
5. `cmake .`
6. `make`

To run, open a terminal window and issue the following commands:

1. `cd [extraction_folder]/peisposture`
2. `sudo ./peisposture [port]`

Tipically:

`sudo ./peisposture /dev/ttyACM0` --> if the sensing device is directly connected to the port

`sudo ./peisposture /dev/ttyUSB0` --> if the sensing device communicates with an XBee receiver, connected to the port