<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Система прокторинга - тест интеграции</title>
    </head>
<body>

<?
  $PORT = 8787;

  if ($_POST['mode']=='start'){
    $ip = $_SERVER['REMOTE_ADDR'];

    $url = "http://".$ip.":".$PORT."/WPD/index.jsp";    
    $params = array(
        'mode' => 'start',
        'name' => $_POST['name'],
        'test' => $_TEST['name'],
        'time' => '60',
    );
    $res = file_get_contents( $url . '?' . urldecode(http_build_query($params)) );
    if ($res!=''){
      echo "ok";
    }else{
      echo "error";
    }
  }

  if ($_POST['mode']=='stop'){
    $ip = $_SERVER['REMOTE_ADDR'];

    $url = "http://".$ip.":".$PORT."/WPD/index.jsp";    
    $params = array(
        'mode' => 'stop',
    );
    $res = file_get_contents( $url . '?' . urldecode(http_build_query($params)) );
    if ($res!=''){
      echo "ok";
    }else{
      echo "error";
    }
  }

  
?> 


  <form action="index.php" method="post" >
  <input name="mode" type="hidden" value="start"/>
    <p>
    <label>Название теста<br></label>    
    <input name="test" type="text" size="40" maxlength="100" value="<?=$_POST['test']?>">
    </p>
    <p>
    <label>Введите ваше имя<br></label>    
    <input name="name" type="text" size="20" maxlength="45" value="<?=$_POST['name']?>">
    </p>
 <p>                
  <input type="submit" name="submit" value="Начать тест"/>
 </p>   
 </form>

 <p/>

  <form action="index.php" method="post" >
  <input name="mode" type="hidden" value="stop"/>
  <p>                
    <input type="submit" name="submit" value="Завершить тест"/>
  </p>   
  </form>

</body>
</html>