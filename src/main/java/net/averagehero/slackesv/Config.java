package net.averagehero.slackesv;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class Config {

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

    // No default for tests, because there's not a public test key for Crossway's API like there was
    // for their last version.
    @Value("${esv.key}")
    private String esvKey;

    @Bean(name="esvKey")
    public String getEsvKey() {
        return esvKey;
    }

    // URL that will be requested to get verses
    @Value("https://api.esv.org/v3/passage/text/?" +
        "output-format=plain-text&" +
        "include-passage-references=true&" +
        "include-first-verse-numbers=false&" +
        "include-verse-numbers=false&" +
        "include-footnotes=false&" +
        "include-short-copyright=false&" +
        "include-copyright=false&" +
        "include-passage-horizontal-lines=false&" +
        "include-heading-horizontal-lines=false&" +
        "include-headings=false&" +
        "include-subheadings=false&" +
        "include-selahs=true&" +
        "include-content-type=true&" +
        "line-length=0&" +
        "q=")
    private String esvApiLink;

    @Bean(name="esvApiLink")
    public String getEsvApiLink() {
        return esvApiLink;
    }

    @Value("https://www.esv.org/")
    private String esvWebLink;

    @Bean(name="esvWebLink")
    public String getEsvWebLink() {
        return esvWebLink;
    }
}
