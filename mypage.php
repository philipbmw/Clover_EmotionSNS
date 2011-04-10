<?php
  // Start the session
  require_once('startsession.php');

  // Insert the page header
  $page_title = 'Where opposites attract!';
  require_once('header.php');

  require_once('appvars.php');
  require_once('connectvars.php');

  // Show the navigation menu
  require_once('navmenu.php');
  // Connect to the database
  $dbc = mysqli_connect(DB_HOST, DB_USER, DB_PASSWORD, DB_NAME);

  if (isset($_POST['submit'])) {
    // Grab the profile data from the POST
    $mention = mysqli_real_escape_string($dbc, trim($_POST['mention']));
  
    
      // Make sure someone isn't already registered using this username
      $query = "SELECT * FROM mention WHERE mention = '$mention'";
      $data = mysqli_query($dbc, $query);
      if (mysqli_num_rows($data) == 0) {
        // The username is unique, so insert the data into the database
        $query = "INSERT INTO mention (mention_date, mention ) VALUES (NOW(),'$mention')";
        mysqli_query($dbc, $query);


        mysqli_close($dbc);
        exit();
      }
    
	echo $_POST['mention'];
  }

  mysqli_close($dbc);
?>

  
  <form method="post" action="<?php echo $_SERVER['PHP_SELF']; ?>">
    <fieldset>
      <textarea name = "mention" id="mention" cols="20" rows="10"></textarea>
      
      
     
    </fieldset>
    <input type="submit" value="전송" name="submit" />

	
  </form>


 


<?php
  // Insert the page footer
  require_once('footer.php');
?>


 