name := "minimal"

version := "2018.09"

scalaVersion := "2.12.10"
val akkaVersion = "2.5.14"
val playVersion = "2.6.3"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  ws, guice,
  "org.webjars" %% "webjars-play" % playVersion,
  "org.webjars" % "bootstrap" % "3.1.1-2",


  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "org.mockito" % "mockito-all" % "2.0.2-beta" % Test,
  "net.jadler" % "jadler-all" % "1.3.0" % Test
)

coverageExcludedPackages := "<empty>;Reverse.*;router.*;controllers.javascript;play.api.*;views.html.*"

herokuAppName in Compile := "minimal"
