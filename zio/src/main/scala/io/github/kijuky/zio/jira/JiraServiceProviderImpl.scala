package io.github.kijuky.zio.jira

import zio.*

private case class JiraServiceProviderImpl(jira: JiraService)
    extends JiraServiceProvider:
  override def get: Task[JiraService] =
    ZIO.succeed(jira)

object JiraServiceProviderImpl:
  def layer(
    serverUri: String = "",
    accessToken: String = ""
  ): TaskLayer[JiraService] =
    ZLayer.scoped:
      ZIO.fromAutoCloseable:
        for
          optServerUri <- System.env("JIRA_SERVER_URI")
          serverUri <- ZIO.succeed(optServerUri.getOrElse(serverUri))
          optAccessToken <- System.env("JIRA_ACCESS_TOKEN")
          accessToken <- ZIO.succeed(optAccessToken.getOrElse(accessToken))
        yield JiraService(serverUri, accessToken)
