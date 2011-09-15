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

  // Connect to the database 
  $dbc = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME); 

  // Retrieve the user data from MySQL
  $query = "SELECT email, mention_date, mention_hit,mention  FROM mention WHERE mention IS NOT NULL ORDER BY mention_date DESC";
  $data = mysqli_query($dbc, $query);
  

  mysqli_close($dbc);
?>


<?php
/**
 * @file
 * User has successfully authenticated with Twitter. Access tokens saved to session and DB.
 */

/* Load required lib files. */
session_start();
require_once('twitteroauth/twitteroauth.php');
require_once('config.php');

/* If access tokens are not available redirect to connect page. */
if (empty($_SESSION['access_token']) || empty($_SESSION['access_token']['oauth_token']) || empty($_SESSION['access_token']['oauth_token_secret'])) {
   // header('Location: ./clearsessions.php');
}
/* Get user access tokens out of the session. */
$access_token = $_SESSION['access_token'];

/* Create a TwitterOauth object with consumer/user tokens. */
$connection = new TwitterOAuth(CONSUMER_KEY, CONSUMER_SECRET, $access_token['oauth_token'], $access_token['oauth_token_secret']);
$timeline = $connection->OAuthRequest("http://api.twitter.com/1/direct_messages.xml","GET",array("count"=>"50"));
$xml = simplexml_load_string($timeline);


$nCmtMsg = $_POST['contentbox'];   
$nCmtMSg = urlencode($nCmtMsg);
$connection->post('statuses/update', array('status' => $nCmtMsg));
//echo $nCmtMsg;

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

<form method="POST" action="index.php">
<textarea name="contentbox" rows="3" cols="110" id="contentbox"></textarea>
<input type="submit" value="Tweet"/>
  </form> 


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
?>

<p>    
 <!--<a href="http://twitter.com/<?php echo $screenName ?>/status/<?php echo $statusId ?>">-->
 <a title="<?php echo $name;?>" href="http://www.twitter.com/<?php echo $url;?>"><img class="photo-img" src="<?php echo $profileimage?>" border="0" alt="" width="40" /></a>  
 <?php echo $createdAt ?></a>     
 <!--<a href="http://twitter.com/<?php echo $screenName ?>">бс</a> -->
 <span class="screen_name"><?php echo $screenName ?></span>
 <span class="user_name">(<?php echo $userName ?>)</span><br/>
 <span class="disp_text"><?php echo $dispText ?></span></p>

<?php
 endforeach; 
?>


