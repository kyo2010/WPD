<%@page import="photodetector2.SQLModels.PD_USER"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id= "user" scope= "session" class= "wpd.UserInfo" />  
<jsp:useBean id= "pd" scope= "application" class= "photodetector2.WebPhotoDetector" />  
<% user.WEB_PAGE_CAPTION = "База лиц"; %>

<% if (!user.isIS_ADMIN()) pageContext.forward("index.jsp"); 

 String error_message = null;
   String mode = request.getParameter("mode");
   if (mode == null) mode = "";
   
   if (mode.equalsIgnoreCase("del") && request.getParameter("user_id")!=null){
     try{
       pd.deleteUser(Integer.parseInt(request.getParameter("user_id")));
     }catch(Exception e){
       error_message = "Ошибка удаления теста. "+e.getMessage();
     }  
   }

%>

<jsp:include page="_beg.jsp" />

<% if (error_message!=null) { %>
   <p><span class='error'><%=error_message%></span></p>
<% } %>


<p>

    <div class="w3-cell-row">


  
  <%
    List<PD_USER> users = PD_USER.dbControl.getList(pd.con,"1=1 ORDER BY UID desc limit 0,50");
    
    if (users.size()==0){
        %> 
<p>
  <div class="w3-container w3-card-4" >
    <h3>База лиц пуста</h3> 
  </div>
</p>
        <%
    }
    
    for (PD_USER u : users){
      %>
      
      <span class="w3-mobile">
        <a href="<%=pd.WEB_PATH_WPD_FILES+u.UFILE %>" target="_blank" style="width:335px" class="w3-button w3-card w3-container w3-margin-bottom">
        <p><%=u.UNAME %></p>        
        <% if (u.UFILE!=null && !u.UFILE.equals("")) { %>
          <img src="<%=pd.WEB_PATH_WPD_FILES+u.UFILE %>" width="270" />
        <% } %>  
        <p><div class="w3-button w3-red" onclick="javascript:cfm('Вы уверены, что хотите удалить?','users.jsp?mode=del&user_id=<%=u.UID %>')">Удалить</div></p>        
        </a>
      </span>            
      <%
    }    
  %>
  
  </div>
  <p/>
</p>
    

<jsp:include page="_end.jsp" />