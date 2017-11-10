package net.averagehero.slackesv.services.esv;

import com.google.gson.Gson;
import net.averagehero.slackesv.SlackRelayConfig;
import net.averagehero.slackesv.beans.ESVError;
import net.averagehero.slackesv.beans.ESVPassage;
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
        Object esvResponse;
        try {
            String url = getBaseUrl() + getPath() + "?" + convertParams(getParams()) + "&q=" + userText;

            logger.debug("Issuing ESV API GET Request to: " + url);

            // The ESV developer key goes in the header of every request
            Request request = new Request.Builder()
                    .url(url)
                    .header("Authorization", "Token " + context.getBean("esvKey"))
                    .build();

            long start = System.currentTimeMillis();
            Response response = client.newCall(request).execute();
            logger.debug("ESV Request: " + (System.currentTimeMillis() - start) + " ms.");

            body = response.body().string();
            Gson gson = new Gson();
            if (response.isSuccessful()) {
                esvResponse = gson.fromJson(body, ESVPassage.class);
            } else {
                esvResponse = gson.fromJson(body, ESVError.class);
                logger.error(esvResponse.toString());

                // Do not just pass the error along to Slack. We have no control over what it is, and
                // don't want to blindly send those to end users. Could be things like: reached daily limit,
                // or developer key not authorized.
                return("Error with api.esv.org exchange. Please check server logs for details.");
            }

            //body = "{\"query\":\"Genesis 1:1,John 1:1\",\"canonical\":\"Genesis 1:1; John 1:1\",\"parsed\":[[1001001,1001001],[43001001,43001001]],\"passage_meta\":[{\"canonical\":\"Genesis 1:1\",\"chapter_start\":[1001001,1001031],\"chapter_end\":[1001001,1001031],\"prev_verse\":null,\"next_verse\":1001002,\"prev_chapter\":null,\"next_chapter\":[1002001,1002025]},{\"canonical\":\"John 1:1\",\"chapter_start\":[43001001,43001051],\"chapter_end\":[43001001,43001051],\"prev_verse\":42024053,\"next_verse\":43001002,\"prev_chapter\":[42024001,42024053],\"next_chapter\":[43002001,43002025]}],\"passages\":[\"\\nGenesis 1:1\\n\\n\\nThe Creation of the World\\n\\n  [1] In the beginning, God created the heavens and the earth. (ESV)\",\"\\nJohn 1:1\\n\\n\\nThe Word Became Flesh\\n\\n  [1] In the beginning was the Word, and the Word was with God, and the Word was God. (ESV)\"]}";


        } catch (IOException e) {
            throw new DependentServiceException("Error issuing request to ESV API");

        } catch (NoSuchBeanDefinitionException e) {
            throw new InternalImplementationException("Error with ESV service configuration");
        }

        return esvResponse.toString();
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
