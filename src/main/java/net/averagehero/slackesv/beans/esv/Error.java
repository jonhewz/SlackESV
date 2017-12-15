package net.averagehero.slackesv.beans.esv;

/**
 * Example response JSON from ESV:
 * {"detail":"This application has not been approved yet."}
 */
public class Error {

    private final String detail;

    public Error(String detail) {
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
