The UgTool offers a bunch of actions on UserGroups.

Until now it ist not included in the main-pom, so you have to make a maven-build by yourself (pom already existing).


Start with:

(normal)
java -jar ugtool-1.0-SNAPSHOT-jar-with-dependencies.jar

(changing FrameworkURL)
java -DframeworkUrl=myNewFrameworkUrl -jar ugtool-1.0-SNAPSHOT-jar-with-dependencies.jar