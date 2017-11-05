package net.averagehero.slackesv;

import net.averagehero.slackesv.services.SlackRelayService;
import net.averagehero.slackesv.services.Unimplemented;
import net.averagehero.slackesv.services.esv.PassageQuery;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: jhughes
 * Date: 11/13/15
 * Time: 12:12 PM
 */
@Configuration
public class SlackRelayConfig {

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter crlf = new CommonsRequestLoggingFilter();
        crlf.setIncludeClientInfo(true);
        crlf.setIncludeQueryString(true);
        crlf.setIncludePayload(true);
        crlf.setMaxPayloadLength(500);
        return crlf;
    }

    // There are two configs that come into the application via environment variables:
    // 1) slack.token - this is unique to a slack team, and is used to authenticate requests and
    //                  make sure they are coming from an approved team.
    // 2) esv.key - Crossway provides a developer key for you to use when making requests against their REST api.

    // Default to TEST, used for spock tests
    @Value("${slack.token:TEST}")
    private String authorizedSlackToken;

    @Bean(name="authorizedSlackToken")
    public String getAuthorizedSlackToken() {
        return authorizedSlackToken;
    }

    // Default to TEST
    @Value("${esv.key:TEST")
    private String esvKey;

    @Bean(name="esvKey")
    public String getEsvKey() {
        return esvKey;
    }

    @Value("https://api.esv.org")
    private String esvBaseUrl;

    @Value("/v3/passage/text/")
    private String esvPassageQueryPath;

    @Value("")
    private String esvPassageQueryParams;

    @Bean(name="esvPassageQueryParams")
    public Map<String,String> getEsvPassageQueryParams() {
        Map<String, String> rv = new HashMap<String, String>();
        rv.put("output-format", "plain-text");
        rv.put("include-passage-references", "true");
        rv.put("include-first-verse-numbers", "false");
        rv.put("include-verse-numbers", "false");
        rv.put("include-footnotes", "false");
        rv.put("include-short-copyright", "false");
        rv.put("include-copyright", "false");
        rv.put("include-passage-horizontal-lines", "false");
        rv.put("include-heading-horizontal-lines", "false");
        rv.put("include-headings", "false");
        rv.put("include-subheadings", "false");
        rv.put("include-selahs", "true");
        rv.put("include-content-type", "true");
        rv.put("line-length", "0");
        return (rv);
    }

    @Bean(name="unimplemented")
    public SlackRelayService getUnimplementedService() {
        return new Unimplemented();
    }

    // ESV Services
    @Bean(name="esv.passagequery")
    public SlackRelayService getESVPassageQueryService() {
        return new PassageQuery("ESV Passage Query", esvBaseUrl, esvPassageQueryPath, getEsvPassageQueryParams());
    }

    @Bean(name="esv.help")
    public SlackRelayService getESVHelpService() {
        return new net.averagehero.slackesv.services.esv.Help("ESV Help");
    }



}
