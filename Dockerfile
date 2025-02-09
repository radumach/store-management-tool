# Use Amazon Corretto 21 as the base image
FROM amazoncorretto:21-alpine

# Set the working directory
WORKDIR /app

# # List the contents of the target directory
# RUN ls target

# Copy the JAR file from the build stage
COPY target/*jar store-management-tool.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "store-management-tool.jar"]
