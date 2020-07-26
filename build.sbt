lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """API""",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.12.9",
    libraryDependencies ++= Seq(
      guice,
      jdbc,
      "org.scalatestplus.play"%%"scalatestplus-play"%"5.0.0"%Test,
      "com.google.firebase"%"firebase-admin"%"6.14.0",
      "com.typesafe.play"%%"play-json"%"2.8.0",
      "org.postgresql" % "postgresql" % "9.4-1200-jdbc41",
      // "cn.playscala" % "play-mongo_2.12" % "0.3.0"
      // "org.reactivemongo" %% "play2-reactivemongo" % "0.12.4"
      "org.reactivemongo" %% "play2-reactivemongo" % "0.20.11-play28",
      "org.reactivemongo" %% "reactivemongo-play-json-compat" % "0.20.9-play28"
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"  
    )
    // addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
  )