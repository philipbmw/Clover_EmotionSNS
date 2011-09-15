<!DOCTYPE html PUBLIC "-//WAPFORUM//DTD XHTML Mobile 1.2//EN" "http://www.wapforum.org/DTD/xhtml-mobile12.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" lang="ko" xml:lang="ko">

<head>
<meta charset="utf-8">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />


<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no, target-densitydpi=medium-dpi">

<link rel="stylesheet" type="text/css" href="style.css" />

<?php

  // Start the session
  require_once('startsession.php');

  // Insert the page header

  require_once('header.php');

  require_once('appvars.php');
  require_once('connectvars.php');

  // Show the navigation menu
  require_once('navmenu.php');

/* Load required lib files. */
session_start();
require_once('twitteroauth/twitteroauth.php');
require_once('config.php');

/* If access tokens are not available redirect to connect page. */
if (empty($_SESSION['access_token']) || empty($_SESSION['access_token']['oauth_token']) || empty($_SESSION['access_token']['oauth_token_secret'])) {
    header('Location: ./clearsessions.php');
}
/* Get user access tokens out of the session. */
$access_token = $_SESSION['access_token'];

/* Create a TwitterOauth object with consumer/user tokens. */
$connection = new TwitterOAuth(CONSUMER_KEY, CONSUMER_SECRET, $access_token['oauth_token'], $access_token['oauth_token_secret']);

$mentions = $connection->OAuthRequest("http://api.twitter.com/1/statuses/friends_timeline.xml","GET",array("count"=>"100"));
$xml = simplexml_load_string($mentions);

$nCmtMsg = $_POST['contentbox'];   
$nCmtMSg = urlencode($nCmtMsg);
$connection->post('statuses/update', array('status' => $nCmtMsg));

?>
  <article id =hgroup2><h4><center> &#10084Timeline&#10084</center></hgroup2></h4>

<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
 
    
     <script type="text/javascript">
 $(document).ready(function()
{
$("#contentbox").keyup(function()
{
var box=$(this).val();
var main = box.length *100;
var value= (main / 145);
var count= 145 - box.length;

if(box.length <= 145)
{
$('#count').html(count);
$('#bar').animate(
{
"width": value+'%',
}, 1);
}
else
{
alert('Full');
}
return false;
});

});
</script>
<style>
textarea {width:80%; height:100px; border:solid 2px #006699; font-family:Arial, Helvetica, sans-serif; font-size:14px;}
//#contentbox {height:50px;}
#bar {background-color:#5fbbde; width:0px; height:16px;}
#barbox {float:right; height:16px; background-color:#FFFFFF; width:100px; border:solid 2px #000; -webkit-border-radius:5px;-moz-border-radius:5px;}
#count {float:right; margin-right:8px; font-family:'Georgia', Times New Roman, Times, serif; font-size:16px; font-weight:bold; color:#666666}

body
{
font-family:Arial, Helvetica, sans-serif;
}
#contentbox
{
width:450px; height:50px;
border:solid 2px #006699;
font-family:Arial, Helvetica, sans-serif;
font-size:14px;

}
#bar
{
background-color:#5fbbde;
width:0px;
height:16px;
}
#barbox
{
float:right; 
height:16px; 
background-color:#FFFFFF; 
width:100px; 
border:solid 2px #000; 
margin-right:3px;
-webkit-border-radius:5px;-moz-border-radius:5px;
}
#count
{
float:right; margin-right:8px; 
font-family:'Georgia', Times New Roman, Times, serif; 
font-size:16px; 
font-weight:bold; 
color:#666666
}

</style>

<body>


<div style="margin:50px; padding:10px; width:460px">
<div style="height:25px">

<div style="float:left"><b>What's happening?</b></div>

<div id="barbox"><div id="bar"></div></div><div id="count">140</div>
</div>

<form method="POST" onload="pagereload()">
<textarea name="contentbox" rows="3" cols="110" id="contentbox"></textarea>
<input type="submit" value="Tweet"  />
  </form> 

<script language="JavaScript">
<--새로고침 스크립트
function pagestart() {
window.setTimeout("pagereload()", 30000);
}
function pagereload() {
location.reload();
}
//스크립트끝-->
</script>

<script type="text"/javascript">
window.onresize=function() {
	var p = getElementById("contentbox");
	p.style.width = window.screen.width - 100;
}

</script>

</div>

</body> 

  
<?php
  foreach ($xml->status as $status) : 
?>

  
<?php     


  $statusId = $status->id;
  $screenName = $status->user->screen_name;
  $createdAt = date("Y/m/d H:i:s", strtotime($status->created_at));
  $userName = $status->user->name;
  $dispText = preg_replace("/(http:\/\/[\w\d\/%#$&?()~_.=+-]+)/"," <a href='\\1'>\\1</a> ",  $status->text);
  $profileimage = $status->user->profile_image_url; 
  
  //echo $_SESSION['user_id'];
  
  $dbc = mysqli_connect('localhost', 'root', 'apmsetup', 'clover_db'); 
  $dbc->query("SET NAMES 'utf8'");
  
  $query = "INSERT INTO user_${_SESSION['user_id']} (textid) VALUES('$statusId')";

  $data = mysqli_query($dbc, $query);
  
  mysqli_close($dbc);


?>

<?php
 endforeach;    
?>


<?php

    $dbc = mysqli_connect('localhost', 'root', 'apmsetup', 'clover_db'); 
    $dbc->query("SET NAMES 'utf8'");
   
  // Retrieve the user data from MySQL

  $query = "select T.userimage, T.screenname,T.username,T.text from timeline_tbl T, user_${_SESSION['user_id']} U where T.textid = U.textid ";
 // $query = "select * from timeline_tbl";
  $data = mysqli_query($dbc, $query);

  $query1 = "select * from timeline_tbl order by textid desc";
  $data1 = mysqli_query($dbc, $query1);



while ($row = mysqli_fetch_array($data1)) 
{ 
?>

<?
if ($row["status"]==1){
	
?>
<ol class="red">
</a>
<!-- 사진
<a href="http://117.16.43.144/clover/user.php?id=<?php echo $row[userid] ;?>">
<img class="photo-img" src="<?php echo $row[userimage] ?>" border="0" alt="" width="40" /></a>  
-->
<?php echo $row[date] ?></a>
 <!--<a href="http://twitter.com/<?php echo $screenName ?>">■</a> -->
<span class="screen_name"><?php echo $row[screenname] ?></span>
<span class="user_name">(<?php echo $row[username] ?>)</span><br/>
<span class="disp_text"><?php echo $row[text] ?></span></p>
<?php }?>
</ol>




<?
if ($row["status"]==NULL){
	
?>
<ol class="yellow">
</a>
<!-- 사진
<a href="http://117.16.43.144/clover/user.php?id=<?php echo $row[userid] ;?>">
<img class="photo-img" src="<?php echo $row[userimage] ?>" border="0" alt="" width="40" /></a>  
-->
<?php echo $row[date] ?></a>
 <!--<a href="http://twitter.com/<?php echo $screenName ?>">■</a> -->
<span class="screen_name"><?php echo $row[screenname] ?></span>
<span class="user_name">(<?php echo $row[username] ?>)</span><br/>
<span class="disp_text"><?php echo $row[text] ?></span></p>
<?php }?>
</ol>




<?
if ($row["status"]==-1){
	 
?>
<ol class="blue" id="updates">
</a>
<a href="http://117.16.43.144/clover/user.php?id=<?php echo $row[userid] ;?>">
<img class="photo-img" src="<?php echo $row[userimage] ?>" border="0" alt="" width="40" /></a>  
<?php echo $row[date] ?></a>  

 <a href="http://twitter.com/<?php echo $screenName ?>"></a> 
<span class="screen_name"><?php echo $row[screenname] ?></span>
<span class="user_name">(<?php echo $row[username] ?>)</span><br/>
<span class="disp_text"><?php echo $row[text] ?></span></p>
<?php }?>
</ol>

<?php
} 
?>

<?php
mysqli_close($dbc);
?>





