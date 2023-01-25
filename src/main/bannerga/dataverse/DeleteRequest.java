package bannerga.dataverse;

import bannerga.config.ConfigBuilder;
import io.netty.handler.codec.http.HttpMethod;
import net.minidev.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class DeleteRequest implements WebApiRequest {

    /***
     *
     * @param entityUrl the full address for the delete request including the service root (returned by a successful post request)
     * @param body Not used for a DELETE request implementation. Should be null and will be ignored.
     * @return json object with the response code of 204 when successful
     * @throws IOException
     */
    @Override
    public JSONObject send(String entityUrl, String body) throws IOException {
        var config = ConfigBuilder.build();
        var url = new URL(entityUrl);
        var connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(HttpMethod.DELETE.toString());
        connection.setDoOutput(true);
        connection.setRequestProperty("OData-MaxVersion", "4.0");
        connection.setRequestProperty("OData-Version", "4.0");
        connection.setRequestProperty("Prefer", "odata.include-annotations=*");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + getToken());
        connection.connect();

        var json = new JSONObject();
        int expectedSuccessCode = HttpURLConnection.HTTP_NO_CONTENT;
        if (connection.getResponseCode() == expectedSuccessCode) {
            json.put("code", connection.getResponseCode());
        }
        return json;
    }
}
