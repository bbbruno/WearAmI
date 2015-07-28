# metaCSP Framework Installation Guide
The following installation guide has been tested on Ubuntu 12.04.5 LTS.

## 1. metaCSP framework installation guide
1. Open a terminal window and issue the following commands:
2. `cd [install_folder]`
3. `sudo apt-get install git`
4. `git clone https://github.com/FedericoPecora/meta-csp-framework.git`

## 2. Importing the framework in an Eclipse project
1. Open a terminal window and issue the following commands:
2. `sudo apt-get install eclipse`
3. `cd [install_folder]/meta-csp-framework/`
4. `./gradlew eclipse`
5. Open Eclipse IDE
6. Click on **File --> Import...**
7. Select **General --> Existing project into workspace**
8. Select *meta-csp-framework* as root directory for the project to be imported
9. Check the option **Copy projects into workspace**

To test whether the import was successful, run the example

**src/main/java org.metacsp.examples.meta TestContextInference.java**.

## 3. Installing PEISJava
*PEISJava requires PEIS middleware to be installed.*

1. Open a terminal window and issue the following commands:
2. `cd [PEISmiddleware_extraction_folder]/peisecology-master/bindings/PEISJava/G6.1`
3. `ant all`
4. `sudo ant install`

To test whether installation was successful, issue the command: `java -jar dist/PeisJava.jar`

## 4. Creating the PEISWearamiReasoner
1. Copy the file **PEISWearamiReasoner.java** inside the folder

      **[eclipse_workspace]/meta-csp-framework/src/main/java/org/metacsp**
2. Copy the file **testPEIS.ddl** inside the folder **[eclipse_workspace]/meta-csp-framework/domains**
