lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """API""",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      guice,
      "com.h2database"%"h2"%"1.4.199",
      "org.scalatestplus.play"%%"scalatestplus-play"%"5.0.0"%Test,
      "com.google.firebase"%"firebase-admin"%"6.14.0",
      "com.typesafe.play"%%"play-json"%"2.8.0"
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"  
    )
  )
