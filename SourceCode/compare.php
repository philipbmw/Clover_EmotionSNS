<?
require_once('connectvars.php');
//파일을 열기위해 fopen이라는 함수를 사용하게 되는데, fopen 함수는 현재의 서버에
// 있는 파일이나 HTTP 접속에 의한 다른 서버의 파일 또는 FTP 접속에 의한 다른 서버>의 파일들을 가져올 수가 있다. 파일을 읽거나 기록하기 위해 fopen 함수를 사용한다.
//fopen("FileName", "Mode");
//$fp는 파일 포인터
//$fp = fopen("test.txt", "w");
//$fp = fopen("http://naver.com", "r"); index를 읽기 전용으로 열기
//$fp = fopen("ftp://id@pass:naver.com,"r"); ftp://naver.com 을 읽기 전용으로 열
//
session_start();
$error_msg-"";
$fp = fopen("test.txt", "r");
//fgets(파일포인터,길이) 파일 내용을 읽어오는 함수
//이 함수는 기본적으로 1바이트를 소모하므로 2바이트를 읽기위해서는 길이를 2로
//test.txt를 모두 읽어오는 예제
//while($file = fgets($fp,2)){
//      echo $file;
//}
//echo "<br>";
while($file = fgets($fp,2)){
        $result .= $file;
}

//파일의 끝까지 파일포인터가 왔는가?
//echo feof($fp);
//파일포인터를 닫는다.
fclose($fp);

echo '$result';


?>