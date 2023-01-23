package bannerga.config;

public record Config(String clientId,
                     String clientSecret,
                     String tenantId,
                     String resourceUrl,
                     String serviceRoot,
                     String token) { }
