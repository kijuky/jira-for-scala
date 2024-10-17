ThisBuild / resolvers += "Atlassian Public" at "https://maven.atlassian.com/content/repositories/atlassian-public/"
val jiraDependencies = Seq(
  "com.atlassian.jira" % "jira-rest-java-client-core" % "5.2.7" exclude
    ("com.atlassian.sal", "sal-api"),
  // atlassian-plugin を解決できないので、jarを直接取得する
  "com.atlassian.sal" % "sal-api" % "4.4.4" artifacts
    Artifact("sal-api", "jar", "jar"),
  // Runtime で良いが面倒なので加えておく。
  "io.atlassian.fugue" % "fugue" % "6.1.0",
  "org.slf4j" % "slf4j-simple" % "1.7.36" % Test // scala-steward:off
)

lazy val root = project
  .in(file("."))
  .aggregate(vanilla, zio)
  .settings(publish / skip := true)

lazy val vanilla = project
  .in(file("vanilla"))
  .settings(
    name := "jira-for-scala",
    scalaVersion := "2.12.20", // scala-steward:off
    crossScalaVersions := Seq(scalaVersion.value, "3.3.4"),
    console / initialCommands := {
      val serverUri = sys.env.getOrElse("JIRA_SERVER_URI", "")
      val accessToken = sys.env.getOrElse("JIRA_ACCESS_TOKEN", "")
      s"""
         |val jira = io.github.kijuky.jira.JiraFacade("$serverUri", "$accessToken")
         |import jira.Implicits._
         |""".stripMargin
    },
    libraryDependencies ++= jiraDependencies
  )

lazy val zio = project
  .in(file("zio"))
  .settings(
    name := "jira-for-zio",
    scalaVersion := "3.3.4",
    libraryDependencies ++= jiraDependencies ++
      Seq("dev.zio" %% "zio" % "2.1.11")
  )

inThisBuild(
  Seq(
    organization := "io.github.kijuky",
    homepage := Some(url("https://github.com/kijuky/jira-for-scala")),
    licenses := Seq(
      "Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")
    ),
    developers := List(
      Developer(
        "kijuky",
        "Kizuki YASUE",
        "ikuzik@gmail.com",
        url("https://github.com/kijuky")
      )
    ),
    versionScheme := Some("early-semver"),
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
  )
)
