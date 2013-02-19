Introduction
------------

[![Build Status](https://secure.travis-ci.org/Athou/GeekBot.png?branch=master)](http://travis-ci.org/Athou/GeekBot)

GeekBot is an IRC Bot and a framework built around the excellent PircBot Java IRC Bot library. It is made for deployment on OpenShift but should work on a standalone JBossAS 7.x server and probably on any JavaEE6 container.

The bot is bundled with plenty of commands already, and allows you to create new commands easily with annotations.

One of the features of the bot is to store sentences said on the channel and give those back at some point in the future randomly in a conversation or when directly addressed.

There are also basic commands like Quotes handling, Google Web and Images search, Wikipedia search, Youtube title fetching when linking a video, Horoscope, RSS Fetching, Imgur images mirroring, and much more.

All commands are available in the be.hehehe.geekbot.commands package and you can add your own.


Installation
------------

Copy `/src/main/resources/config.properties.example` to `$OPENSHIFT_DATA_DIR/config.properties`

Copy `/src/main/resources/own3d.properties.example` to `$OPENSHIFT_DATA_DIR/own3d.properties`

Edit those new files to reflect your local configuration (bot name, irc server and channel, database connection ...)


Deployment
----------

just `git push` to your OpenShift git repository
