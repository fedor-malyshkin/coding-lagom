#!/bin/sh

java -Dhttp.port=9002 -Dakka.remote.artery.canonical.port=25522 -Dakka.management.http.port=8560 -Dpidfile.path="/dev/null" -jar service-a-impl-all.jar
