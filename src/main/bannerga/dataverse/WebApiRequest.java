package bannerga.dataverse;

import bannerga.config.ConfigBuilder;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import net.minidev.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public interface WebApiRequest {

    JSONObject send(String uri, String body) throws IOException;

    default String getToken() {
        var config = ConfigBuilder.build();
        String token;
        ExecutorService service = null;
        try {
            service = Executors.newFixedThreadPool(1);
            var authContext = new AuthenticationContext("https://login.microsoftonline.com/" + config.tenantId(), false, service);
            var credential = new ClientCredential(config.clientId(), config.clientSecret());
            AuthenticationResult result = authContext.acquireToken(config.resourceUrl(), credential, null).get();
            token = result.getAccessToken();
        } catch (ExecutionException | InterruptedException | MalformedURLException e) {
            throw new RuntimeException("Failed to get obtain a token. Cannot use the WebApi: " + e);
        } finally {
            assert service != null;
            service.shutdown();
        }
        return token;
    }

}
