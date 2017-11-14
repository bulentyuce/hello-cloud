FROM oracle/glassfish:5.0

COPY target/hello-cloud.war $GLASSFISH_HOME/glassfish/domains/domain1/autodeploy/
