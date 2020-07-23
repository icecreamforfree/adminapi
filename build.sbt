lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """API""",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      guice,
      jdbc,
      "org.scalatestplus.play"%%"scalatestplus-play"%"5.0.0"%Test,
      "com.google.firebase"%"firebase-admin"%"6.14.0",
      "com.typesafe.play"%%"play-json"%"2.8.0",
      "org.postgresql" % "postgresql" % "9.4-1200-jdbc41"
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"  
    )
  )
