# NclspCore

This is a Bukkit plugin for Minecraft servers, providing useful commands and features for server administrators and players. 

## Table of Contents
- [NclspCore](#nclspcore)
  - [Table of Contents](#table-of-contents)
  - [Features](#features)
  - [Configuration](#configuration)
  - [Installation](#installation)
  - [Usage](#usage)
  - [Dependencies](#dependencies)
  - [Disclaimer](#disclaimer)

## Features
- `/msg` command for private messaging
- `/clearchat` command to clear the chat
- `/blockchat` command to toggle chat blocking for all players
- `/broadcast` command to send a message to all players
- `/tpa` command for teleport request
- `/rtp` command for random teleport
- Moderation commands: `/ban`, `/kick`, `/mute`, `/unban`, `/unmute`
- Events for chat moderation, join/leave moderation and more

## Configuration
The `config.yml` file controls the activation and parameters of different features in the plugin.

You can access the configuration file in the plugin's folder, once the plugin is installed and the server is running. Make sure to restart the server after any changes to the configuration file.

It's important to note that certain features, such as moderation commands, require a database with a valid configuration. Only MySQL or MariaDB are supported. Also, note that some commands and permissions are related to the plugin's features, so be sure to have a look at the permissions section in the readme before using them.

- `activation` section controls which features will be enabled on the server, such as private messaging, broadcast, clear chat, teleport request, weather, time, moderation and random teleport.
- `parameters` section allows you to adjust settings for different features. For example, `tpa` section controls the time to respond and the time before teleport. `moderation` section lets you configure the database settings for moderation features, such as host, port, username, password, database name, and table prefix. `rtp` section allows you to adjust the center coordinates and maximum range of random teleport, as well as the timer for each permission.
- `messages` section allows you to customize the messages that are displayed to players, such as error messages for no permission, no arguments, and player not online.
## Installation
1. Download the plugin's jar file
2. Place the jar file in your server's `plugins` folder
3. Restart the server
4. Configure the plugin by editing the `config.yml` file in the plugin's folder

## Usage
- Use the `/help` command to see a list of available commands and their usage.
- For more information on a specific command, use `/<command> help`
- Check the plugin's configuration file to enable/disable specific features and adjust settings.

## Dependencies
- Bukkit API
- Java 8 or higher

## Disclaimer
This plugin is provided as is, with no guarantee of proper function or support. Use at your own risk.
