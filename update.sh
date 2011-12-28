#!/bin/sh

screen -S gb -X quit
git pull
mvn clean
mvn package
./start.sh
