#include <iostream>
#include <cstdlib>
#include <time.h>
using namespace std;

string S[] = { "NguyenNgocHai", "AiPhiuGiaiKhatVaiLon", "Djtmeban", "Okehaylam"};//"hello", "teag", "love", "john",

void background(){
	cout << "----------Welcome To Hangman 1.0!----------" << endl;
}

string getQuestion(string S[]){//// lây 1 tu trong 1 list string có san S[]
	srand(time(0));
	int len = 4;/////S.size()
	int i = rand() % len;
	return S[i];
}

string getNullAns(string result){
	string question = "";
	for(int i = 0; i < result.length(); i++){
		question += "*";
	}
	return question;
}

void printScore(int wrong, string question){
	cout << "- Number of Wrong Answer: " << wrong << endl;
	cout << "-Your Question: " << question <<  endl;
}

int test(string c){
	if(c.length()>10)return 0;
	return 1;
}

bool checkCharacter(char yourChar, string question, string resultWord){/// question : hien ra man hinh: "-----" .....con resultWord la kqua de ktra
	int n = resultWord.length();
	bool check = false;
	for(int i = 0; i < n; i++){
		if(resultWord[i] == yourChar){
			question[i] = yourChar;
			check = true;
			break;
		}
	}
	return check;
}

bool checkWord(string question, string result){
	if(question == result) return true;
	return false;
}


int main(){
	cout << "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
	background();
	string result = getQuestion(S);
	int wrongAns = 0;
	string question = getNullAns(result);
	char ans;
	while(question != result){
		printScore(wrongAns, question);
		cout << "Your word is: ";
		cin >> ans;
		bool check = checkCharacter(ans, question, result);
		if(check == true){
			for(int i = 0; i < result.length(); i++){
				if(result[i] == ans){
					question[i] = ans;
				}
			}
			if(checkWord(question, result) == true){
				cout << "The Answer is: " << question << endl;
				cout << "-----Congratunation!----- \n---You Win!---" << endl;
				break;
			}
		}else{
			wrongAns++;
			if(wrongAns == 7){
				cout << "---You are lose!---" << endl;
				break;
			}
		}
		cout << "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
	}
		
	return 0;
}
