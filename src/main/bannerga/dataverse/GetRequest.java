package bannerga.dataverse;

import bannerga.config.TestConfig;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.openqa.selenium.remote.http.HttpMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetRequest implements WebApiRequest {

    /***
     *
     * @param queryString Name of the entity plus any additional query parameters.
     *                    See https://learn.microsoft.com/en-us/power-apps/developer/data-platform/webapi/query-data-web-api
     * @param ignore   Not used for a GET request implementation. Should be null and will be ignored.
     * @return json object with the dataverse rows contained within the 'value' array
     * @throws IOException if connection to endpoint cannot be established
     */
    @Override
    public JSONObject send(String queryString, String ignore) throws IOException {
        return send(queryString);
    }

    private JSONObject send(String queryString) throws IOException {
        var config = TestConfig.get();
        URL url = new URL(config.serviceRoot() + queryString);

        var token = getToken();
        var connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(HttpMethod.GET.toString());
        connection.setRequestProperty("OData-MaxVersion", "4.0");
        connection.setRequestProperty("OData-Version", "4.0");
        connection.setRequestProperty("Prefer", "odata.include-annotations=*");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + token);

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
            System.out.println("Unable to retrieve any body content from the WebAPI. Ensure there is expected content");
        } catch (ParseException e) {
            System.out.println("Unable to parse the response from the WebAPI: " + e);
        }
        return json;
    }

}
