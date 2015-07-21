﻿# peistrans Installation Guide
The following installation guide has been tested on Ubuntu 12.04.5 LTS.

*peistrans requires peisxbee to be installed.*

*peistrans requires PEIS middleware to be installed.*

1. Download peistrans
2. Move it to the desired folder (e.g. 'Documents/') and extract it
3. Open a terminal window and issue the following commands:
5. `cd [extraction_folder]/peistrans`
6. `cmake .`
7. `make`

To run, open a terminal window and issue the following commands:

1. `cd [extraction_folder]/peistrans`
2. `./peistrans [config_file]`

To run peistrans with the low-level configuration for Angen setup, issue the command:

1. `cd [extraction_folder]/peistrans`
2. `./peistrans ./configs/XBee_Angen.xml`

To modify or create a configuration, follow the instructions in: *xml_format_readme.txt*.
