<?php
  // Generate the navigation menu
  echo '<hr />';
  if (isset($_SESSION['email'])) {
    echo '<a href="index.php">Home</a> &#10084; ';
    echo '<a href="viewprofile.php">View Profile</a> &#10084; ';
    echo '<a href="editprofile.php">Edit Profile</a> &#10084; ';
	echo '<a href="mypage.php">My Page</a>&#10084';
	echo '<a href="friends.php">Friends</a>&#10084';
	echo '<a href="logout.php">Log Out (' . $_SESSION['email'] . ')</a>';

  }
  else {
    echo '<a href="login.php">Log In</a> &#10084; ';
    echo '<a href="signup.php">Sign Up</a>';
  }
  echo '<hr />';
?>
