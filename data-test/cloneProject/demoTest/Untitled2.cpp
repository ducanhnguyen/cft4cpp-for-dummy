#include <iostream>
using namespace std;

int f(int a, int b){
	if(a==0)return b;
	if(b==0)return a;
	if(a>=b)return f(a-b,b);
	return f(a,b-a);
}

int main(){
	int a=1,b=2;
	cout << f(a,b);
	return 0;
}
