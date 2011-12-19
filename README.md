Introduction
------------

GeekBot is an IRC Bot and a framework built around the excellent PircBot Java IRC Bot library.

The bot is bundled with plenty of commands already, and allows you to create new commands easily with annotations.

One of the features of the bot is to store sentences said on the channel and give those back at some point in the future randomly in a conversation or when directly addressed. The bot tries to find sentences that match the current context of the conversation, resulting in usually funny automatic reactions.

There are also basic commands like Quotes handling, Google Web and Images search, Wikipedia search, Youtube title fetching when linking a video, Horoscope, RSS Fetching, IMGUR images mirroring, and much more.

All commands are available in the be.hehehe.geekbot.commands package and you can add your own.


Installation
------------

Copy `/src/main/resources/config.properties.example` to `/src/main/resources/config.properties`

Copy `/src/main/resources/own3d.properties.example` to `/src/main/resources/own3d.properties`

Copy `/src/main/resources/META-INF/persistence.xml.example` to `/src/main/resources/META-INF/persistence.xml`

Edit those new files to reflect your local configuration (bot name, irc server and channel, database connection ...)

Build
-----

mvn package

Run
---
java -jar target/GeekBot-1.0.0.jar

Or use the 'start.sh' script (will start a new screen called 'gb'): 

chmod +x start.sh

./start.sh
