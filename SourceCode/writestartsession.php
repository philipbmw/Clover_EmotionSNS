<?php
  session_start();

  // If the session vars aren't set, try to set them with a cookie
  if (!isset($_SESSION['mention_id'])) {
    if (isset($_COOKIE['mention_id']) && isset($_COOKIE['writer'])) {
      $_SESSION['mention_id'] = $_COOKIE['mention_id'];
      $_SESSION['writer'] = $_COOKIE['writer'];
    }
  }
?>
