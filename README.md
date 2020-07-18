# CFT4Cpp (desktop version - Fix bug)
Tool for test data generation for C/C++ projects

## Developement environment
* IDE: Eclipse
* Java 8

## Run command line

Syntax:

java -jar cft4cpp -f [tested-functions-file] -p [tested-project]

where, 
- [tested-functions-file]: each function, which is represented by its path, is put in a line. Ex: test.cpp\max(int,int)
- [tested-project]: the absolute path to the tested project. Each project has it own make file.

##### Ubuntu OS: 
+ sudo apt-get install z3 (z3 will be installed in /usr/bin/z3)
+ sudo apt-get install mcpp (mcpp will be installed in /usr/bin/mcpp)
+ Update configuration file (located at /local/setting.properties) to update the right path to gcc, g++, name of make file, etc.
+ Run and enjoy

##### Window OS

## Links
* Tutorial: https://goo.gl/eyw5wQ
* Papers list: https://drive.google.com/drive/folders/0Bx7Ly02_CEI0V00zaTBPWWg4UG8?usp=sharing
* Papers summary: https://docs.google.com/document/d/1g9z2JM2-f7e8fpuuHDLXYhrchh2WEHeQuzENpbVBWmU/edit?pref=2&pli=1
* General Page: https://docs.google.com/spreadsheets/d/1PZsBsQxdqENxF5oKns-StiI-qf4G3M9i7ML7H0lvNJI/edit#gid=0

## Developers
Rd320 room, E3 building, 144 Xuan Thuy street, Ha Noi
