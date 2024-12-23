package io.github.kijuky.zio.jira

import zio.*

import scala.concurrent.Future

private case class JiraUserRepoImpl(jira: JiraService) extends JiraUserRepo:
  override def findByName(name: String): Task[Option[JiraUser]] =
    for user <- ZIO.attemptBlocking(jira.userClient.getUser(name).get())
    yield Option(user).map(JiraUser.apply)

object JiraUserRepoImpl:
  def layer: URLayer[JiraService, JiraUserRepo] =
    ZLayer:
      for service <- ZIO.service[JiraService]
      yield JiraUserRepoImpl(service)
