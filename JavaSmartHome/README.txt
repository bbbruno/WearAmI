Compile: javac -classpath HermiT.jar *.java

Run: java -classpath HermiT.jar; OWLSmartHome
Important: mantain the ";" after the jar name. 

Options: 
nocontext: recognized contexts are not displayed
nosituation: recognized situations are not displayed
removetime=t: set the interval life (in seconds). This property determines how long a configuration of sensors is kept in memory to be recognized as part of a future situation before being deleted (default t=180).

Example: 
java -classpath HermiT.jar; OWLSmartHome nocontext removetime=300

Configuration:
configSH.txt contains the following parameters:
removeTime=int - t: set the interval life (in seconds). This property determines how long a configuration of sensors is kept in memory to be recognized as part of a future situation before being deleted (default t=180).
sleepScale=int - 
numPIR=int - number of PIRs
numItem=int - number of item sensors
numDoor=int - number of door sensors
numStoves=int - number of stove sensors
numFaucets=int - number of faucet sensors
numPhones=int - number of phone sensors