package net.averagehero.slackesv.beans;


/**
 *
 * Example response JSON from ESV:
 * {"detail":"This application has not been approved yet."}
 */
public class ESVError {

    private final String detail;

    public ESVError(String detail) {
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }

    @Override
    public String toString() {
        return getDetail();
    }
}
