# UNIGA Discord integration for Minecraft

## Setup

Create `config` folder within the server root, if it is not present already.

Then create `config/uniga-discord-integration.json` config file with following content:
```json
{
  "token": "Your discord token",
  "chatChannelId": Chat channel ID (number),
  "statusChannelId": Voice channel ID (number)
}
```

The bot requires permissions to both read and send messages to `chatChannel` and manage `statusChannel` to start.
