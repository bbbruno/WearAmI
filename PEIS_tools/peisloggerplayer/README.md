# peisloggerplayer Installation Guide
The following installation guide has been tested on Ubuntu 12.04.5 LTS.

*peisloggerplayer requires PEIS middleware to be installed.*

1. Download peisloggerplayer
2. Move it to the desired folder (e.g. 'Documents/') and extract it
3. Open a terminal window and issue the following commands:
4. `cd [extraction_folder]/peisloggerplayer`
5. `cmake .`
6. `make`

To run, open a terminal window and issue the following commands:

1. `cd [extraction_folder]/peisloggerplayer`
2. `./peislogger --file [log_file_name]`

For example: `./peislogger --file ../log_test.txt`

