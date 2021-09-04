#!/bin/sh

java -Dhttp.port=9000 -Dakka.remote.artery.canonical.port=25520 -Dakka.management.http.port=8558 -Dpidfile.path="/dev/null" -jar service-a-impl-all.jar
