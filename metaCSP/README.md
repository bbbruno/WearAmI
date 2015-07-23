# metaCSP Framework Installation Guide
The following installation guide has been tested on Ubuntu 12.04.5 LTS.

## 1. metaCSP framework installation guide
1. Open a terminal window and issue the following commands:
2. `cd [desider_install_folder]`
3. `sudo apt-get install git`
4. `sudo apt-get install eclipse`
5. `git clone https://github.com/FedericoPecora/meta-csp-framework.git`

## 2. Importing the framework in an Eclipse project
1. Open a terminal window and issue the following commands:
2. `cd [desider_install_folder]/meta-csp-framework/`
3. `./gradlew eclipse`
4. Open Eclipse IDE
5. Click on **File --> Import...**
6. Select **General --> Existing project into workspace**
7. Select *meta-csp-framework* as root directory for the project to be imported
8. Check the option **Copy projects into workspace**

To test whether the import was successful, run the example **src/main/java org.metacsp.examples.meta TestContextInference.java**.