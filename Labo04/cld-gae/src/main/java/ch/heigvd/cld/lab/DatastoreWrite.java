package ch.heigvd.cld.lab;

import com.google.cloud.datastore.*;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * A servlet that writes an entity to the Google Cloud Datastore for each request it receives.
 */
@WebServlet(name = "DatastoreWrite", value = "/datastorewrite")
public class DatastoreWrite extends HttpServlet {

    /**
     * Get method that writes data to the Datastore.
     *
     * @param request  The HTTP request.
     * @param response The HTTP response
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            final PrintWriter pw = response.getWriter();
            response.setContentType("text/plain");

            // Log system time and the parameters of the request
            log(request.getQueryString());

            // Check if the request contains a _kind and a _key parameter.
            final String keyKind = request.getParameter("_kind");
            final String keyName = request.getParameter("_key");

            if (keyKind == null) {
                pw.println("Error: missing required _kind parameter");
                return;
            }

            // Extract the properties from the request.
            final Map<String, String[]> parameterMap = new HashMap<>(request.getParameterMap());
            parameterMap.remove("_kind");
            parameterMap.remove("_key");

            // Convert the properties to a map.
            final Map<String, String> properties = parameterMap
                    .entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0]));

            // Save the entity to the datastore.
            if (createDatastoreEntity(keyKind, keyName, properties)) {
                pw.println("The entity has been written to the datastore");
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                pw.println("Error: the entity could not be written to the datastore");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (IOException e) {
            // We can't write to the response writer, so we write to the standard error.
            System.err.println("Error: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Create a new entity in the Datastore.
     *
     * @param keyKind    The kind of the entity.
     * @param keyName    The key of the entity. If null, a new key will be generated.
     * @param properties The properties of the entity.
     * @return True if the entity was created successfully, false otherwise.
     */
    private boolean createDatastoreEntity(String keyKind, String keyName, Map<String, String> properties) {
        try {
            final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

            final KeyFactory keyFactory = datastore.newKeyFactory().setKind(keyKind);

            final Key key = (keyName == null || keyName.isEmpty()) ? (Key) keyFactory.newKey() : keyFactory.newKey(keyName);
            final Entity.Builder entityBuilder = Entity.newBuilder(key);

            properties.forEach(entityBuilder::set);

            final Entity entity = entityBuilder.build();
            final Entity createdEntity = datastore.put(entity);

            log("Created entity: " + createdEntity.toString());

            return true;
        } catch (DatastoreException e) {
            System.err.println("Error: " + e.getMessage());

            return false;
        }
    }
}
