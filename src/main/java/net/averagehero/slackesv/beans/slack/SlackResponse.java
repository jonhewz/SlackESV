package net.averagehero.slackesv.beans.slack;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Example JSON for an empty response.
 *  {"responseType":"in_channel"}
 */
public class SlackResponse {
    protected static String PRIVATE = "ephemeral";
    protected static String PUBLIC = "in_channel";

    private final String responseType;

    private SlackResponse() {
        this.responseType = PRIVATE;
    }

    protected SlackResponse(String responseType) {
        this.responseType = responseType;
    }

    public static SlackResponse createPublic() {
        return new SlackResponse(PUBLIC);
    }

    public static SlackResponse createPrivate() {
        return new SlackResponse(PRIVATE);
    }

    @JsonProperty("response_type")
    public String getResponseType() {
        return responseType;
    }
}
