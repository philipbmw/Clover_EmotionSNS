<?
require_once('connectvars.php');
//������ �������� fopen�̶�� �Լ��� ����ϰ� �Ǵµ�, fopen �Լ��� ������ ������
// �ִ� �����̳� HTTP ���ӿ� ���� �ٸ� ������ ���� �Ǵ� FTP ���ӿ� ���� �ٸ� ����>�� ���ϵ��� ������ ���� �ִ�. ������ �аų� ����ϱ� ���� fopen �Լ��� ����Ѵ�.
//fopen("FileName", "Mode");
//$fp�� ���� ������
//$fp = fopen("test.txt", "w");
//$fp = fopen("http://naver.com", "r"); index�� �б� �������� ����
//$fp = fopen("ftp://id@pass:naver.com,"r"); ftp://naver.com �� �б� �������� ��
//
session_start();
$error_msg-"";
$fp = fopen("test.txt", "r");
//fgets(����������,����) ���� ������ �о���� �Լ�
//�� �Լ��� �⺻������ 1����Ʈ�� �Ҹ��ϹǷ� 2����Ʈ�� �б����ؼ��� ���̸� 2��
//test.txt�� ��� �о���� ����
//while($file = fgets($fp,2)){
//      echo $file;
//}
//echo "<br>";
while($file = fgets($fp,2)){
        $result .= $file;
}

//������ ������ ���������Ͱ� �Դ°�?
//echo feof($fp);
//���������͸� �ݴ´�.
fclose($fp);

echo '$result';


?>