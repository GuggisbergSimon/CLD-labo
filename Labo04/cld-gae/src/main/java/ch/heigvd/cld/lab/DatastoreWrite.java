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
import java.util.Enumeration;

@WebServlet(name = "DatastoreWrite", value = "/datastorewrite")
public class DatastoreWrite extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.setContentType("text/plain");
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Enumeration<String> parameterNames = req.getParameterNames();

        String kind = req.getParameter("_kind");
        if (kind == null) {
            resp.getWriter().println("Missing _kind parameter");
            // GLS: http status code
            return;
        }

        String key = req.getParameter("_key");

        // GLS: Could have written
        // Entity entity = new Entity(kind, key);
        // Read the javadoc to find out how the classes that you use work
        Entity entity;
        if (key == null) {
            entity = new Entity(kind);
        } else {
            entity = new Entity(kind, key);
        }

        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = req.getParameter(paramName);
            // GLS: extract _kind ?
            if (!paramName.equals("_kind")) { // GLS: and key ?
                entity.setProperty(paramName, paramValue);
            }
        }

        datastore.put(entity);
        resp.getWriter().println("Entity written to datastore.");
    }
}
