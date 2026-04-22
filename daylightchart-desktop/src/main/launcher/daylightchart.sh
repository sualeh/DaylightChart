#!/bin/sh
java -cp $(echo lib/*.jar | tr ' ' ':') daylightchart.Main $*
