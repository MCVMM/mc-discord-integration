[![Uniga Discord](https://discord.com/api/guilds/713445444146954290/embed.png?style=banner2)](https://discord.gg/BpuFRRB)

# UNIGA Discord integration for Minecraft

## Setup

Create `config` folder within the server root, if it is not present already.

Then create `config/uniga-discord-integration.json` config file with following content:
```json
{
  "token": "Your discord token (can be obtained from https://discord.com/developers/applications)",
  "channel": 0
}
```

To get the channel ID, first enable developer settings, then right click the channel and select **Copy ID**

The bot requires permissions to read, send messages and manage the channel with ID specified in `channel`.
