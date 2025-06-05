package com.app.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration class for test properties and endpoints
 */
public class TestConfig {
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "test-config.properties";
    
    // Initialize properties from the config file
    static {
        try (InputStream input = TestConfig.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new RuntimeException("Unable to find " + CONFIG_FILE);
            }
            properties.load(input);
            
            // Resolve any property placeholders
          //  resolvePropertyPlaceholders();
            
        } catch (IOException ex) {
            throw new RuntimeException("Error loading " + CONFIG_FILE, ex);
        }
    }

    // Base URLs
    public static String getBaseUrl() {
        return properties.getProperty("api.base.url");
    }

    // Auth endpoints
    public static String getTokenUrl() {
        return properties.getProperty("auth.token.url");
    }

    public static String getClientId() {
        return properties.getProperty("auth.client.id");
    }

    public static String getClientSecret() {
        return properties.getProperty("auth.client.secret");
    }

    public static String getGrantType() {
        return properties.getProperty("auth.grant.type");
    }
    
    /**
     * Gets the JSON request body for registration
     * @param workshopCode The workshop code to register for
     * @param userPhone User's phone number (optional, can be null)
     * @param preferredContact User's preferred contact method (optional, can be null)
     * @return JSON string for registration request
     */
    public static String getRegistrationRequestBody(String workshopCode, String userPhone, String preferredContact) {
        StringBuilder json = new StringBuilder();
        json.append("{\n  \"workshopCode\": \"").append(workshopCode).append("\"");
        
        if (userPhone != null) {
            json.append(",\n  \"userPhone\": \"").append(userPhone).append("\"");
        }
        
        if (preferredContact != null) {
            json.append(",\n  \"userPreferredContact\": \"").append(preferredContact).append("\"");
        }
        
        json.append("\n}");
        return json.toString();
    }
    
    /**
     * Gets the JSON request body for registration with default values
     * @param workshopCode The workshop code to register for
     * @return JSON string for registration request with default values
     */
    public static String getRegistrationRequestBody(String workshopCode) {
        return getRegistrationRequestBody(workshopCode, "+49123456789", "EMAIL");
    }

    public static String getAuthAdminUsername() {
        return properties.getProperty("auth.admin.username");
    }

    public static String getAuthAdminPassword() {
        return properties.getProperty("auth.admin.password");
    }

    public static String getAuthUserUsername() {
        return properties.getProperty("auth.user.username");
    }

    public static String getAuthUserPassword() {
        return properties.getProperty("auth.user.password");
    }

    // Registration endpoints
    public static String getRegistrationsEndpoint() {
        return properties.getProperty("api.endpoint.registrations");
    }
    // Registration endpoints
    public static String getRegistrationsByWorkshopCodeEndpoint() {
        return properties.getProperty("api.endpoint.registration.by.workshop");
    }

    public static String getRegistrationByIdEndpoint(int id) {
        return getRegistrationsEndpoint() + "/" + id;
    }

    public static String getRegistrationByWorkshopEndpoint(String workshopCode) {
        return getRegistrationsByWorkshopCodeEndpoint().replace("{workshopCode}", workshopCode);
    }

    public static String getUserRegistrationsEndpoint() {
        return properties.getProperty("api.endpoint.user.registrations");
    }

    // Workshop endpoints
    public static String getWorkshopsEndpoint() {
        return properties.getProperty("api.endpoint.workshops");
    }

    public static String getWorkshopByCodeEndpoint(String code) {
        return getWorkshopsEndpoint() + "/" + code;
    }

    public static String getUpcomingWorkshopsEndpoint() {
        return properties.getProperty("api.endpoint.workshops.upcoming");
    }

    public static String getAvailableWorkshopsEndpoint() {
        return properties.getProperty("api.endpoint.workshops.available");
    }

    // Test data
    public static String getTestWorkshopCode() {
        return properties.getProperty("test.workshop.code");
    }

    public static int getTestRegistrationId() {
        return Integer.parseInt(properties.getProperty("test.registration.id"));
    }

    public static String getTestUserName() {
        return properties.getProperty("test.user.name");
    }

    public static String getTestUserEmail() {
        return properties.getProperty("test.user.email");
    }

    public static String getTestUserPhone() {
        return properties.getProperty("test.user.phone");
    }

    // REST Assured configuration
    public static boolean shouldLogAllRequests() {
        return Boolean.parseBoolean(properties.getProperty("rest.assured.log.all.requests"));
    }

    public static boolean shouldLogAllResponses() {
        return Boolean.parseBoolean(properties.getProperty("rest.assured.log.all.responses"));
    }

    public static String getRestAssuredBaseUri() {
        return properties.getProperty("rest.assured.base.uri");
    }

    public static int getRestAssuredPort() {
        return Integer.parseInt(properties.getProperty("rest.assured.port"));
    }

    public static String getRestAssuredBasePath() {
        return properties.getProperty("rest.assured.base.path");
    }

}
