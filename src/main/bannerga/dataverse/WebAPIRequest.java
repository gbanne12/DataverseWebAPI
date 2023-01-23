package bannerga.dataverse;

import bannerga.config.TestConfig;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebAPIRequest {

    public String getToken() throws MalformedURLException, ExecutionException, InterruptedException {
        var config = TestConfig.get();
        String token;
        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            var authContext = new AuthenticationContext("https://login.microsoftonline.com/" + config.tenantId(), false, service);
            var credential = new ClientCredential(config.clientId(), config.clientSecret());
            AuthenticationResult result = authContext.acquireToken(config.resourceUrl(), credential, null).get();
            token = result.getAccessToken();
        } finally {
            assert service != null;
            service.shutdown();
        }
        return token;
    }

    public JSONObject httpRequest(String requestType, String requestBody, URL url, String token) throws IOException {
        var json = new JSONObject();

        var connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(requestType);
        connection.setRequestProperty("OData-MaxVersion", "4.0");
        connection.setRequestProperty("OData-Version", "4.0");
        connection.setRequestProperty("Prefer", "odata.include-annotations=*");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + token);

        // Successful POST Request returns new entity URL in header
        boolean isGetRequest = requestType.equals("GET");
        boolean isPostRequest = requestType.equals("POST");

        if (isPostRequest) {
            return writeContent(connection, requestBody);
        } else if (isGetRequest) {
            return getResponseBody(connection);
        }
        return json;
    }

    private JSONObject writeContent(HttpURLConnection connection, String requestBody) throws IOException {
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            outputStream.write(input, 0, input.length);

            var json = new JSONObject();
            int expectedSuccessCode = HttpURLConnection.HTTP_NO_CONTENT;
            if (connection.getResponseCode() == expectedSuccessCode) {
                json.put("code", connection.getResponseCode());
                json.put("entityId", connection.getHeaderField("OData-EntityId"));
            }
            return json;
        }
    }

    private JSONObject getResponseBody(HttpURLConnection connection) {
        var json = new JSONObject();
        try {
            var inputStream = connection.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String output;
            var response = new StringBuilder();

            while ((output = in.readLine()) != null) {
                response.append(output);
            }
            in.close();
            JSONParser parser = new JSONParser(960);
            json = (JSONObject) parser.parse(response.toString());
        } catch (IOException e) {
            System.out.println("Unable to get response from the WebAPI. Ensure there is expected body content");
        } catch (ParseException e) {
            System.out.println("Unable to parse the response from the WebAPI: " + e.getStackTrace());
        }
        return json;
    }

}
