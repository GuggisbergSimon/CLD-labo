## Task 2: Develop a Servlet that uses the Datastore

In this task you will write a test Servlet that writes data to the
Datastore. You will use it later to test the write performance of the
Datastore.

The Servlet shall work as follows:

- The Servlet will be called **DatastoreWrite** and will respond to
  HTTP requests that are sent to the URI path **/datastorewrite**.

- For each HTTP request it receives, the Servlet shall write an
  entity to the datastore.

- The Servlet will receive the data for the entity in the query part
  of the URI of the HTTP request.

  The query part of a URI starts after the question mark '?' and
  continues until the end. It contains a number of field-value pairs
  of the form

        field1=value1&field2=value2&field3=value3

  Example: To add a new entity describing a book to the datastore
  one would send the following URI in the HTTP request:

        https://20200406t150557-dot-labgae.appspot.com/datastorewrite?_kind=book&_key=837094&author=John%20Steinbeck&title=The%20Grapes%20of%20Wrath

- The Servlet should be able to deal with arbitrary field-value
  pairs. Each field-value pair shall become a property of the entity.
  The field name becomes the property name and the field value the
  property value.

- The Servlet should treat the fields named **\_kind** and **\_key**
  specially. The field **\_kind** shall indicate the kind of the
  entity. It is mandatory. The field **\_key** shall contain the key
  name of the entity. It is optional. If not present, the Servlet
  shall let the Datastore generate the key.

Example: When the Servlet is invoked with the URI given above, it
should write an entity of kind **book** with key name **837094** and
the properties **author: John Steinbeck** and **title: The Grapes of
Wrath** to the datastore.

Hints for writing the Servlet:

- We provide a sample Servlet that shows how to use the Datastore API
  to write an entity. See file [DatastoreWriteSimple.java](./appendices/DatastoreWriteSimple.java). You can download it, and **copy-paste** (Not drag and drop) on the servlet directory of your project on IntelliJ.
  You can also update the index.jsp to see your new servlet in the home page list.

- The Datastore API documentation can be found at
  <https://cloud.google.com/appengine/docs/standard/java/datastore/entities>.

- The Javadoc of the Servlet API can be found at
  <http://docs.oracle.com/javaee/7/api/> (Oracle hasn't published the
  Javadoc of version 8 yet, but version 7 remains valid). The Servlet
  API is in the packages javax.servlet.\*.

  Recommendation: To make JavaDoc easier to search use the
  [JavaDoc Search Frame](https://github.com/StevenGBrown/javadoc-search-frame)
  extension for Chrome or Firefox.

- To access the data in the query part of the URI use the methods
  `getParameterNames()` and `getParameter()` in package
  `javax.servlet.http.HttpServletRequest`.

To develop and test the Servlet perform the following steps:

1. Create a new Servlet and write its code.

2. Test the Servlet on your local machine as described in the previous
   task. The test web application server contains a local datastore
   that will receive the data. To examine the data in the datastore
   open the local console at <http://localhost:8080/_ah/admin/>.

3. After you have tested it successfully deploy the Servlet to App Engine. **Note: We have noticed a bug in the datastore viewer. We have found that data that is successfully written to the datastore is not displayed in the viewer. We are investigating the problem.**

4. Test the Servlet on App Engine. To examine the data in the
   datastore open the Cloud Platform console, open the hamburger menu
   and select **Datastore > Entities**.

Deliverables:

```
//TODO - Commit and publish the servlet in your repo, in the 'labo4' directory.
```

```
//TODO - Copy a screenshot of the local and the App Engine console with the Datastore Viewer.
```
