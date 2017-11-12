package net.averagehero.slackesv;

import com.sun.net.httpserver.Authenticator;
import net.averagehero.slackesv.beans.SlackResponse;
import net.averagehero.slackesv.services.DependentServiceException;
import net.averagehero.slackesv.services.InternalImplementationException;
import net.averagehero.slackesv.services.SlackRelayService;
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

import java.net.URI;


/**
 * SlackCommandController is intended to be called by Slack slash commands, as documented here:
 * https://api.slack.com/slash-commands
 *
 * It is intended to be a fairly thin wrapper around whatever back-end services the Slack user wishes
 * to interface with.
 *
 * Each endpoint should be defined as a POST, since this is Slack's favored method. Each endpoint should
 * also require each of the params, since they are guaranteed to be sent by Slack and therefore can
 * be used to (lightly) authenticate clients.
 *
 * The text param contains variable text provided by the user. It is the only provided data that should
 * be relayed to the back-end service.
 */

@RestController
public class SlackCommandController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private AnnotationConfigApplicationContext context =
            new AnnotationConfigApplicationContext(SlackRelayConfig.class);

    /**
     * This is the generic endpoint for all ESV activity. The ESV api supports many actions: passageQuery,
     * query, readingPlanQuery, queryInfo, readingPlanInfo, verse, dailyVerse.
     *
     * The text param can contain an optional action, and this can be extended to support as many actions
     * as are desired. If no action is provided it will default to a passageLookup
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
    public ResponseEntity<SlackResponse> esv(
                           @RequestParam("token") String token,
                           @RequestParam("team_id") String teamId,
                           @RequestParam("team_domain") String teamDomain,
                           @RequestParam("channel_id") String channelId,
                           @RequestParam("channel_name") String channelName,
                           @RequestParam("user_id") String userId,
                           @RequestParam("user_name") String userName,
                           @RequestParam("command") String command,
                           @RequestParam("text") String text) {

        // Perform basic authentication
        ResponseEntity<SlackResponse> authResponse = authenticateRequest(token);
        if (authResponse != null) {
            return authResponse;
        }

        // Parse out optional subcommand. In the example "/esv passageQuery Matthew 5:14", "esv" is
        // the command, "passageQuery" is the subcommand, and "Matthew 5:14" is what should be passed
        // into the service.
        //
        // However, assuming that passageQuery is the service marked PRIMARY, the user could just
        // specify "/esv Matthew 5:14" and it should return the results of a passageQuery.

        // Try to find a more specific service
        SlackRelayService service;
        text = text.trim().toLowerCase();
        String[] tokens = text.split("\\s+");

        // If no parameters are provided, fall back to HELP
        if (tokens.length == 1 && "".equals(tokens[0])) {
            service = context.getBean("esv.help", SlackRelayService.class);
        } else {
            int firstParamIndex = 1;

            // If the first param is not a valid subcommand assume passageQuery
            if (!context.isBeanNameInUse("esv." + tokens[0])) {
                service = context.getBean("esv.passagequery", SlackRelayService.class);
                firstParamIndex = 0;

            // The first param is a valid command - use that service and pass the rest of the params in
            } else {
                service = context.getBean("esv." + tokens[0], SlackRelayService.class);
            }

            // Reconstitute the tokens back into the text field (minus subcommand), and proceed
            text = "";
            for (int i = firstParamIndex; i < tokens.length; i++) {
                text += tokens[i] + (i < tokens.length - 1 ? " " : "");
            }
        }

        return runService(service, userName, text);
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
     * @param token
     * @return
     */
    private ResponseEntity<SlackResponse> authenticateRequest(String token) {
        ResponseEntity<SlackResponse> responseEntity = null;

        // Authenticate based on Slack Token.
        try {
            String authorizedSlackToken = context.getBean("authorizedSlackToken", String.class);

            // Server error - need to define the slack token as an environment variable. (see SlackRelayConfig.java)
            if (authorizedSlackToken == null) {
                return new ResponseEntity<SlackResponse>(
                        SlackResponse.createPrivate("Authorization misconfiguration"),
                        HttpStatus.INTERNAL_SERVER_ERROR);

                // Client error - token mismatch
            } else if (!authorizedSlackToken.equalsIgnoreCase(token)) {
                return new ResponseEntity<SlackResponse>(
                        SlackResponse.createPrivate("Authorization denied for provided token"),
                        HttpStatus.UNAUTHORIZED);
            }

            // Else - good to go, carry on.

        } catch (NoSuchBeanDefinitionException e) {
            return new ResponseEntity<SlackResponse>(
                    SlackResponse.createPrivate("Authorization unconfigured"),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    /**
     * Helper method to abstract out the logic of Exception handling.
     * @param service
     * @param text
     * @return
     */
    private ResponseEntity<SlackResponse> runService(SlackRelayService service, String userName, String text) {

        // The relayed request can fail because the dependent service is having problems, or because _this_
        // application has bugs. Trying to be helpful in diagnosing the problem.
        String responseText;
        try {
            responseText = service.performAction(userName, text);
        } catch (DependentServiceException e) {
            return new ResponseEntity<SlackResponse> (
                    SlackResponse.createPrivate(service.getName() + " failed."),
                    HttpStatus.BAD_GATEWAY);

        } catch (InternalImplementationException e) {
            return new ResponseEntity<SlackResponse> (
                    SlackResponse.createPublic("Internal error trying to proxy request to " + service.getName() + "."),
                    HttpStatus.INTERNAL_SERVER_ERROR);

        }

        return new ResponseEntity<SlackResponse> (
                SlackResponse.createPublic(responseText),
                HttpStatus.OK);

    }
}
