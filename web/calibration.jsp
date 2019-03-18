<%@page import="photodetector2.SQLModels.PD_TEST_LOG"%>
<%@page import="java.util.Calendar"%>
<%@page import="photodetector2.SQLModels.PD_TEST"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id= "user" scope= "session" class= "wpd.UserInfo" />  
<jsp:useBean id= "pd" scope= "application" class= "photodetector2.WebPhotoDetector" />  

<%
  if (pd.CURRENT_TEST != null) {
    if (pd.CURRENT_TEST.is_calibrated) {
%>   <h3>Идёт тест.<br>
  <font color="red">Калибровка завершена</font></h3> 
  <p><a href="index.jsp?mode=stop" class="w3-button w3-teal">Закончить тест</a> </p>
  <img src="<%=pd.WEB_PATH_WPD_FILES + pd.CURRENT_TEST.VIDEO_FILE%>/last_view.jpg" width="200px"/> 
  <p/>


<% PD_TEST test = pd.CURRENT_TEST;
  long t = (Calendar.getInstance().getTimeInMillis() - test.START_AT.getTimeInMillis()) / 1000;
%>
Дата тест : <%=test.START_AT.getDateAsYYYYMMDD_andTime("-", ":")%><br/>
Тестируемый : <%=test.FIO%><br/>
Название теста : <%=test.TEST_NAME%><br/>
Время теста : <%=t / 60%> минут <%=(t - t / 60 * 60)%> секунд<br/>       
<p> 
    <a href="index.jsp" class="w3-button w3-teal">Обновить</a> 
</p>
<div class="w3-cell-row">
    <h3>Логи теста</h3>
    <%
      if (pd.CURRENT_TEST != null && pd.CURRENT_TEST.logs != null) {
        for (PD_TEST_LOG log : pd.CURRENT_TEST.logs) {          
          long time = (log.LOG_AT.getTimeInMillis()-test.START_AT.getTimeInMillis())/1000;
          long time_in_video = (log.LOG_AT.getTimeInMillis()-test.START_AT.getTimeInMillis())/( 10*test.FRAME_INTERVAL );
    %>

    <span class="w3-mobile">
        <a href="<%=pd.WEB_PATH_WPD_FILES + log.LOG_IMAGE%>" target="_blank" style="height:280px;width:335px" class="w3-button w3-card w3-container w3-margin-bottom">
            <p>Время на видео : <%=time_in_video/60 %> минут <%=(time_in_video-time_in_video/60*60) %> секунд<br/>          
            Время с начала теста : <%=time/60 %> минут <%=(time-time/60*60) %> секунд<br/>         
            <%=log.LOG_MSG%></p>
            <img src="<%=pd.WEB_PATH_WPD_FILES + log.LOG_IMAGE%>" width="270" />
        </a>
    </span>            
    <%
        }
      }
    } else {
    %>
    <h3><font color="red"> Идёт калибровка...</font><br>
  Пожалуйста, будьте в фокусе камеры.
    </h3> 
   <br>
      <a href="index.jsp?mode=stop" class="w3-button w3-teal">Закончить тест</a> <br/>    
    <%
      
        }
      } else {
    %>Тест не запущен<%
       }
    %>