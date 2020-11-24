#!/bin/sh

JAVA_HOME=/usr/java/j2sdk1.4.1_02
ISAVIZ_HOME=/usr/local/IsaViz

$JAVA_HOME/bin/java -classpath "\
$ISAVIZ_HOME/lib/isaviz.jar:\
$ISAVIZ_HOME/lib/zvtm.jar:\
$ISAVIZ_HOME/lib/jena.jar:\
$ISAVIZ_HOME/lib/xercesImpl.jar:\
$ISAVIZ_HOME/lib/xmlParserAPIs.jar:\
$ISAVIZ_HOME/lib/icu4j.jar:\
$ISAVIZ_HOME/lib/junit.jar:\
$ISAVIZ_HOME/lib/log4j-1.2.7.jar:\
$ISAVIZ_HOME/lib/jakarta-oro-2.0.5.jar:\
$ISAVIZ_HOME/lib/antlr.debug.jar" org.w3c.IsaViz.GSSEditor