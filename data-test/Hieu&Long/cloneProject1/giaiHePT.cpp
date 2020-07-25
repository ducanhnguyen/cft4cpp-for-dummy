#include <iostream>
using namespace std;

bool check0(int* a, int n){////ktra xem trc so thu i da du so 0 chua...
	if(n==0) return true;
	for(int i=0;i<n;i++){
		if(a[i]!=0) return false;
	}
	return true;
}

void arrangeArray(int** a, int m, int n){
	int tmp = *(*(a+0)+0);
	for(int i=0;i<n;i++){
		*(*(a+0)+i) /= tmp ;
	}
	for(int i=0;i<m;i++){
		bool check = check0(*(a+i),i);
		while(check==false){
			for(int j=0;j<i;j++){
				if(a[i][j]!=0){
					int t = a[i][j];
					for(int k=j;k<n;k++){
						a[i][k] = a[i][k] - t*a[j][k];
					}
					break;
				}
			}
			check = check0(*(a+i),i);
			if(check==true){
				int t = *(*(a+i)+i);
				for(int j=0;j<n;j++){
					*(*(a+i)+j)/=t;
				}
			}
		}
	}
}

int main(){
    int m=0;
    while(m>=0){
    	cout << "Nhap so an cua He Phuong Trinh: ";
    	cin >> m;
    	int n=m+1;
    	int** a;///////////ma tran m*n;
    	a = new int*[m];
		for(int i=0;i<m;i++){
			*(a+i) = new int (n);
			cout << "He so cua PT thu " << i+1 << " : ";
			for(int j=0;j<n;j++){
			cin >> *(*(a+i)+j);
			}
			cout <<"*"<< endl;
		}
		
		arrangeArray(a,m,n);/*//////chuyen thanh` dang:
		1 a b c ....
		0 1 a b c ....
		0 0 1 a b c ....
		*/
		for(int i=0;i<m;i++){
			for(int j=0;j<n;j++){
				cout << a[i][j] << " ";
			}
			cout << endl;
		}
		
//    	cout << a[m-1][n-1];
	}
	

    return 0;
}
