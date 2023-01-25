package bannerga.dataverse;

import bannerga.config.ConfigBuilder;
import io.netty.handler.codec.http.HttpMethod;
import net.minidev.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PostRequest implements WebApiRequest {


    /***
     * @param queryString Name of the entity to be added
     * @param body  json content containing the field names for the entity
     * @return json object that when successful will contain the success code ('code') & entity id ('OData-EntityId')
     * @throws IOException if request cannot be sent or response code cannot be obtained
     */
    @Override
    public JSONObject send(String queryString, String body) throws IOException {
        var config = ConfigBuilder.build();
        var url = new URL(config.serviceRoot() + queryString);
        var connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(HttpMethod.POST.toString());
        connection.setRequestProperty("OData-MaxVersion", "4.0");
        connection.setRequestProperty("OData-Version", "4.0");
        connection.setRequestProperty("Prefer", "odata.include-annotations=*");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + getToken());
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream outputStream = connection.getOutputStream()) {
            byte[] input = body.getBytes(StandardCharsets.UTF_8);
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
}
