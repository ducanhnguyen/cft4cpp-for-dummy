#include <iostream>
#include <cstdlib>
#include <time.h>
using namespace std;

void test(int a, int b){
	if(a>b){
		a++;
	}
	else if(b>a){
		b++;
	}
	else {
		a--;
		b--;
	}
}

int main(){
	cout<<"test"<<endl;
		
	return 0;
}
