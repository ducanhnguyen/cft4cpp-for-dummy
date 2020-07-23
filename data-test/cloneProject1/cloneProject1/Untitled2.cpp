#include <iostream>
using namespace std;

void f(int& a, int&b);





#include <string>#include <iostream>#include <fstream>#include <stdlib.h>using namespace std;void writeContentToFile(char* path, string content){ofstream myfile;myfile.open(path);myfile << content;myfile.close();}string build = "";bool mark(string append){build += append + "\n";writeContentToFile("C:/Users/admin/Downloads/cft4cpp-for-dummy/data-test/Hieu&Long/cloneProject1/0testdriver_execution.txt", build);return true;}void f(int& a,int& b){mark("{");
	mark("cout<<a+b;"); cout<<a+b;

mark("}");}int main(){try{int a=6;int b=20;f(a,b); }catch(exception& error){build="Phat hien loi runtime";}return 0;}
