<!DOCTYPE html PUBLIC "-//WAPFORUM//DTD XHTML Mobile 1.2//EN" "http://www.wapforum.org/DTD/xhtml-mobile12.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" lang="ko" xml:lang="ko">

<head>
<meta charset="utf-8">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />


<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no, target-densitydpi=medium-dpi">

<link rel="stylesheet" type="text/css" href="sty.css" />

<?php
$host_name = "localhost"; 
$user_name = "root"; 
$user_password = "apmsetup"; 
$db_name = "clover"; 

$connect = mysql_connect($host_name,$user_name,$user_password) ; 

mysql_select_db($db_name, $connect) ; 

$result=mysql_query("select * from colortest", $connect);

while ($row = mysql_fetch_array($result)) 
{ 
	
	if ($row["number"]==1){
	?> 
	
	<ol class="red" id="updates">
	
	  <?php echo  $row["text"]; ?>  
		<?php  echo '부정??'; }?>  </ol>



				<?
	if ($row["number"]==0){
					?>
		<ol class="blue">
		<?php echo $row["text"] ; ?> 
		<?php echo'긍정??' ; 
	}?> </ol>
<?
} 
?>
