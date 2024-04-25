package ch.heigvd.cld.lab;

import com.google.cloud.datastore.*;

import javax.json.JsonValue;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

/**
 * A servlet that writes an entity to the Google Cloud Datastore for each request it receives.
 */
@WebServlet(name = "DatastoreWrite", value = "/datastorewrite")
public class DatastoreWrite extends HttpServlet {
    /**
     * The Datastore service.
     */
    final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

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
                response.getWriter().println(Json.createObjectBuilder()
                    .add("error", "missing required _kind parameter")
                    .build()
                    .toString());
                return;
            }

            // Extract the properties from the request.
            final Map<String, String[]> parameterMap = new HashMap<>(request.getParameterMap());
            parameterMap.remove("_kind");
            parameterMap.remove("_key");

            // Convert the properties to a map.
            final Map<String, String> properties = parameterMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue()[0]));

            // Save the entity to the datastore.
            final Entity createdEntity = createDatastoreEntity(keyKind, keyName, properties);

            // Write the response.
            if (createdEntity != null) {
                // Return all entities of the kind that was written.
                final List<Entity> entities = getAllEntities(keyKind);

                // Build the JSON response.
                JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
                for (Entity entity : entities) {
                    JsonObjectBuilder objectBuilder = Json.createObjectBuilder();

                    // Add the ID/name and kind to the JSON object.
                    if (entity.getKey().getName() == null) {
                        objectBuilder.add("_key", Json.createValue(entity.getKey().getId()));
                    } else {
                        objectBuilder.add("_key", entity.getKey().getName());
                    }
                    objectBuilder.add("_kind", entity.getKey().getKind());

                    // Add the properties to the JSON object.
                    for (String name : entity.getNames()) {
                        objectBuilder.add(name, entity.getString(name));
                    }

                    arrayBuilder.add(objectBuilder);
                }

                response.getWriter().println(arrayBuilder.build().toString());
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
    private Entity createDatastoreEntity(String keyKind, String keyName, Map<String, String> properties) {
        try {
            final KeyFactory keyFactory = datastore.newKeyFactory().setKind(keyKind);

            // Create a new entity with the properties.
            final IncompleteKey incompleteKey = (keyName == null || keyName.isEmpty()) ? keyFactory.newKey() : keyFactory.newKey(keyName);
            final FullEntity.Builder<IncompleteKey> entityBuilder = FullEntity.newBuilder(incompleteKey);

            properties.forEach(entityBuilder::set);

            final FullEntity<IncompleteKey> entity = entityBuilder.build();
            final Entity createdEntity = datastore.put(entity);

            log("Created entity: " + createdEntity.toString());

            return createdEntity;
        } catch (DatastoreException e) {
            System.err.println("Error: " + e.getMessage());

            return null;
        }
    }

    /**
     * Retrieve all entities of a given kind.
     *
     * @param kind The kind of the entities to retrieve.
     * @return A list of entities.
     */
    public List<Entity> getAllEntities(String kind) {
        Query<Entity> query = Query.newEntityQueryBuilder().setKind(kind).build();
        QueryResults<Entity> results = datastore.run(query);

        List<Entity> entities = new ArrayList<>();
        while (results.hasNext()) {
            entities.add(results.next());
        }

        return entities;
    }
}
