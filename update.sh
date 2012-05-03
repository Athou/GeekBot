#!/bin/sh

git pull
mvn clean package
screen -S gb -X quit
./start.sh
