package fish.payara.microprofile.config.extensions.gcp;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import org.jvnet.hk2.annotations.Service;

import fish.payara.microprofile.config.extensions.OAuth2Client;
import fish.payara.microprofile.config.extensions.gcp.model.Secret;
import fish.payara.microprofile.config.extensions.gcp.model.SecretResponse;
import fish.payara.microprofile.config.extensions.gcp.model.SecretsResponse;
import fish.payara.nucleus.microprofile.config.source.extension.ConfiguredExtensionConfigSource;

@Service(name = "gcp-secrets-config-source")
public class GCPSecretsConfigSource extends ConfiguredExtensionConfigSource<GCPSecretsConfigSourceConfiguration> {

    private static final String AUTH_URL = "https://www.googleapis.com/oauth2/v4/token";

    private static final String LIST_SECRETS_ENDPOINT = "https://secretmanager.googleapis.com/v1/projects/%s/secrets";
    private static final String GET_SECRETS_ENDPOINT = LIST_SECRETS_ENDPOINT + "/%s/versions/latest:access";

    private Client client = ClientBuilder.newClient();

    private OAuth2Client authClient;

    @Override
    public void bootstrap() {
        final SignedJWT jwt = buildJwt(
                // issuer
                "test-account@payara-bingo.iam.gserviceaccount.com",
                // scope
                "https://www.googleapis.com/auth/cloud-platform");
        try {
            jwt.sign(new RSASSASigner(getPrivateKey()));
            this.authClient = new OAuth2Client(AUTH_URL, jwt);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | JOSEException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, String> getProperties() {

        final String accessToken = authClient.getAccessToken();

        Map<String, String> results = new HashMap<>();

        final WebTarget secretsTarget = client
                .target(String.format(LIST_SECRETS_ENDPOINT, "payara-bingo"));

        final Response secretsResponse = secretsTarget.request()
                .accept("application/json")
                .header("Authorization", "Bearer " + accessToken).get();

        if (secretsResponse.getStatus() != 200) {
            return results;
        }

        final List<Secret> secrets = secretsResponse
                .readEntity(SecretsResponse.class)
                .getSecrets();

        for (Secret secret : secrets) {
            final String secretName = secret.getName();
            results.put(secretName, getValue(secretName));
        }

        return results;
    }

    @Override
    public String getValue(String propertyName) {
        final String accessToken = authClient.getAccessToken();

        final WebTarget secretTarget = client
                .target(String.format(GET_SECRETS_ENDPOINT, "payara-bingo", propertyName));

        final Response secretResponse = secretTarget
                .request()
                .accept("application/json")
                .header("Authorization", "Bearer " + accessToken)
                .get();

        if (secretResponse.getStatus() != 200) {
            return null;
        }

        final String value = secretResponse
                .readEntity(SecretResponse.class)
                .getPayload()
                .getData();

        new Exception(propertyName + " = " + value).printStackTrace();
        return value;
    }

    @Override
    public boolean setValue(String name, String value) {
        return false;
    }

    @Override
    public boolean deleteValue(String name) {
        return false;
    }

    @Override
    public String getName() {
        return "gcp";
    }

    // Helpers

    private static SignedJWT buildJwt(final String issuer, final String scope) {
        Instant now = Instant.now();
        Instant expiry = now.plus(1, ChronoUnit.MINUTES);

        JWTClaimsSet claims = new JWTClaimsSet.Builder().issuer(issuer).audience(AUTH_URL).issueTime(Date.from(now))
                .expirationTime(Date.from(expiry)).claim("scope", scope).build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).build();

        return new SignedJWT(header, claims);
    }

    private static PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String privateKey = "-----BEGIN PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCJbhfrCIcMcsEL\n6RCTgtv+AHENuh9wKVKLczMh3HrjSrqVYDuE5e/Aiyt2f6U3uf9laeuxZ7MEQv8Y\nXzwxQ+j459aLQiQ11Q575TXhkiAUrHvv7S9qdRa8YthQdoT9heT40tkohYjzxan9\nmHehB0lfbC1S7drEsqp3FKCaaHmh8VOYdCfIx53WwvaPlz9CN3+mI8lh+f5qmEgi\nyAtMLV7IWKYMdtkx205kU0NcfS6VnJo35+GGLnL4aCnhFWPgZVvGewDZ7BAn91OJ\n0UqsXTe0puaGVceM10VNf/L6r6i89ZjCGGIvMSECqulOacmFLChFZHt/vZQVIfsK\nTg6TTCgLAgMBAAECggEAJZQ5rRadWw2L9RZLZpKrVjbHszG4RALApAohqrovzjQS\ndiMk00/Osc88WMBJrMXK7O+8N7v76wfo2kC8ZFpF+73lYBn7bc7vEXjZNW+wHwcV\nMsOgKhOPXn8G2GW4MtzoghOhtt9g1guwUnxdBebIrPOXJyyMHCogy8QTsHUuAVZl\n6Qn8ZCY2hIUcN9tGdKLQLgP4moCyxQG6Ak4mMwUMzBl6Np0k4ppqwbJ1yeIrBoYu\n3i0ErI7nPrsNW7BvXOsgHdfXffXXBiLe4wZN0pIlo+IGj7QJXgSHK286Ofvxf8gL\nOCKtG5FXlzDRGfaeZLoyknLy6RW6NmiEXUig3FZkQQKBgQC8xLolLGPb8SYOz/+a\nXpC2XsmrHrL5bK/uHZZ57dhovwTcO23Ek1AunPxEZOO7ZVPkw4jbPZGYZVB9ijmx\n/Mq+SGnD5U6ffsEkiFoXg8ZlGZ35pqTq1+tIAr/mOfPTX9ccCB/iyquIQFUUzCbJ\nPIxYoefvIROpiDSeurEd7Y5/QQKBgQC6YICy0X5s+097i3VMOLlPTlXcB4DbWrZU\n1ZI+YCKqQ9YF6d9eEFCFSAan0mTYeAAemLUf0UsjMStmY0Zrc0WEX0ajk7EIWZY5\nNPnKjl2FcWtb6txPVqW7R7Ol2kt8oJaIqpnnaF9aSCWrjlRlQSbYjPGpTeBjlom1\noCkw8jTgSwKBgBKjfhnDi0yhP/fAc+WDJtjK/TCnRFa6c5iYVU5OvMqC6fDw8UKy\npP0lRQdfytirBaCV6gJzztd0UYFDz7SSWFZ3gfH0x1GaS2nXCMY/naxH3kBTQv9p\nOHMQym/qEvgxA2ViK/vRtSQr7fqyubWvA9CQk/0Zj/5yU3fEyU5CMYrBAoGADa/6\no6dI/p3yN2OFiMR9r3pUSeVvLCzcSRyqMdSDlzeGJ0cGtXTDRRaGDpsf9tnRQG38\nl0qalwoMYueVi1ya1V7d5DN7g954btkaIysTaTJsLQLxjQ4S+88HPX3rn0p346+L\nv/flUM8NnSzFu+3t+WdYdBOZEBdFI8aQX9zZz0MCgYARHeGwDE2Aq+H1Kb5xGkLQ\nRIGmWENVuvkqhZaj8j9wnnHC9aMSMkhwfHAmhOsQpNdEuR+bzaVFMxbuEWtT3onG\ndE00+Nr1ahti1Cz/NBFPNtLdEZQveNGjtjheCfrbv7S49mdS6wHHJ8HGk+wIsTmb\nTSxiS8u6ZG5GSksgjD6dZw==\n-----END PRIVATE KEY-----\n";
        String privateKeyContent = privateKey.replaceAll("\\n", "").replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "");

        KeyFactory kf = KeyFactory.getInstance("RSA");

        PKCS8EncodedKeySpec keySpecPKCS8 = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyContent));
        return kf.generatePrivate(keySpecPKCS8);
    }

}
