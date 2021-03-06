package net.averagehero.slackesv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.averagehero.slackesv.beans.esv.Error;
import net.averagehero.slackesv.beans.esv.Query;
import net.averagehero.slackesv.beans.slack.Attachment;
import net.averagehero.slackesv.beans.slack.AttachmentResponse;
import net.averagehero.slackesv.beans.slack.SlackResponse;
import net.averagehero.slackesv.beans.slack.TextResponse;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Handle requests to this application at defined endpoints.
 */
@RestController
public class Controller {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private AnnotationConfigApplicationContext context =
            new AnnotationConfigApplicationContext(Config.class);

    private final OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    /**
     * Requests are for ESV Bible passages.
     *
     * @param token
     * @param teamId
     * @param teamDomain
     * @param channelId
     * @param channelName
     * @param userId
     * @param userName
     * @param command
     * @param text
     * @return
     */
    @RequestMapping(value = "/esv", method = RequestMethod.POST)
    public ResponseEntity<? extends SlackResponse> esv(
            @RequestParam("token") String token,
            @RequestParam(value = "team_id", required = false) String teamId,
            @RequestParam(value = "team_domain", required = false) String teamDomain,
            @RequestParam(value = "enterprise_id", required = false) String enterpriseId,
            @RequestParam(value = "enterprise_name", required = false) String enterpriseName,
            @RequestParam(value = "channel_id", required = false) String channelId,
            @RequestParam(value = "channel_name", required = false) String channelName,
            @RequestParam(value = "user_id", required = false) String userId,
            @RequestParam(value = "user_name", required = false) String userName,
            @RequestParam(value = "command", required = false) String command,
            @RequestParam("text") String text,
            @RequestParam("response_url") String responseUrl,
            @RequestParam(value = "trigger_id", required = false) String triggerId) {

        logger.debug("/esv request: token=" + token +
                "&team_id=" + teamId +
                "&team_domain=" + teamDomain +
                "&enterprise_id=" + enterpriseId +
                "&enterprise_name=" + enterpriseName +
                "&channel_id=" + channelId +
                "&channel_name=" + channelName +
                "&user_id=" + userId +
                "&user_name=" + userName +
                "&command=" + command +
                "&text=" + text +
                "&response_url=" + responseUrl +
                "&trigger_id=" + triggerId
        );

        // Perform basic authentication
        ResponseEntity<TextResponse> errorResponse = authenticateRequest(token);
        if (errorResponse != null) {
            return errorResponse;
        }

        // Check for valid input
        if (text.trim().isEmpty() ||
                text.startsWith("help") ||
                text.startsWith("-h") ||
                text.startsWith("--help")) {
            return new ResponseEntity<TextResponse>(TextResponse.createPrivate(
                    "ESV Help\n" +
                            "--------\n" +
                            "Perform a passage lookup:\n" +
                            "/esv prov 31:1-5\n" +
                            "/esv JOH 1\n"),
                    HttpStatus.OK);
        }

        // There's no guarantee that api.esv will return within Slack's timeout. Better to let a
        // thread do the work and post back to Slack's response_url, as described here:
        // https://api.slack.com/slash-commands#delayed_responses_and_multiple_responses
        new Thread(() -> {

            ObjectMapper objectMapper = new ObjectMapper();

            // Send the text along to the ESV API
            String body;
            SlackResponse slackResponse;
            try {
                String esvApiLink = context.getBean("esvApiLink", String.class) +
                        URLEncoder.encode(text, "UTF-8");

                logger.debug("Issuing ESV API GET Request to: " + esvApiLink);

                // The ESV developer key goes in the header of every request
                Request esvRequest = new Request.Builder()
                        .url(esvApiLink)
                        .header("Authorization", "Token " + context.getBean("esvKey"))
                        .build();

                long start = System.currentTimeMillis();
                try (okhttp3.Response esvResponse = client.newCall(esvRequest).execute()) {
                    logger.debug("ESV Request: " + (System.currentTimeMillis() - start) + " ms.");

                    //body = "{\"query\":\"Genesis 1:1,John 1:1\",\"canonical\":\"Genesis 1:1; John 1:1\",\"parsed\":[[1001001,1001001],[43001001,43001001]],\"passage_meta\":[{\"canonical\":\"Genesis 1:1\",\"chapter_start\":[1001001,1001031],\"chapter_end\":[1001001,1001031],\"prev_verse\":null,\"next_verse\":1001002,\"prev_chapter\":null,\"next_chapter\":[1002001,1002025]},{\"canonical\":\"John 1:1\",\"chapter_start\":[43001001,43001051],\"chapter_end\":[43001001,43001051],\"prev_verse\":42024053,\"next_verse\":43001002,\"prev_chapter\":[42024001,42024053],\"next_chapter\":[43002001,43002025]}],\"passages\":[\"\\nGenesis 1:1\\n\\n\\nThe Creation of the World\\n\\n  [1] In the beginning, God created the heavens and the earth. (ESV)\",\"\\nJohn 1:1\\n\\n\\nThe Word Became Flesh\\n\\n  [1] In the beginning was the Word, and the Word was with God, and the Word was God. (ESV)\"]}";
                    body = esvResponse.body().string();
                    if (esvResponse.isSuccessful()) {
                        Query esvQuery = objectMapper.readValue(body, Query.class);

                        String passagesText = "";
                        for (String passage : esvQuery.getPassages()) {
                            passagesText += passage;
                        }
                        Attachment attachment = Attachment.create()
                                .setColor("good")
                                .setFallback(esvQuery.getCanonical() + " | " + passagesText)
                                .setText(passagesText)
                                .setFooter(context.getBean("esvWebLink", String.class) +
                                        URLEncoder.encode(esvQuery.getCanonical(), "UTF-8"));

                        slackResponse = AttachmentResponse.createPublic(attachment);

                    } else {
                        Error esvMessage = objectMapper.readValue(body, Error.class);
                        logger.error(esvResponse.toString());

                        // Do not just pass the error along to Slack. We have no control over what it is, and
                        // don't want to blindly send those to end users. Could be things like: reached daily limit,
                        // or developer key not authorized.
                        throw new Exception("Error with api.esv.org exchange. Please check server logs for details.");
                    }
                }
            } catch (NoSuchBeanDefinitionException e) {
                slackResponse = TextResponse.createPrivate("Error with SlackESV configuration");
            } catch (Exception e) {
                slackResponse = TextResponse.createPrivate(e.getMessage());
            }

            // Send the response back to Slack
            // https://api.slack.com/methods/chat.postMessage
            String slackResponseJson;
            try {
                slackResponseJson = objectMapper.writeValueAsString(slackResponse);

                logger.debug("Posting to Slack: " + slackResponseJson);
                RequestBody slackBody = RequestBody.create(JSON, slackResponseJson);
                Request slackRequest = new Request.Builder()
                        .url(responseUrl)
                        .post(slackBody)
                        .build();
                try (okhttp3.Response response = client.newCall(slackRequest).execute()) {
                    logger.debug(response.toString());
                    if (!response.isSuccessful()) {
                        logger.error("Error posting back to slack: " + response.message());
                    }
                } catch (IOException e) {
                    logger.error(e.toString());
                }
            } catch (JsonProcessingException e) {
                logger.error(e.toString());
            }

            logger.debug("spawn thread returning");

        }).start();

        logger.debug("main thread returning");

        return new ResponseEntity<SlackResponse>(SlackResponse.createPublic(), HttpStatus.OK);
    }

    /**
     * Health check. Currently just used as a keepalive for uptimerobot.com, this can be expanded to include
     * an actual assessment of the application's vitals.
     *
     * @param echo Comes right back.
     * @return
     */
    @RequestMapping(value = "/health", method = RequestMethod.GET)
    public ResponseEntity<String> health(
            @RequestParam(value = "echo", required = false) String echo) {

        logger.debug("/esv request: echo=" + echo);

        if (echo != null && echo.length() > 0) {
            return new ResponseEntity(echo, HttpStatus.OK);
        } else {
            return new ResponseEntity(HttpStatus.OK);
        }
    }

    /**
     * Helper method to abstract out authentication details. When you set up a Slack integration, Slack
     * creates a Token. Per their documentation: "This token will be sent in the outgoing payload. You can use it to
     * verify the request came from your Slack team."
     * Token rotation (for now) is a manual process, and would require regeneration of Slack token, followed by
     * restart of SlackESV with the new token specified as an application parameter.
     *
     * @param token
     * @return
     */
    private ResponseEntity<TextResponse> authenticateRequest(String token) {
        ResponseEntity<TextResponse> responseEntity = null;

        // Authenticate based on Slack Token.
        try {
            String authorizedSlackToken = context.getBean("authorizedSlackToken", String.class);

            // Server error - need to define the slack token as an environment variable. (see Config.java)
            if (authorizedSlackToken == null) {
                return new ResponseEntity<TextResponse>(
                        TextResponse.createPrivate("Authorization misconfiguration"),
                        HttpStatus.INTERNAL_SERVER_ERROR);

            // Client error - token mismatch
            } else if (!authorizedSlackToken.equals(token)) {
                return new ResponseEntity<TextResponse>(
                        TextResponse.createPrivate("Authorization denied for provided token"),
                        HttpStatus.UNAUTHORIZED);
            }

            // Else - good to go, carry on.

        } catch (NoSuchBeanDefinitionException e) {
            return new ResponseEntity<TextResponse>(
                    TextResponse.createPrivate("Authorization unconfigured"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }
}
