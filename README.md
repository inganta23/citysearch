# City Search Application

## Overview

The City Search Application is a Spring Boot-based service that provides city suggestions based on user queries and geographical coordinates. It allows users to upload city data and retrieve suggestions efficiently.

## Features

- **City Suggestions**: Get city suggestions based on a search query and optional latitude and longitude.
- **File Upload**: Upload a TSV (Tab-Separated Values) file containing city data to populate the application. The first line/row of the uploaded file must have "name", "lat" and "long" just like example file (cities_canada-usa.tsv).
- **Scoring Mechanism**: The application uses a scoring system to rank city suggestions based on text relevance and proximity to provided coordinates.
- **Error Handling**: Basic error handling for invalid inputs and file types.
- **Swagger Documentation**: Swagger API documentation for easy testing and exploration of endpoints.

## Live Version

You can access the live version of the API at: [City Search API](https://citysearch-dojq.onrender.com)
The API is deployed using render.com (free tier).

## Prerequisites

- **Java 17**: Ensure you have JDK 17 installed on your machine.
- **Maven**: Make sure you have Maven installed to manage dependencies and build the application.
- **Docker** (optional): If you prefer to run the application in a Docker container.

## Package Installation

1. **Clone the Repository**:
   Clone the repository to your local machine using the following command:

   ```bash
   git clone <repository-url>
   cd citysearch
   ```

2. **Install Dependencies**:
   Use Maven to download the required dependencies specified in the `pom.xml` file. Run the following command:

   ```bash
   mvn install
   ```

   This command will download all necessary libraries and packages required for the application.

## Running the Application

### Running Manually

1. **Run the Application**:
   You can run the application directly using Maven without building a JAR file. Use the following command:

   ```bash
   mvn spring-boot:run
   ```

2. **Access the Application**:
   Once the application is running, you can access it at `http://localhost:8080/api/v1/suggestions`.

### Running with Docker

1. **Build the Docker Image**:
   Open your terminal and navigate to the directory containing the `Dockerfile`. Run the following command to build the Docker image:

   ```bash
   docker build -t citysearch .
   ```

2. **Run the Docker Container**:
   After the image is built, you can run the application using the following command:

   ```bash
   docker run -p 8080:8080 citysearch
   ```

   This command maps port 8080 of the container to port 8080 on your host machine.

3. **Access the Application**:
   Once the application is running, you can access it at `http://localhost:8080/api/v1/suggestions`.

## Swagger Documentation

The application includes Swagger for API documentation. You can access the Swagger UI at:

```
http://localhost:8080/swagger-ui/index.html
```

This interactive interface allows you to explore the API endpoints, view request/response models, and test the API directly.

## API Endpoints

- **Get City Suggestions**:

  - **Endpoint**: `GET /api/v1/suggestions`
  - **Parameters**:

    - `q` (optional): The search query for city names.
    - `latitude` (optional): Latitude for proximity scoring.
    - `longitude` (optional): Longitude for proximity scoring.

  - **Example Request**:
    ```bash
    curl "http://localhost:8080/api/v1/suggestions?q=Toronto&latitude=43.7&longitude=-79.42"
    ```

- **Upload City Data**:

  - **Endpoint**: `POST /api/v1/suggestions`
  - **Content-Type**: `multipart/form-data`
  - **Parameters**:

    - `file`: The TSV file containing city data.

  - **Example Request**:
    ```bash
    curl -X POST -F "file=@path/to/cities.tsv" http://localhost:8080/api/v1/suggestions
    ```

## Example `application.properties`

To configure the application, you can create an `application.properties` file in the `src/main/resources` directory with the following content:

```properties
spring.application.name=citysearch

citysearch.textScoreWeight=0.6
citysearch.locationScoreWeight=0.4
server.port=8080
springdoc.api-docs.enabled=true
springdoc.swagger-ui.enabled=true
logging.level.org.springframework=INFO
spring.servlet.multipart.max-file-size=1MB
spring.servlet.multipart.max-request-size=1MB
citysearch.limitData=10
```

## Limitations of the Application

While the City Search Application provides useful features for city suggestions and data uploads, it has certain limitations that users should be aware of:

1. **In-Memory Data Storage**:

   - The application uses a Trie data structure to store city data in memory. This means that all uploaded data is kept in memory and will be lost when the application is stopped or restarted. There is no persistent storage mechanism (like a database) to retain the uploaded data across server restarts.

2. **Initial Data Source**:

   - The application relies on the provided `cities_canada-usa.tsv` file as the initial data source. Each time the server starts, this file is read, and the data is converted into the Trie structure. If the file is not present or is modified incorrectly, the application may not function as intended.

3. **Limited Data Handling**:

   - The application currently does not support advanced data handling features such as data validation, deduplication, or complex queries. Users must ensure that the uploaded TSV files are correctly formatted and contain valid data.

4. **File Size Limitations**:

   - The application has a maximum file size limit for uploads. This may restrict users from uploading larger datasets, which could be a limitation for applications requiring extensive city data.

5. **Error Handling**:

   - While the application includes basic error handling, there may be edge cases or unexpected input formats that are not adequately managed. Users may encounter errors if they provide invalid data formats or parameters.

6. **Scalability**:

   - The current implementation may not scale well with a large number of cities or high-frequency requests. The in-memory Trie structure may lead to increased memory usage, and performance could degrade with a significant amount of data.

7. **No User Authentication**:

   - The application does not implement any user authentication or authorization mechanisms. This means that anyone with access to the API can upload data or query suggestions without restrictions.

8. **Limited API Documentation**:
   - While Swagger is included for API documentation, the documentation may not cover all edge cases or provide detailed examples for every endpoint. Users may need to refer to the source code for more complex use cases.
