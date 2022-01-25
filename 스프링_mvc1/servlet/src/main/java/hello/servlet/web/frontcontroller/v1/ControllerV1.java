package hello.servlet.web.frontcontroller.v1;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ControllerV1 {

	void process(HttpServletResponse response, HttpServletRequest request) throws ServletException, IOException;
}
