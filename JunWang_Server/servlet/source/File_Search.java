import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation File_Search
 * modified form: http://www.runoob.com/servlet/servlet-form-data.html
 * query a file path 
 */
@WebServlet("/File_Search")
public class File_Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public File_Search() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		database.FileItem file[];
		int i;
		database.Query query=new database.Query();
		// 设置响应内容类型
		response.setContentType("text/html;charset=UTF-8");
		
		String path = request.getParameter("path");
		file = query.queryFile(path);
		
		PrintWriter out = response.getWriter();
		
		out.println("<html>\n" +
			    "<head><title>" + "文件目录列表" + "</title></head>\n" +
			    "<h1 align=\"center\">" + "文件目录列表" + "</h1>\n");
		
		if (file.length==0){
			out.println("空目录<br />");
		}
		else{
			for (i=0;i<file.length;i++){
				out.println(file[i].getName()+"<br />");
			}
			out.println(file[i].getName()+"<br />");
		}
		
		out.println("-------------");
		out.println("</body></html>");
		
		query.closeConnection();
	}
	
	// 处理 POST 方法请求的方法
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}