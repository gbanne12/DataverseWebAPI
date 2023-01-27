package bannerga.dataverse;

import bannerga.config.ConfigBuilder;
import com.microsoft.aad.msal4j.*;
import net.minidev.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public interface WebApiRequest {

    JSONObject send(String uri, String body) throws IOException, ExecutionException, InterruptedException;

    default String getToken() throws MalformedURLException {
        var config = ConfigBuilder.build();

        // This is the secret that is created in the Azure portal when registering the application
        IClientCredential clientSecret = ClientCredentialFactory.createFromSecret(config.clientSecret());
        ConfidentialClientApplication app =
                ConfidentialClientApplication
                        .builder(config.clientId(), clientSecret)
                        .authority("https://login.microsoftonline.com/" + config.tenantId())
                        .build();

        Set<String> defaultScope = Collections.singleton(config.resourceUrl() + "/.default");
        ClientCredentialParameters parameters =
                ClientCredentialParameters
                        .builder(defaultScope)
                        .build();

        IAuthenticationResult result = app.acquireToken(parameters).join();
        return result.accessToken();
    }

}
