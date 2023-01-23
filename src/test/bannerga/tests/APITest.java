package bannerga.tests;

import bannerga.config.Config;
import bannerga.config.TestConfig;
import bannerga.dataverse.WebAPIRequest;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

public class APITest {
    private static Config config;

    @BeforeAll
    public static void setup() {
        config = TestConfig.get();
    }

    @Test
    public void canMakeGetRequestForAccounts() throws Exception {
        // Given
        var entityName = "accounts";
        var filterString = "?$select=name";
        URL url = new URL(config.serviceRoot() + entityName + filterString);

        // When
        var token = new WebAPIRequest().getToken();
        JSONObject queryResponse = new WebAPIRequest().httpRequest("GET", null, url, token);

        // Then
        JSONArray accounts = (JSONArray) queryResponse.get("value");
        assertTrue(accounts.size() > 0, "Response should return account records");
    }

    @Test
    public void canMakePostRequestForAccount() throws Exception {
        // Given
        var timestamp = String.valueOf(new Timestamp(System.currentTimeMillis()));
        var entityName = "accounts";
        var jsonString = """
                {
                    "@logicalName": "accounts",
                    "@alias": "accounts",
                    "name": "Temporary",
                    "telephone1": "0777 111 1111"
                }
                """;
        var requestBody = jsonString.replaceAll("\\bTemporary\\b", timestamp);
        URL url = new URL(config.serviceRoot() + entityName);

        // When
        var token = new WebAPIRequest().getToken();
        JSONObject response = new WebAPIRequest().httpRequest("POST", requestBody, url, token);

        // Then
        var responseCode = (int) response.get("code");
        var entityId = (String) response.get("entityId");
        assertEquals(responseCode, 204, "Server should return '204: No content' for success");
        assertNotNull(entityId, "Server should return ID for new account");
    }

}
