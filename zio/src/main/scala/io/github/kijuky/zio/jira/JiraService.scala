package io.github.kijuky.zio.jira

import zio.*

object JiraService {
  def layer(serverUri: String = "", accessToken: String = ""): TaskLayer[Jira] =
    ZLayer.scoped {
      ZIO.acquireRelease {
        for {
          optServerUri <- System.env("JIRA_SERVER_URI")
          serverUri <- ZIO.succeed(optServerUri.getOrElse(serverUri))
          optAccessToken <- System.env("JIRA_ACCESS_TOKEN")
          accessToken <- ZIO.succeed(optAccessToken.getOrElse(accessToken))
        } yield Jira(serverUri, accessToken)
      } { jira =>
        ZIO.succeed(jira.close())
      }
    }
}
