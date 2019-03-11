<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id= "user" scope= "session" class= "wpd.UserInfo" />  
<% user.WEB_PAGE_CAPTION = "Вход"; %>

<jsp:include page="_beg.jsp" />

<p>
<div class="w3-container w3-card-4" >
  <h3>Результаты тестов</h3>
  <p/>
  
  
</div>
</p>
    

<jsp:include page="_end.jsp" />