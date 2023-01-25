package bannerga.tests;

import bannerga.dataverse.DeleteRequest;
import bannerga.dataverse.GetRequest;
import bannerga.dataverse.PostRequest;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

public class WebApiRequestTest {


    @Test
    public void canMakeGetRequestForAccounts() throws Exception {
        // Given
        var entityName = "accounts";
        var filterString = "?$select=name";

        // When
        String urlSuffix = entityName + filterString;
        JSONObject response = new GetRequest().send(urlSuffix, null);

        // Then
        JSONArray accounts = (JSONArray) response.get("value");
        assertTrue(accounts.size() > 0, "Response should return account records");

        JSONObject first = (JSONObject) accounts.get(0);
        assertFalse(first.get("name").toString().isEmpty());
    }

    @Test
    public void canMakePostRequestForAccount() throws Exception {
        // Given
        var entityName = "accounts";
        var jsonString = """
                {
                    "@logicalName": "accounts",
                    "@alias": "accounts",
                    "name": "Temporary",
                    "telephone1": "0777 111 1111"
                }
                """;
        var timestamp = String.valueOf(new Timestamp(System.currentTimeMillis()));
        var requestBody = jsonString.replaceAll("\\bTemporary\\b", timestamp);


        // When
        JSONObject response = new PostRequest().send(entityName, requestBody);

        // Then
        var responseCode = (int) response.get("code");
        var entityId = (String) response.get("entityId");
        assertEquals(204, responseCode, "Server should return '204: No content' for success");
        assertNotNull(entityId, "Server should return ID for new account");
    }

    @Test
    public void canMakeDeleteRequestForAccounts() throws Exception {
        // Given
        var entityName = "accounts";
        var jsonString = """
                {
                    "@logicalName": "accounts",
                    "@alias": "accounts",
                    "name": "Temporary",
                    "telephone1": "0777 111 1111"
                }
                """;
        var timestamp = String.valueOf(new Timestamp(System.currentTimeMillis()));
        var requestBody = jsonString.replaceAll("\\bTemporary\\b", timestamp);
        JSONObject postResponse = new PostRequest().send(entityName, requestBody);
        var entityUrl = (String) postResponse.get("entityId");

        // When
        JSONObject deleteResponse = new DeleteRequest().send(entityUrl, null);
        var responseCode = (int) deleteResponse.get("code");
        assertEquals(204, responseCode, "Server should return '204: No content' for success");

    }
}
