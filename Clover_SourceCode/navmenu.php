<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
        <head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0, user-scalable=no, target-densitydpi=medium-dpi">

<link rel="stylesheet" type="text/css" href="style.css" />

<div id="tabs6">


<ul>
<?php
  // Generate the navigation menu
   
  if (isset($_SESSION['access_token'])) {
    echo '<li class="home"><a href="index.php"><span>Home &#10084;</span></a></li> ';
    echo '<li> <a href="timeline.php"><span>Timeline &#10084;</span></a></li>';
    echo '<li><a href="mentions.php"><span>@Mentions &#10084;</span></a></li> ';
    //echo '<li> <a href="profile.php"><span>Profile &#10084;</span></a></li>';
     echo '<li> <a href="user.php"><span>User &#10084;</span></a></li>';
    //echo '<li> <a href="message.php"><span>Direct Message &#10084;</span></a></li>';
    //echo '<li> <a href="editprofile.php"><span>Edit Profile &#10084;</span></a></li> ';
	//echo '<li> <a href="mypage.php"><span>My Page &#10084;</span></a></li>';
	
	echo '<li> <a href="clearsessions.php"><span>Log Out &#10084;  </span> </a></li>';

  }
  else {
    echo '<li> <a href="redirect.php"><span>Log In &#10084; </span> </a></li> ';
    //echo '<li> <a href="clearsessions.php"><span>Log Out &#10084;  </span> </a></li>';
	
  }
 
?>

</ul>
</div>
<br /><br />
</body>
</html>
