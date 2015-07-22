# peislogger Installation Guide
The following installation guide has been tested on Ubuntu 12.04.5 LTS.

*peislogger requires PEIS middleware to be installed.*

1. Download peislogger
2. Move it to the desired folder (e.g. 'Documents/') and extract it
3. Open a terminal window and issue the following commands:
4. `cd [extraction_folder]/peislogger`
5. `cmake .`
6. `make`

To run, open a terminal window and issue the following commands:

1. `cd [extraction_folder]/peislogger`
2. `./peislogger --log [log_file_name]`

For example: `./peislogger --log ../log_test.txt`

