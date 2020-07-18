#include <iostream>
using namespace std;

void f(int& a, int&b){
	cout<<a+b;
}

int main(){
	int a=1,b=2;
	f(a,b);
	return 0;
}
