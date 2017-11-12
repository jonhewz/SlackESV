# SLACKESV
This application is a conduit between Slack and Crossway's English Standard Version REST API. It allows you to use
a slash command to send requests to their API and receive responses with scripture data.

When a Slack user types `/esv Romans 1`, Slack will post a request to your web application. This application will
then make a GET request to api.esv.org to get the desired text, and return it back to Slack to be posted in a channel.

The biggest assumption is that api.esv.org will consistently return within the 3-second timeout that Slack
imposes. In my testing, there have been intermittent long responses over 3 seconds. This problem seems to be
resolved, but if it continues the right solution is to follow the directions outlined in
[Slack's documentation](https://api.slack.com/slash-commands#delayed_responses_and_multiple_responses)
Specifically, the controller needs to spawn a thread to handle the response from api.esv.org and post it back 
to Slack.

## Heroku Deployment Steps
SlackESV can certainly be deployed as a self-executing jarfile to any server with which you are familiar. But
it is tested primarily with Heroku, and is quite easy to get running on Heroku by following these steps.
1) Obtain an application key from [api.esv.org](https://api.esv.org/account/create-application/), which
will require manual approval from their staff. This key is what will be used to identify *your* requests.
You should also be aware of their [conditions of usage](https://api.esv.org/#conditions).
![alt text](https://github.com/jonhewz/SlackESV/blob/master/markdown/images/create-esv-application.png "create-esv-application")
<img src="https://github.com/jonhewz/SlackESV/blob/master/markdown/images/create-esv-application.png" width="200">
2) Set up your slash command for your Slack organization. Go to 
https://YOUR-ORG.slack.com/apps/manage/custom-integrations, and click on `Slash Commands`.
![alt text](https://github.com/jonhewz/SlackESV/blob/master/markdown/images/slack-add-configuration.png "slack-add-configuration")
<img src="https://github.com/jonhewz/SlackESV/blob/master/markdown/images/slack-add-configuration" width="200">
Be aware that the URL field will need to be changed once you get a url from Heroku. Also make note of the
generated Token field, which will be used in the next step.
![alt text](https://github.com/jonhewz/SlackESV/blob/master/markdown/images/slack-integration-settings.png "slack-integration-settings")
Set an icon if you wish, as well as the autocomplete.
![alt text](https://github.com/jonhewz/SlackESV/blob/master/markdown/images/slack-autocomplete.png "slack-complete")
3) Use this button to launch a heroku instance

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)
![alt text](https://github.com/jonhewz/SlackESV/blob/master/markdown/images/heroku-launch.png "heroku-launch")
Use your ESV application key that you obtained in step #1, and the slack token you obtained in #2, 
to launch the instance.
4) Find the domain of your newly launched application on the Settings page for your app.
![alt text](https://github.com/jonhewz/SlackESV/blob/master/markdown/images/heroku-domain.png "heroku-domain")
Go back to your Slack slash command configuration and set the URL field with your heroku app's domain.
5) Heroku free dynos fall asleep after an hour of idle time. The wake-up time is a big deal, and will 
result in Slack timeouts the first request after an idle period of time. You can use any sort of 
service to keep your dyno alive. I had success with [UptimeRobot](https://uptimerobot.com). You can create 
a monitor with your Heroku URL, followed by /health?echo=whateverYouWant. It is a good idea to also set 
up an email notification, so that if there are problems with your service you'll be notified.
![alt text](https://github.com/jonhewz/SlackESV/blob/master/markdown/images/uptime-robot.png "uptime-robot")

That's it! You should be good to go!