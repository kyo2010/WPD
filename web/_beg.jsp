<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id= "user" scope= "session" class= "wpd.UserInfo" />  
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Система прокторинга <%=user.WEB_PAGE_CAPTION.equals("")?"":(" - "+user.WEB_PAGE_CAPTION)%></title>
        <link rel="stylesheet" href="css/w3.css?1"/>
        <link rel="stylesheet" href="css/main.css?8"/>
        <script type="text/javascript" src="js/main.js"></script>

    </head>
    <body>

<div class="w3-sidebar w3-bar-block w3-card w3-animate-left" style="display:none" 
     id="mySidebar" style="width:200px;display:none;position:absolute !important;top:0">
  
  
  <div class="w3-container w3-teal">
  <span onclick="w3_close()" onclick1="closeSidebar()" class="w3-button w3-teal w3-display-topright w3-xlarge">x</span>
  <br>
  <div class="w3-padding w3-left">
         <% if (user.IS_LOGIN) { %>
	     <b><%=user.LOGIN%></b>
         <% } else { %>
	     вы неавторизованы
         <% } %>
  </div>
  </div>
  <p/>  
  <% if (user.IS_LOGIN) { %>
    <a href="options.jsp" class="w3-bar-item w3-button">Настройки</a>
    <a href="login.jsp?mode=logoff" class="w3-bar-item w3-button">Выйти</a>
  <% } else { %>
    <a href="login.jsp" class="w3-bar-item w3-button">Войти</a>
  <% } %>
 
</div>

<div id="main">

<div class="w3-teal">
  <div class="w3-container">
    <span>
    <h1>
    <button id="openNav" class="w3-button w3-teal w3-xlarge" onclick="w3_open()">&#9776;</button>  
    <%=user.WEB_PAGE_CAPTION%>    
	</h1>
	</span>	
  </div>
  <div class="w3-bar w3-teal">    
    <a href="index.jsp" class="w3-bar-item w3-hover-orange w3-button">Тест</a>  
    <% if (!user.IS_LOGIN) { %>
      <a href="login.jsp" class="w3-bar-item w3-hover-red w3-button">Войти в систему</a>      
        <!-- <a href="login.jsp" class="w3-bar-item w3-hover-red w3-button">Войти</a> -->
    <% } else { %>
      <a href="users.jsp" class="w3-bar-item w3-hover-blue w3-button">База лиц</a>
      <a href="results.jsp" class="w3-bar-item w3-hover-green w3-button">Записи тестов</a>
      <a href="login.jsp?mode=logoff" class="w3-bar-item w3-hover-red w3-button">Выход</a>    
    <% } %>   
  </div>
</div>

<div class="w3-container">
               