#!/bin/sh
BASEPATH="`dirname $0`"
VERSION=4.1.0
java -cp "$BASEPATH"/lib -jar "$BASEPATH"/lib/daylightchart-$VERSION.jar
