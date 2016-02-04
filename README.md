# Athena Game Manager

AthenaGM is a minigame platform for Spigot Minecraft servers. Its goal is to supply a base framework of APIs for separate gameplay plugins, and to orchestrate the loading and instancing of matches based on YAML filed embedded in the map packages. AthenaGM will handle things like team structure, spawn points and kits, while "handing off" to external plugins to manage the actual game mode-specific events. e.g. A CTF map can ask for a "ctf" game mode, and another plugin can wait and listen for a match to start with such a map, springing into action to handle the flags, merely informing AthenaGM when it should increment team scores.

Why Athena? The ancient Greek deity of inspiration and war strategy seemed appropriate for a PvP minigame framework.

## Development

If you're looking to develop a game type using AthenaGM, have a look at the wiki for information. Additionally, [the JavaDocs can be found here.](http://javadoc.rdwl.xyz/athenagm/apidocs/)

## Mapping

AthenaGM maps are essentially regular Minecraft worlds that have been pruned of extraneous chunks and packaged up with some YAML metadata files. To make a map for an AthenaGM game mode, please refer to the relevant section of the wiki.