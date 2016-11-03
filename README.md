# Slack Cats Integration

This is a really simple integration which takes an input from Slack (or an empty input) and returns a cat gif to the slack channel it was originally posted from.


## Slack usage

```
/cat <search-terms>
```

The usage from Slack could be set up as above by going to Slack settings and adding a new custom integration (Slash Command) HTTP POSTing to the root of where the app is hosted. The app will then take the users input, and output either a random cat gif (if there is no search string) or a gif matching the search string if there is. 

WARNING: images are from giphy search and are not guaranteed to actually be cats. I apologise for the inconvenience this may cause :-)


## Deployment

This app can be deployed directly on heroku for hosting. See their website (https://www.heroku.com/) for details of how to set up a Heroku app if you haven't done so before. The instructions are pretty user friendly.
