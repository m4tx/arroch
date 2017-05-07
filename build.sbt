name := "arroch"
version := "1.0"

lazy val `arroch` = (project in file(".")).enablePlugins(PlayJava)
scalaVersion := "2.11.7"

libraryDependencies ++= Seq(javaJdbc, cache, javaWs)
libraryDependencies ++= Seq(
  javaJpa,
  "org.postgresql" % "postgresql" % "42.0.0",
  "dom4j" % "dom4j" % "1.6.1",
  "org.hibernate" % "hibernate-core" % "5.2.10.Final"
) //Hibernate ORM

PlayKeys.externalizeResources := false

unmanagedResourceDirectories in Test += baseDirectory ( _ /"target/web/public/test" ).value

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
