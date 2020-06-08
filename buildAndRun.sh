#!/usr/bin/env bash
git pull origin master;
mvn clean package;
fuser -k 9095/tcp;
nohup java -jar target/mindITCovid19.jar 1>/dev/null 2>/dev/null &