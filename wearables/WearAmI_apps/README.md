# Wearables Apps Installation Guide
The following installation guide has been tested on Windows 8.1 (x64).

## 1. Smartphone WearAmI app installation guide
1. Download and install Android Studio IDE (it requires Java JDK 7 or higher)
2. Activate the developer mode on the smartphone
3. Enable USB debugging on the smartphone
4. Make sure the smartphone has Internet access
5. Connect the smartphone to the PC via USB and select PTP as connection method
6. With the **Android SDK Manager** within Android Studio, download the **Google USB Driver**
7. Manually install the driver, instructions can be found here: http://visualgdb.com/tutorials/android/usbdebug/manualinstall.php
8. Download the WearAmI package from the GitHub repository **bbbruno/WearAmI**
9. Import the **wearables_apps** project in Android Studio
10. Build the project
11. Run the *WearAmI (mobile)* app
12. When prompted on the smartphone, accept the connection to the PC

The WearAmI app opens on the smartphone and is listed among the installed apps.

## 2. Smartwatch WearAmI app installation guide
1. Pair the smartwatch to the smartphone, instructions can be found here: https://support.google.com/androidwear/answer/6056630?hl=en
2. Activate the developer mode on the smartwatch
3. Enable ADB debugging on the smartwatch
4. Connect the smartwatch to the PC via USB
5. In Android Studio, build the **wearables_apps** project
6. Run the *WearAmI (wear)* app
7. When prompted on the smartwatch, accept the connection to the PC

The WearAmI app opens on the smartwatch and is listed among the installed apps.

## 3. Smartwatch WearAmI app usage guide
1. Pair the smartwatch to the smartphone (only to make sure its date & time are properly set)
2. Launch the smartwatch WearAmI app
3. Tap on the "Start" button (the on-screen text displays the number of recordings written so far)
4. Once done with the recording, tap on the "Stop button" (the on-screen text displays "NON SCRIVO")
5. To download the log file, connect the smartwatch to the PC via USB
6. In Android Studio, open the **Android Device Monitor** (Tools-->Android-->Android Device Monitor)
7. Open the **File Explorer** tab: the log file is in the folder */mnt/shell/emulated/0/*:
![](https://github.com/bbbruno/WearAmI/blob/master/images/ss_useWearable.png)
