# SLACKESV
This application is a conduit between Slack and Crossway's English Standard Version REST API. It allows you to use
a slash command to send requests to their API and receive response data.

When a Slack user types `/esv Romans 1`, Slack will post a request to this web application. This application will
then make a GET request to api.esv.org to get the desired text, and return it back to Slack to be posted in a channel.

This can be expanded in the future to support more of ESV's functionality, such as queries, reading plans, and daily
verses. 
