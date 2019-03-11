<%@page import="photodetector2.SQLModels.PD_TEST_LOG"%>
<%@page import="java.util.List"%>
<%@page import="photodetector2.SQLModels.PD_TEST"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id= "user" scope= "session" class= "wpd.UserInfo" />  
<jsp:useBean id= "pd" scope= "application" class= "photodetector2.WebPhotoDetector" />  
<% user.WEB_PAGE_CAPTION = "Log"; %>

<% if (!user.isIS_ADMIN()) pageContext.forward("index.jsp"); %>

<jsp:include page="_beg.jsp" />

<%
  String test_id = request.getParameter("test_id");
  if (test_id == null) test_id = "";
  PD_TEST test = null;
  List<PD_TEST_LOG> logs = null;
  try{
    test = PD_TEST.dbControl.getItem(pd.con, "TEST_ID=?", test_id);
    logs = PD_TEST_LOG.dbControl.getList(pd.con, "TEST_ID=? order by LOG_AT desc", test_id);
  }catch(Exception e){}
%>

<p>
<div class="w3-container w3-card-4" >
  <h3>Предупреждения</h3>
  <p/>
  <% if (test!=null) { 
        long t = (test.FINISH_AT.getTimeInMillis()-test.START_AT.getTimeInMillis())/1000;
  %>
    Дата тест : <%=test.START_AT.getDateAsYYYYMMDD_andTime("-", ":") %><br/>
    Тестируемый : <%=test.FIO %><br/>
    Название теста : <%=test.TEST_NAME %><br/>
    Время теста : <%=t/60 %> минут <%=(t-t/60*60) %> секунд<br/>    
    <% if (!test.VIDEO_FILE_FULL.equals("")) { %>
      Видео : <br>
      <video controls height="300">
        <source src="<%=pd.WEB_PATH_WPD_FILES+test.VIDEO_FILE_FULL %>" type="video/mp4">
      </video>         
    <% } %>    
    
    <div class="w3-cell-row">
        <h3>Логи теста</h3>
    <%
    for (PD_TEST_LOG log : logs){
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
  %>
    </div>
        
  <% } %>  
</div>
</p>
    

<jsp:include page="_end.jsp" />