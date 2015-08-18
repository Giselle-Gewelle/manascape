cd "$MANASCAPE_HOME"

mvn clean install

cd target
mv *.war "$MANASCAPE_HOME"

cd "$MANASCAPE_HOME"

cp *.war "$TOMCAT_HOME/webapps"