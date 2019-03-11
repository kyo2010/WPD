<%@page import="photodetector2.SQLModels.PD_SETTING"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id= "user" scope= "session" class= "wpd.UserInfo" />  
<jsp:useBean id= "pd" scope= "application" class= "photodetector2.WebPhotoDetector" />  
<% user.WEB_PAGE_CAPTION = "Настройки"; %>

<jsp:include page="_beg.jsp" />

<% if (!user.isIS_ADMIN()) pageContext.forward("index.jsp"); 

  String error_message = null;
   String mode = request.getParameter("mode");
   if (mode == null) mode = "";
   
   if (mode.equalsIgnoreCase("save")){
     error_message = "Изменения сохранены";
     try{
       PD_SETTING.setParam(pd.con, "TIME_INTERVAL", ""+Integer.parseInt(request.getParameter("TIME_INTERVAL")));              
     }catch(Exception e){
       error_message = "Ошибка сохранения. "+e.getMessage();
     };  
     try{
       if (request.getParameter("ADMIN_PASSWORD1").equals(request.getParameter("ADMIN_PASSWORD"))){
         PD_SETTING.setParam(pd.con, "ADMIN_PASSWORD", request.getParameter("ADMIN_PASSWORD"));       
       }else{
         error_message = "Пароли не совпадают";   
       }
     }catch(Exception e){
       error_message = "Ошибка сохранения. "+e.getMessage();
     }
     
     if ("ON".equals(request.getParameter("HIGHLIGHT_VIDEO"))){
       PD_SETTING.setParam(pd.con, "HIGHLIGHT_VIDEO", "1");       
     }else{
       PD_SETTING.setParam(pd.con, "HIGHLIGHT_VIDEO", "1");           
     }          
     
   }

%>

<% if (error_message!=null) { %>
   <p><span class='error'><%=error_message%></span></p>
<% } %>

<p>
  <h3><%=user.WEB_PAGE_CAPTION %></h3>
  <p/>
    
  <form action="options.jsp" method="post" class="w3-container w3-card-4" >
    <input name="mode" type="hidden" value="save"/>
    <p>
      <label>Время опроса камеры<br></label>    
      <input name="TIME_INTERVAL" type="text" size="20" maxlength="45" value="<%=pd.configTools.getTIME_INTERVAL() %>"> микро сек
    </p>
    
     <p>
      <label>Пароль администратора<br></label>    
      <input name="ADMIN_PASSWORD" type="password" size="20" maxlength="45" value="<%=pd.configTools.getADMIN_PASSWORD()%>">
    </p>
    
    <p>
      <label>Повторите пароль<br></label>    
      <input name="ADMIN_PASSWORD1" type="password" size="20" maxlength="45" value="<%=pd.configTools.getADMIN_PASSWORD()%>">
    </p>
    
     <p>
      <label>Подсвечивать контуры лиц на видео         
      <input name="HIGHLIGHT_VIDEO" type="checkbox" <%=pd.configTools.getHIGHLIGHT_VIDEO()==1?"checked":""%> value="ON"  />
      </label>
    </p>
    
    
    <p>
     <input type="submit" name="submit" value="Сохранить" class="w3-button w3-teal" />
    </p>    
  </form>  
  <p/>
</p>
<p/>
    

<jsp:include page="_end.jsp" />