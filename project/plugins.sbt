// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.4.6")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.0.1")

resolvers += "Sonatype Repository" at "https://oss.sonatype.org/content/groups/public"

addSbtPlugin("com.ebiznext.sbt.plugins" % "sbt-sonar" % "0.1.1")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "4.0.0")
