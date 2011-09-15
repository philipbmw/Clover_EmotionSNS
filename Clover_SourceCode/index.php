<!doctype html> 
<html lang="ko"> 
<head> 
<meta charset="utf-8"> 
<meta name="viewport" content="width=device-width,initial-scale=1.0,maximum-scale=1.0,minimum-scale=1.0,user-scalable=no"> 

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
    
}
/* Get user access tokens out of the session. */
$access_token = $_SESSION['access_token'];

/* Create a TwitterOauth object with consumer/user tokens. */
$connection = new TwitterOAuth(CONSUMER_KEY, CONSUMER_SECRET, $access_token['oauth_token'], $access_token['oauth_token_secret']);

/* If method is set change API call made. Test is called by default. */
$content = $connection->get('account/verify_credentials');
$result = $connection->get('users/show', array('screen_name' => $content->screen_name));

$username = $result->name;
$screen_name = $result->screen_name;
$userimage = $result->profile_image_url;
$tweetcount = $result->statuses_count;
$friednscount = $result->friends_count;
$followers_count = $result->followers_count;

$userid = $result->id;

$_SESSION['user_id'] = $userid;


?>

<style> 

section{display:block;margin-bottom:10px;border:1px dashed #f33;background:#9ee6db;}
section:target{background:#ff9}
section h1{margin:10px;font-size:20px;}

</style>

<article id =hgroup2><h4><center> &#10084Home&#10084</center></hgroup2></h4>

<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.2/jquery.min.js"></script>
<script type="text/javascript">

$(document).ready(function()
{
$("#contentbox").keyup(function()
{
var box=$(this).val();
var main = box.length *100;
var value= (main / 140);
var count= 140 - box.length;

if(box.length <= 140)
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
/*
 *#contentbox
 *{
 *width:450px; height:50px;
 *border:solid 2px #006699;
 *font-family:Arial, Helvetica, sans-serif;
 *font-size:14px;
 *}
 */
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
</head>

<body>


<form method="POST" action="index.php">
<?php
 if (isset($_SESSION['access_token'])) {
	 ?>



<div class="content"> 
<section id="content">
<table>
<tr>
<th>
<a title="<?php echo $name;?>" href="http://www.twitter.com/<?php echo $url;?>"><img class="photo-img" src="<?php echo $userimage?>" border="0" alt="" width="80" /></a>  

<?php
 echo "<br>$username<br>";
 echo "$screen_name<br>";
 echo "Tweets : $tweetcount<br>";
 echo "Following : $friednscount<br>";
 echo "Followers : $followers_count<br>";
?> 
</th>



<th>
<script type="text/javascript">
 function changeCorners(t){
  document.getElementById("results").innerHTML   =  t ;
  document.getElementById("rce").style.width = t ;
}
</script>
<?
 $dbc = mysqli_connect('localhost', 'root', 'apmsetup', 'clover_db'); 

  $query = "select user_id, statistics from user where user_id = '$userid' ";
  $data = mysqli_query($dbc, $query);

while($range = mysqli_fetch_array($data))
	{
?>

<!--Load the AJAX API-->
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript">
    
      // Load the Visualization API and the piechart package.
      google.load('visualization', '1.0', {'packages':['corechart']});
      
      // Set a callback to run when the Google Visualization API is loaded.
      google.setOnLoadCallback(drawChart);
      
      // Callback that creates and populates a data table, 
      // instantiates the pie chart, passes in the data and
      // draws it.
      function drawChart() {

      // Create the data table.
      var data = new google.visualization.DataTable();
      data.addColumn('string', 'Topping');
      data.addColumn('number', 'Slices');
      data.addRows([
        ['Positive', <?echo $range[statistics];?>],
        ['Negative', 100- <?echo $range[statistics];?>],

        
      ]);

      // Set chart options
      var options = {'title':'Emotion Social Network Service Statistics',
                     'width':300,
                     'height':250};

      // Instantiate and draw our chart, passing in some options.
      var chart = new google.visualization.PieChart(document.getElementById('chart_div'));
      chart.draw(data, options);
	  chartArea {left:20, top:0, width:"50%", height:"75%"}
    }
    </script>

    <!--Div that will hold the pie chart-->
    <div id="chart_div"></div>



<?
	}

?>
<th>
 <?php  

//$username = 'burrowuser';

 $following_url = "http://api.twitter.com/1/statuses/friends/" . $screen_name . ".xml";  
 $friends = curl_init();  

 curl_setopt($friends, CURLOPT_URL, $following_url);  
 curl_setopt($friends, CURLOPT_RETURNTRANSFER, TRUE);  

 $twiFriends = curl_exec($friends);  
 $response = new SimpleXMLElement($twiFriends);  


 ?> 
</th>

</tr>

</table>
</section> 

<fieldset> 
<legend><br>Following</legend> 

<?php  
//$username = 'burrowuser';
 $following_url = "http://api.twitter.com/1/statuses/friends/" . $screen_name . ".xml";  
 $friends = curl_init();  
 curl_setopt($friends, CURLOPT_URL, $following_url);  
 curl_setopt($friends, CURLOPT_RETURNTRANSFER, TRUE);  
 $twiFriends = curl_exec($friends);  
 $response = new SimpleXMLElement($twiFriends);  

foreach($response->user as $friends)
{  
     $thumb = $friends->profile_image_url;  
     $url = $friends->screen_name;  
     $name = $friends->name;  
?>  

 <a title="<?php echo $name;?>" href="http://www.twitter.com/<?php echo $url;?>"><img class="photo-img" src="<?php echo $thumb?>" border="0" alt="" width="40" /></a>  

 <?php  
  }  
 ?> 
 

</fieldset> 

<fieldset> 
<legend><br>Followers</legend> 

 <?php  
 $follower_url = "http://api.twitter.com/1/statuses/followers/" . $screen_name . ".xml";  
 $friends = curl_init();  

 curl_setopt($friends, CURLOPT_URL, $follower_url);  
 curl_setopt($friends, CURLOPT_RETURNTRANSFER, TRUE);  

 $twiFriends = curl_exec($friends);  
 $response = new SimpleXMLElement($twiFriends);  

 foreach($response->user as $friends)
 {  
     $thumb = $friends->profile_image_url;  
     $url = $friends->screen_name;  
     $name = $friends->name;  
?>  

 <a title="<?php echo $name;?>" href="http://www.twitter.com/<?php echo $url;?>"><img class="photo-img" src="<?php echo $thumb?>" border="0" alt="" width="40" /></a>  

 <?php  
  }  
 ?> 

</fieldset> 



<?php
}else{
?>
<div class="Textbox">

<div style="margin:50px; padding:10px; ">
<div style="width:80%; height:25px">
<div style="float:left"><b>What's happening?</b></div>
<div id="barbox"><div id="bar"></div></div><div id="count">140</div>
</div>
	<textarea name="contentbox" rows="3" cols="110" id="contentbox" disabled>
	</textarea>
	</br>
    <input type="submit" value="Tweet"; disabled/>

<?php
	}
?>


<script type="text"/javascript">
window.onresize=function() {
	var p = getElementById("contentbox");
	p.style.width = window.screen.width - 100;
}
</script>

</div>

</div>
</body> 

</html>