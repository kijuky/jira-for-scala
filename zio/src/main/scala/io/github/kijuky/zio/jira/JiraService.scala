package io.github.kijuky.zio.jira

import zio.*

object JiraService {
  def layer: TaskLayer[Jira] =
    ZLayer.scoped {
      ZIO.acquireRelease {
        for {
          optServerUri <- System.env("JIRA_SERVER_URI")
          serverUri <- ZIO
            .fromOption(optServerUri)
            .mapError(_ =>
              new IllegalStateException("JIRA_SERVER_URI is not set")
            )
          optAccessToken <- System.env("JIRA_ACCESS_TOKEN")
          accessToken <- ZIO
            .fromOption(optAccessToken)
            .mapError(_ =>
              new IllegalStateException("JIRA_ACCESS_TOKEN is not set")
            )
        } yield Jira(serverUri, accessToken)
      } { jira =>
        ZIO.succeed(jira.close())
      }
    }
}
