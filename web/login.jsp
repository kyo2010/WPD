<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id= "user" scope= "session" class= "wpd.UserInfo" />  
<jsp:useBean id= "pd" scope= "application" class= "photodetector2.WebPhotoDetector" />  
<% user.WEB_PAGE_CAPTION = "Вход"; %>

<%
   String error_message = null;
   String mode = request.getParameter("mode");
   if (mode == null) mode = "";
   
   if (mode.equalsIgnoreCase("logoff")){
     user.logoff();
     pageContext.forward("index.jsp");            
   }
   
   if (mode.equalsIgnoreCase("login")){
     String login = request.getParameter("login");
     String password = request.getParameter("password");
     if (user.login(pd,login, password)){
       response.sendRedirect("index.jsp");
     }else{
       error_message = "Неправильное имя пользователя или пароль";
     }
   }
%>   

<jsp:include page="_beg.jsp" />

    <% if (error_message!=null) { %>
        <p><span class='error'><%=error_message%></span></p>
    <% } %>
	<p></p>

    <form action="login.jsp" method="post" class="w3-container w3-card-4" >
    <input name="mode" type="hidden" value="login"/>
<p>
    <label>Имя для входа<br></label>    
    <input name="login" type="text" size="20" maxlength="45" value="<%=user.LOGIN%>">
    </p>
    
    <p>
    <label>Пароль<br></label>
    <input name="password" type="password" size="20" maxlength="15">
    </p>       

    <p>
     <input type="submit" name="submit" value="Войти" class="w3-button w3-teal" />
    </p>    
  

<jsp:include page="_end.jsp" />