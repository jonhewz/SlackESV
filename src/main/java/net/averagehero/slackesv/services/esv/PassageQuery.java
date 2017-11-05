package net.averagehero.slackesv.services.esv;

import net.averagehero.slackesv.SlackRelayConfig;
import net.averagehero.slackesv.services.DependentServiceException;
import net.averagehero.slackesv.services.InternalImplementationException;
import net.averagehero.slackesv.services.SlackRelayService;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jhughes
 * Date: 11/12/15
 * Time: 3:27 PM
 */
public class PassageQuery implements SlackRelayService {
    Logger logger = LoggerFactory.getLogger("SlackCommandController");

    private final String name;
    private final String baseUrl;
    private final String path;
    private final Map<String, String> params;

    private final OkHttpClient client = new OkHttpClient();

    public PassageQuery(String name, String baseUrl, String path, Map<String, String> params) {
        this.name = name;
        this.baseUrl = baseUrl;
        this.path = path;
        this.params = params;
    }

    /**
     * Perform a GET request to the ESV API passageQuery resource, where the passage search query is the
     * userText passed in.
     *
     * @param userText
     * @return
     * @throws DependentServiceException
     * @throws InternalImplementationException
     */
    @Override
    public String performAction(String userName, String userText)
            throws DependentServiceException, InternalImplementationException {

        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(SlackRelayConfig.class);

        String body;
        try {
            String url = getBaseUrl() + getPath() + "?" + convertParams(getParams()) + "&q=" + userText;

            logger.debug("Issuing ESV API GET Request to: " + url);

            // The ESV developer key goes in the header of every request
            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Token " + context.getBean("esvKey"))
                    .build();

            Response response = client.newCall(request).execute();
            body = response.body().string();



        } catch (IOException e) {
            throw new DependentServiceException("Error issuing request to ESV API");

        } catch (NoSuchBeanDefinitionException e) {
            throw new InternalImplementationException("Error with ESV service configuration");
        }

        return body;
    }

    public String getName() {
        return name;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParams() {
        return params;
    }

    private String convertParams(Map<String, String> paramMap) {
        String rv = "";
        for (String key : paramMap.keySet()) {
            rv += "&" + key + "=" + paramMap.get(key);
        }
        return rv;
    }
}
