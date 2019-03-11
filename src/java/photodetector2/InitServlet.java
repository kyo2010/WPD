/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package photodetector2;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Основная цель сервлета, это закончить запись и освободить ресурсы веб камеры при закрытии программы.
 * 
 * getServletContext().getAttribute("pd") - 
 * Содержит основной класс приложения, который находится в единственном числе.
 * 
 * В jsp определен как:
 * <jsp:useBean id= "pd" scope= "application" class= "photodetector2.WebPhotoDetector" />  
 */
public class InitServlet extends HttpServlet {

  /**
   * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
   * methods.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  protected void processRequest(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    response.setContentType("text/html;charset=UTF-8");
    try (PrintWriter out = response.getWriter()) {
      /* TODO output your page here. You may use following sample code. */
      out.println("<!DOCTYPE html>");
      out.println("<html>");
      out.println("<head>");
      out.println("<title>Servlet InitServlet</title>");      
      out.println("</head>");
      out.println("<body>");
      out.println("<h1>Servlet InitServlet at " + request.getContextPath() + "</h1>");
      out.println("</body>");
      out.println("</html>");
    }
  }

  // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
  /**
   * Handles the HTTP <code>GET</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    processRequest(request, response);
  }

  /**
   * Returns a short description of the servlet.
   *
   * @return a String containing servlet description
   */
  @Override
  public String getServletInfo() {
    return "Init Servlet";
  }// </editor-fold>

  @Override
  public void destroy() {
    /** Завершение записи и освобождение ресурсов камеры */
    if (getServletContext().getAttribute("pd")!=null){
      WebPhotoDetector pd = (WebPhotoDetector)getServletContext().getAttribute("pd");
      pd.destroy();
    }
    super.destroy(); //To change body of generated methods, choose Tools | Templates.    
  }
   
  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config); //To change body of generated methods, choose Tools | Templates.
    /** Сначала думал  здесь иницилизировать основной объект приложения, 
     *  но почему то в разные ClassLoader (для Servlet и JSP) 
     *  и OPEN CV dll не могут быть загружены дважды в разные ClassLoader
     */
    /*WebPhotoDetector pd = null;
    if (getServletContext().getAttribute("pd")==null){
      try{
        pd = new WebPhotoDetector();
        getServletContext().setAttribute("pd", pd);
      }catch(Exception e){
        System.out.println("!!! Error init Servlet !!!");
      }
    }else{
      pd = (WebPhotoDetector)getServletContext().getAttribute("pd");
    }
    if (pd!=null){
      pd.setSERVLET_PATH( getServletContext().getRealPath("WPD-FILES") );                
    };*/
  }      
}
