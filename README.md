# PokiSense 1.8.8 plugin
## What is it?
Up until Minecraft 1.20 Pre-release 5, Minecraft clients had a [glitch](https://bugs.mojang.com/browse/MC-74984) that allowed them to connect to multiple servers at once.
This lets both servers interact with the client (say one enables flight, letting the client fly in the other server).<br><br>
PokiSense is a 1.8.8 plugin that replicates this glitch thanks to Lunar Client Deep Links (**lunarclient://**).
This plugin also has some cool interactions with the client.

## Warning
If not used with caution, this may get you **banned** from servers, if you end up banned, I advise you to stop having such skill issues 💀

## Docs
### Commands must be run in console, otherwise there's a 99% chance that they won't work.
### At the moment this client only works on Windows.<br><br>

- `/connect <server_ip> [delay_ticks]`
  - Connects to the target server with a delay using the [glitch](https://bugs.mojang.com/browse/MC-74984) mentioned.<br>
  _**Must have Lunar Client launched beforehand.**_
- `/staff`
  - Attempts to enable Lunar Staff Mods (X-Ray).
- `/headed`
  - Toggles fake Creative Mode with flight disabled.
- `/fly [speed]`
  - Toggles flight. Speed goes from 0 to 10 counting decimals.
- `/glow <premiumPlayer...>`
  - Attempts to apply a glow effect to each player via Lunar Client API.
- `/cglow`
  - Same as `/glow` but takes the name copied in the clipboard.
- `/panic`
  - Disables Staff Mods, sets the Gamemode to survival, disables flight, and removes all glow effects from all players.