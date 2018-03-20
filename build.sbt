lazy val akkaHttpVersion = "10.0.11"
lazy val akkaVersion    = "2.5.11"
lazy val slickVersion = "3.2.0-M2"
lazy val scalaTestVersion = "3.0.1"

scalaVersion in ThisBuild:= "2.12.4"
/*
lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "com.narrative",
      scalaVersion    := "2.12.4"
    )),
    name := "NarrativeEndpoint",
    libraryDependencies ++= Seq(

    )
  )*/


lazy val `http-service` = project
  .in(file("."))
  .settings(name := "http-service")
  .settings(moduleName := "http-service")
  .settings(description := "http-service for narrative")
  .settings(commonSettings)
  .dependsOn(`DbService`)



lazy val commonSettings: Seq[Def.Setting[_]] = Seq(
  resolvers += Resolver.bintrayRepo("beyondthelines", "maven"),
  addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M10" cross CrossVersion.full),
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-xml"        % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-stream"          % akkaVersion,
    "mysql" % "mysql-connector-java" % "5.1.40",
    "com.typesafe.akka" %% "akka-http-testkit"    % akkaHttpVersion % Test,
    "com.typesafe.akka" %% "akka-testkit"         % akkaVersion     % Test,
    "com.typesafe.akka" %% "akka-stream-testkit"  % akkaVersion     % Test,
    "org.scalamock" %% "scalamock" % "4.1.0" % Test,
    "org.scalatest"     %% "scalatest"            % "3.0.1"         % Test),
  scalacOptions += "-Xplugin-require:macroparadise",
  scalacOptions in(Compile, console) ~= (_ filterNot (_ contains "paradise")) // macroparadise plugin doesn't work in repl yet.
)

lazy val DbService = project
  .in(file("DbService"))
  .settings(moduleName := "DbService")
  .settings(parallelExecution in Test := false)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.slick" %% "slick" % slickVersion,
      "org.slf4j" % "slf4j-nop" % "1.6.4",
      "com.h2database" % "h2" % "1.3.175",
      "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
      "org.flywaydb" % "flyway-core" % "3.2.1",
      "org.scalatest"     %% "scalatest" % scalaTestVersion % "test"
    )
  )
  .dependsOn(`Model`)


lazy val Model = project
  .in(file("Model"))
  .settings(
    moduleName := "Model"
  )

lazy val GatlingTesting = project
    .in(file("GatlingTesting"))
    .settings(moduleName := "GatlingService")
    .enablePlugins(GatlingPlugin)
    .settings(
      libraryDependencies ++= Seq(
        "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.3.0" % "test",
        "io.gatling"            % "gatling-test-framework"    % "2.3.0" % "test"
      )
    )