import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.net.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Request {
    private String input;

    private String method;
    private String version;
    private String uri;
    private String path;
    private final List<NameValuePair> queryParams;

    public Request(String input) throws URISyntaxException {
        this.input = input;
        final var parts = input.split(" ");

        if (parts.length == 3) {
            this.method = parts[0];
            this.version = parts[2];
            this.uri = parts[1];
        } else {
            this.method = null;
            this.uri = null;
            this.version = null;
        }
        URIBuilder builder = new URIBuilder(parts[1]);
//        URI getUri = builder.build();
//        this.queryParams = URLEncodedUtils.parse(getUri, Charset.defaultCharset());
        queryParams = builder.getQueryParams();
        this.uri = builder.getPath();
    }

    public void parse() {
        uri = parseUri(input);
    }

    private String parseUri(String requestString) {
        final var parts = requestString.split(" ");

        if (parts.length == 3) {
            return parts[1];
        }

        return null;
    }

    public String getUri() {
        return uri;
    }

    public String getMethod() {
        return method;
    }

    public String getVersion() {
        return version;
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }

    public List<NameValuePair> getParamByName(String name) {
        return queryParams
                .stream()
                .filter(x -> Objects.equals(x.getName(), name))
                .collect(Collectors.toList());
    }
}