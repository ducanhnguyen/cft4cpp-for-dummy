
using namespace std;

void f(int& a, int&b){
	if(a>b){
		cout<<a-b<<endl;
	}
	else cout<<a+b<<endl;
}

int main(){
	int a=1,b=2;
	f(a,b);
	return 0;
}

