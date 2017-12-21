# SLACKESV
This application proxies Slack slash-command requests to Crossway's English Standard Version REST API.

When a Slack user types `/esv Romans 1`, Slack will post a request to your web application. This application will
then make a GET request to api.esv.org to get the desired text, and return it back to Slack to be posted in a channel.

## Heroku Deployment Steps
SlackESV can certainly be deployed as a self-executing jarfile to any server with which you are familiar. But
it is tested primarily with Heroku, and is quite easy to get running on Heroku by following these steps.
1) Obtain an application key from [api.esv.org](https://api.esv.org/account/create-application/), which
will require manual approval from their staff. This key is what will be used to identify *your* requests.
You should also be aware of their [conditions of usage](https://api.esv.org/#conditions).
2) Set up your slash command for your Slack organization. Go to 
https://api.slack.com/apps, and create a new app. Be aware that the URL field will need to be changed once you get a url 
from Heroku. Also make note of the generated Token field, which will be used in the next step.
Set an icon if you wish, as well as the autocomplete.
3) Launch a heroku instance [![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)
Use your ESV application key that you obtained in step #1, and the slack token you obtained in #2, 
to launch the instance.
4) Find the domain of your newly launched application on the Settings page for your app.
Go back to your Slack slash command configuration and set the URL field with your heroku app's domain.
5) Heroku free dynos fall asleep after an hour of idle time. The wake-up time is a big deal, and will 
result in Slack timeouts the first request after an idle period of time. You can use any sort of 
service to keep your dyno alive. I had success with [UptimeRobot](https://uptimerobot.com). You can create 
a monitor with your Heroku URL, followed by /health?echo=whateverYouWant. It is a good idea to also set 
up an email notification, so that if there are problems with your service you'll be notified.

That's it! You should be good to go!