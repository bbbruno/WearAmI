# peislogger Installation Guide
The following installation guide has been tested on Ubuntu 12.04.5 LTS.

*peislogger requires PEIS middleware to be installed.*

1. Download the WearAmI package from the GitHub repository **bbbruno/WearAmI**
2. Move it to the desired folder (e.g. 'Documents/') and extract it
3. Open a terminal window and issue the following commands:
4. `cd [extraction_folder]/WearAmI/PEIS_tools/peislogger`
5. `cmake .`
6. `make`

To run, open a terminal window and issue the following commands:

1. `cd [extraction_folder]/WearAmI/PEIS_tools/peislogger`
2. `./peislogger --log [log_file_name]`

For example: `./peislogger --log ../log_test.txt`

The configuration for Angen setup (defined in *angen1.conf*) is used by default.

To use a different configuration:

1. Create the corresponding *.conf* file
2. Modify line 10 of *peislogger.c* so that it refers to the new configuration
3. Re-compile
