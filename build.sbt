name := "WeixinBot"

version := "1.0"

lazy val `weixinbot` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq( jdbc , cache , ws   , specs2 % Test )

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "sonatype-forge" at "https://repository.sonatype.org/content/groups/forge/"

//background

libraryDependencies ++= {
  val scalaXmlV = "1.0.4"
  val akkaV = "2.4.2"
  val playSlickV = "1.1.1"
  val mysqlConnectorV = "5.1.31"
  val slickV = "3.1.0"
  val httpclientVersion = "4.3.5"
  val httpcoreVersion = "4.3.2"
  val twitterVersion = "6.22.1"
  val postgreVersion = "9.4.1208"
  val nsalatimeVersion = "2.0.0"

  Seq(
    "com.typesafe.play" %% "play-slick" % playSlickV,
    "org.scala-lang.modules" % "scala-xml_2.11" % scalaXmlV,
    "com.typesafe.slick" %% "slick" % slickV withSources(),
    "com.typesafe.slick" %% "slick-codegen" % slickV,
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-remote" % akkaV,
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "mysql" % "mysql-connector-java" % mysqlConnectorV,
    "org.apache.httpcomponents" % "httpclient" % httpclientVersion withSources(),
    "org.apache.httpcomponents" % "httpcore" % httpcoreVersion withSources(),
    "org.apache.httpcomponents" % "httpmime" % httpclientVersion withSources(),
    "com.twitter" % "util-core_2.10" % twitterVersion,
    "org.postgresql" % "postgresql" % postgreVersion,
    "com.github.nscala-time" % "nscala-time_2.11" % nsalatimeVersion

  )

}
// frontend
libraryDependencies ++= Seq(
  "org.webjars" % "webjars-play_2.11" % "2.4.0-1",
  "org.webjars" % "bootstrap" % "3.3.5",
  "org.webjars" % "react" % "0.13.3",
  "org.webjars.bower" % "react-router" % "0.13.3",
  "org.webjars.bower" % "reflux" % "0.2.11",
  "org.webjars" % "toastr" % "2.1.0",
  "org.webjars" % "font-awesome" % "4.4.0",
  "org.webjars.bower" % "smalot-bootstrap-datetimepicker" % "2.3.1",
  //  "org.webjars" % "momentjs" % "2.10.6",
  "org.webjars.bower" % "bootstrap-daterangepicker" % "2.0.11",
  "org.webjars" % "lodash" % "3.10.1",
  "org.webjars" % "less" % "2.5.3",
  "org.webjars" % "hammerjs" % "2.0.4",
  "org.webjars.bower" % "raphael" % "2.1.4"
)

pipelineStages := Seq(digest, gzip)