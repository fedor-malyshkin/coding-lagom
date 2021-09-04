#!/bin/sh

java -Dhttp.port=9001 -Dakka.remote.artery.canonical.port=25521 -Dakka.management.http.port=8559 -Dpidfile.path="/dev/null" -jar service-a-impl-all.jar
