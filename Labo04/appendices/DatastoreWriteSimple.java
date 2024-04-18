package ch.heigvd.cld.lab;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "DatastoreWrite", value = "/datastorewrite")
public class DatastoreWriteSimple extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
                         throws ServletException, IOException {
		
        resp.setContentType("text/plain");
        PrintWriter pw = resp.getWriter();
        pw.println("Writing entity to datastore.");
		
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity book = new Entity("book");
        book.setProperty("title", "The grapes of wrath");
        book.setProperty("author", "John Steinbeck");
        datastore.put(book);
    }
}
