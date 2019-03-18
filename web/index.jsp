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
       int time = 0;
       try{
         time = Integer.parseInt(request.getParameter("time"));
       }catch(Exception e){}
       pd.startAction(user,time);
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
      <div id="testContent"></div>
      <script>
       var request = new XMLHttpRequest();   
       //refresh_content();
       IS_SENT = false;
       setInterval(refresh_content,1000);
     
       function refresh_content() {      
         if (IS_SENT==true) return;
         //var inputText = document.getElementById('testContent').value;
         var data = "";
         //alert(view);
         request.open('GET', 'calibration.jsp');
         request.setRequestHeader('Content-type', 'application/json; charset=utf-8');
         request.send(data);
         IS_SENT = true;
         request.onreadystatechange = function () {
           IS_SENT = false;  
           if (request.readyState === 4) {
               if (request.status === 200) {
                   document.getElementById('testContent').innerHTML=request.responseText;
               } else {
                   alert('Ajax Race error!');
               }
           }
          };
       };       
      </script>   
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
       <label>Максимальное время теста<br></label>    
       <input name="time" type="text" size="20" maxlength="45" value="60"> минут
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