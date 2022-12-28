## Hair Check Bot

A discord bot that replies to a message of hair product ingredients, indicating whether it contains ingredients that are good or bad for your hair.

### How it works

It searches for the alcohols present, with a default list of good and bad ones configured in [application.yaml](src/main/resources/application.yaml).

### Configuration

You just need to provide the bot's token, via either:
- environment variable: `BOT__TOKEN=token-here`
- `application.yaml` file: `bot.token: token`

### Tip

Check on-the-fly by grabbing the text via Google lens, then sending it to the bot.
