# Lab 04 : Google App Engine

- Work in a group of 2 students.
- Duration of this lab is 4 periods.

#### Pedagogical objectives

- Develop and deploy a custom web application on a PaaS infrastructure

- Use a data persistence service

- Conduct performance tests and observe the auto-scaling mechanisms of
  the platform

- Assess how quickly resources (quotas) are used up

#### Tasks

In this lab you will perform a number of tasks and document your
progress in a lab report. Each task specifies one or more deliverables
to be produced. Collect all the deliverables in your lab report. Give
the lab report a structure that mimics the structure of this document.

Prerequisites:

- Google account with Google Cloud Platform enabled

* [Link to validate your email address](https://eur01.safelinks.protection.outlook.com/?url=https%3A%2F%2Fgcp.secure.force.com%2FGCPEDU%3Fvid%3DaGU4M000000HQUa&data=05%7C02%7Cnicolas.glassey%40heig-vd.ch%7C3b5d4f9c421e43b710f708dc379e44c8%7Ca372f724c0b24ea0abfb0eb8c6f84e40%7C0%7C0%7C638446400101091107%7CUnknown%7CTWFpbGZsb3d8eyJWIjoiMC4wLjAwMDAiLCJQIjoiV2luMzIiLCJBTiI6Ik1haWwiLCJXVCI6Mn0%3D%7C0%7C%7C%7C&sdata=5G088eCjE3RC03H3fzpa0e9n0pRBlAw%2FM5pY51P6MMQ%3D&reserved=0)


## Task 0 Set up the development environment on your machine

The setup of the development environment consists of the steps
detailed below. If you run into trouble, please consult the
[Google App Engine Java 11 Quickstart](https://cloud.google.com/appengine/docs/standard/java11/quickstart).

1. Make sure your computer has
   [Java SE Development Kit (JDK) 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
   installed.

   Note : update/set your path environment variable

    ```
    [INPUT]
    java --version
    
    [OUTPUT]
    java 11.0.22 2024-01-16 LTS
    Java(TM) SE Runtime Environment 18.9 (build 11.0.22+9-LTS-219)
    Java HotSpot(TM) 64-Bit Server VM 18.9 (build 11.0.22+9-LTS-219, mixed mode)
    ```

2. Download and install [git](https://git-scm.com/).

3. Download and install the [Google Cloud SDK](https://cloud.google.com/sdk/docs/).

   After you download and install invoke the following commands:

   ```
   gcloud init
   gcloud auth application-default login
   ```
  
   Note : update may requested using "gcloud components update". It can take a while ;(.

4. Install the Cloud SDK `app-engine-java` component:
   ```
   gcloud components install app-engine-java
   ```
5. You must have Maven 3.8 installed. Determine whether Maven 3.8 is
   installed by invoking the following command:

   mvn -v

   If you don't have the proper version of Maven installed:

   - [Download](http://maven.apache.org/download.cgi) Maven 3.8 from
     the Maven website.
   - [Install](http://maven.apache.org/install.html) Maven 3.8 on
     your local machine.

   Note: Linux users may need to download Maven instead of using
   `apt-get install` to install Maven 3.8.

6. Download and install **IntelliJ Ultimate** 2022.3.3 that
   you will find on the
   [Jetbrains IntelliJ download page](https://www.jetbrains.com/fr-fr/idea/download/).
   If you don't have a student license already, you can ask for one
   [here](https://www.jetbrains.com/shop/eform/students).

7. Install the plugin Google Cloud Code for IntelliJ:

   - On the **Welcome to IntelliJ** window, select **Plugins**.
   - Search for [**Google Cloud Code**](https://plugins.jetbrains.com/plugin/8079-gemini--google-cloud-code) and click on **Install**.
   - IntelliJ might have the old **Google App Engine Plugin** bundled. You must disable it if so.


### TODO

* [Task 001 - Deployment of a simple web application](001_SimpleWebAppDeployment.md)
* [Task 002 - Deployment of a servlet](002_SimpleWebAppDeployment.md)
* [Task 003 - Performances](003_DataStorePeformances.md)
