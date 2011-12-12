Introduction
------------

GeekBot is an IRC Bot and a framework built around the excellent PircBot Java IRC Bot library.

The bot is bundled with plenty of commands already, and allows you to create new bot commands easily with annotations.


Installation
------------

Copy /src/main/resources/config.properties.example to /src/main/resources/config.properties

Copy /src/main/resources/META-INF/persistence.xml.example to /src/main/resources/META-INF/persistence.xml

Edit those two new files to reflect your local configuration

Build
-----

mvn package

Run
---
java -jar target/geekbot-1.0.0.jar

Or use the 'start.sh' script (will start a new screen called 'gb'): 
chmod +x start.sh
./start.sh
