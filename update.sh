#!/bin/sh

git pull
mvn clean
mvn package
screen -S gb -X quit
./start.sh
