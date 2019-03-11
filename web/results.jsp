<%@page import="photodetector2.SQLModels.PD_TEST"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="photodetector2.SQLModels.PD_USER"%>
<%@page import="java.util.List"%>
<jsp:useBean id= "user" scope= "session" class= "wpd.UserInfo" />  
<jsp:useBean id= "pd" scope= "application" class= "photodetector2.WebPhotoDetector" />  
<% user.WEB_PAGE_CAPTION = "Видео-записи тестов"; %>

<jsp:include page="_beg.jsp" />

<% if (!user.isIS_ADMIN()) pageContext.forward("index.jsp"); 

  String error_message = null;
   String mode = request.getParameter("mode");
   if (mode == null) mode = "";
   
   if (mode.equalsIgnoreCase("del") && request.getParameter("test_id")!=null){
     try{
       pd.deleteTest(Integer.parseInt(request.getParameter("test_id")));
     }catch(Exception e){
       error_message = "Ошибка удаления теста. "+e.getMessage();
     }  
   }

%>

<% if (error_message!=null) { %>
   <p><span class='error'><%=error_message%></span></p>
<% } %>

<p>
<div class="w3-container w3-card-4" >
  <h3><%=user.WEB_PAGE_CAPTION %></h3> 
  
  <table class="w3-table w3-striped w3-bordered">
   <tr class="w3-teal">
    <th >Дата тест</th>
    <th >Команды</th>
    <th >Тестируемый</th>
    <th >Название теста</th>
    <th >Время теста</th>
    <th >Предупреждений</th>
    <th >Видео</th>    
  </tr>

  
  <%
    List<PD_TEST> tests = PD_TEST.dbControl.getList(pd.con,"1=1 ORDER BY TEST_ID desc limit 0,50");
    
    for (PD_TEST test : tests){
      long t = (test.FINISH_AT.getTimeInMillis()-test.START_AT.getTimeInMillis())/1000;
      %>
        <tr>
           <td><a href="results_log.jsp?test_id=<%=test.TEST_ID %>"><%=test.START_AT.getDateAsYYYYMMDD_andTime("-", ":") %></a></td>
           <td> 
               <a href="results_log.jsp?test_id=<%=test.TEST_ID %>">Подробнее</a>
               <a class="w3-button w3-red" href="#" onclick="javascript:cfm('Вы уверены, что хотите удалить результаты теста?','results.jsp?mode=del&test_id=<%=test.TEST_ID %>')">Удалить</a>           
           </td>
           
           
           <td><%=test.FIO %></td>
           <td><%=test.TEST_NAME %></td>
           <td><%=t/60 %> минут <%=(t-t/60*60) %> секунд</td>
           <td><%=test.LOGS %></td>
           <td style="vertical-align: top">
               <% if (!test.VIDEO_FILE_FULL.equals("")) { %>
               <video controls height="300">
                 <source src="<%=pd.WEB_PATH_WPD_FILES+test.VIDEO_FILE_FULL %>" type="video/mp4">
               </video>         
               <% } %>
           </td>
        </tr>
      <%
    }    
  %>
  
  </table>
  <p/>
</div>
</p>
    

<jsp:include page="_end.jsp" />