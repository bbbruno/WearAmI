# PC Configuration for the WearAmI Human Activity Monitoring environment

## 1. Install the OS
*We recommend the installation of Ubuntu 14.04.3 32bit.*

It can be downloaded from here:
http://www.ubuntu.com/download/desktop

A detailed installation guide can be found here:
http://www.ubuntu.com/download/desktop/install-ubuntu-desktop

## 2. Configure the OS
1. Install the g++ compiler and other useful stuff (from terminal):

  `sudo apt-get update`

  `sudo apt-get install build-essential`

2. Install CMake

  CMake is an open-source, cross-platform make system to control the software compilation process with simple platform- and compiler-independent configuration files. Most of the WearAmI tools use CMake to properly define the compilation requirements.

  *We recommend the installation of CMake 2.8.12 or higher.*

  With Ubuntu 14.04.3 you can install CMake 2.8.12 with:

  `sudo apt-get install cmake`

3. Install Doxygen

  Doxygen is probably the most popular software documentation system. It supports a variety of languages (C, C++, Java, Objective-C, Python, ...) and it can generate documentation in HTML, LaTeX and other formats. Most of the WearAmI tools use doxygen to automatically generate the software documentation.

  With Ubuntu 14.04.3 you can install doxygen 1.8 with:

  `sudo apt-get install doxygen`

4. Install Visual Studio Code

  Visual Studio Code is a free and lightweight code editor, available for multiple platforms (Windows, Mac, Linux), which can be easily customized and extended. It can be downloaded from:

  https://code.visualstudio.com/#alt-downloads

  A detailed installation guide can be found here:

  https://code.visualstudio.com/Docs/editor/setup

5. Install git

  git is a very popular and free source code management system, specifically designed for non-linear and distributed development. GitHub is a git repository hosting service, providing a web-based graphical interface for the git repository and a bunch of useful features. WearAmI is a public git repository hosted on GitHub. Visual Studio Code automatically integrates with git, allowing for an easy management of the WearAmI repository.

  With Ubuntu 14.04.3 you can install git with:

  `sudo apt-get install git`

  Detailed setup guides can be found here:

  https://help.github.com/articles/set-up-git/#platform-linux

  and here:

  https://help.github.com/articles/caching-your-github-password-in-git/#platform-linux
