# WearAmI
*Wearable and Ambient Intelligence Make Assistive Robots Smarter*
 
## What is it?
WearAmI is a joint research project between the University of Genoa, Italy, and Örebro University, Sweden, partially funded by the Italian Ministry of Foreign Affairs and International Cooperation (MAECI) and the Italian Ministry of Education, Universities and Research (MIUR) under grant no. PGR00193.

## What is it for?
The goal of the WearAmI project is to design, develop and test technologies to provide in-home assistive robots with accurate 
information about the status of the person, to be used for:

1. robot planning of pro-active actions;
2. validation of the robot actions, on the basis of the subsequent person actions.

## Repository overview
- `environmentals`: software for the management of the environmental sensors.
- `middleware`: communication middleware, allowing wearable and environmental sensors to share information among themselves and with the reasoner.
- `PEIS_tools`: a collection of utilities for the middleware.
- `reasoner`: the temporal reasoner used to extract high-level Human Activities information from the sensor's data
- `testbed`: details of the project's testbed. WearAmI can rely on a fully accessorized smart apartment in Örebro (http://www.angeninnovation.se/) and a development setup in the EMAROlab facility of the University of Genoa.
- `wearables`: software for the management of the wearable sensors.
