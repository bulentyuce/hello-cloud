FROM sdaschner/open-liberty:javaee8-jdk8-b2

COPY target/hello-cloud.war $DEPLOYMENT_DIR
