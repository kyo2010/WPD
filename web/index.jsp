<%@page import="java.util.List"%>
<%@page import="photodetector2.SQLModels.PD_TEST_LOG"%>
<%@page import="photodetector2.SQLModels.PD_TEST"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.io.File"%>
<jsp:useBean id= "user" scope= "session" class= "wpd.UserInfo" />  
<jsp:useBean id= "pd" scope= "application" class= "photodetector2.WebPhotoDetector" />  

<% user.WEB_PAGE_CAPTION = "Система прокторинга"; %>
<jsp:include page="_beg.jsp" />

<%
   String error_message = null;
   user.LAST_ERROR = "";
   String mode = request.getParameter("mode");
   if (mode == null) mode = "";
   
   if (!pd.IS_INIT){     
     /*
     // For production !!! 
     File dir = new File ( (new File (getServletContext().getRealPath(""))).getParent() );
     File res_dir = new File (dir.getAbsolutePath()+"/WPD-FILES");
     res_dir.mkdirs();
     */ 
          
     // For debug !!! 
     File dir = new File (getServletContext().getRealPath(""));
     // File dir = new File ( (new File (getServletContext().getRealPath(""))).getParent() );
     File res_dir = new File (dir.getAbsolutePath()+"/WPD-FILES");
     res_dir.mkdirs();      
     
     pd.init( res_dir.getAbsolutePath() );                   
   }  
   
   if (mode.equalsIgnoreCase("start") && !pd.testIsStarted()){
     String name = request.getParameter("name");
     String test = request.getParameter("test");
     if (name==null) name = "";
     if (test==null) test = "";
     name = name.trim();
     if (name.equalsIgnoreCase("")){
       error_message = "Please input your name";
     }else{
       user.USER_NAME = name;
       user.TEST_NAME = test;
       pd.startAction(user);
     }     
   }
   if (mode.equalsIgnoreCase("stop")){
     pd.stopAction(user);
   }
%> 

<% if (error_message!=null) { %>
        <p><span class='error'><%=error_message%></span></p>
<% } %>

<p>
  <h3>Система прокторинга</h3>
  <p/>
  <% if (pd.testIsStarted()) { %>
    <h3>Идёт тест</h3>
    <a href="index.jsp?mode=stop" class="w3-button w3-teal">Закончить тест</a> <br/>
    <% PD_TEST test = pd.CURRENT_TEST;
       long t = (Calendar.getInstance().getTimeInMillis()-test.START_AT.getTimeInMillis())/1000;       
    %>
    Дата тест : <%=test.START_AT.getDateAsYYYYMMDD_andTime("-", ":") %><br/>
    Тестируемый : <%=test.FIO %><br/>
    Название теста : <%=test.TEST_NAME %><br/>
    Время теста : <%=t/60 %> минут <%=(t-t/60*60) %> секунд<br/>       
    <p> 
      <a href="index.jsp" class="w3-button w3-teal">Обновить</a> 
    </p>
    <div class="w3-cell-row">
        <h3>Логи теста</h3>
    <%
      if (pd.CURRENT_TEST!=null && pd.CURRENT_TEST.logs!=null){
    for (PD_TEST_LOG log : pd.CURRENT_TEST.logs){
      %>
      
      <span class="w3-mobile">
        <a href="<%=pd.WEB_PATH_WPD_FILES+log.LOG_IMAGE %>" target="_blank" style="height:280px;width:335px" class="w3-button w3-card w3-container w3-margin-bottom">
          <p><%=log.LOG_AT.getDateAsYYYYMMDD_andTime("-", ":") %></p>
          <p><%=log.LOG_MSG %></p>
          <img src="<%=pd.WEB_PATH_WPD_FILES+log.LOG_IMAGE %>" width="270" />
        </a>
      </span>            
      <%
        }
    }    
  %>

    
  <% } else { %>
    <form action="index.jsp" method="post" class="w3-container w3-card-4" >
       <input name="mode" type="hidden" value="start"/>
       <p>
       <label>Название теста<br></label>    
       <input name="test" type="text" size="40" maxlength="100" value="<%=user.TEST_NAME%>">
       </p>
       <p>
       <label>Введите ваше имя<br></label>    
       <input name="name" type="text" size="20" maxlength="45" value="<%=user.USER_NAME%>">
       </p>       
    <p>                
     <input type="submit" name="submit" value="Начать тест" class="w3-button w3-teal" />
    </p>   
    </form>
   <% } %>
  <p/>
  
  
  <!-- <img src="WPD-FILES/last_view.jpg?time=<%=Calendar.getInstance().getTimeInMillis() %>" />  -->

</p>

<jsp:include page="_end.jsp" />