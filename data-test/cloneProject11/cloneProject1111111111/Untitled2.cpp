#include <iostream>
using namespace std;

int f(char c){
	if(c<'h')return 1;
	return 0;
}

int main(){
	int a=1,b=2;
	cout << f('g');
	return 0;
}


