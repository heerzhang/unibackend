package org.fjsei.yewu;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

//测试； 看看 原始的REST模式。

@Slf4j
@WebServlet(urlPatterns = "/test/*",initParams = {
        @WebInitParam(name = "param1", value = "value1"),
})
public class TestServlet extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        String value=config.getInitParameter("param1");
        log.info("param1:{}",value);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Hell 测试而yiq World</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("Hello");
        out.println("</body>");
        out.println("</html>");
    }
}
