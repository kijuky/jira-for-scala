package io.github.kijuky.zio.jira

import zio.*

import scala.concurrent.Future

private case class JiraUserRepoImpl(jira: Jira) extends JiraUserRepo {
  override def findByName(name: String): Task[Option[JiraUser]] =
    for {
      user <- ZIO.fromFuture(implicit ec =>
        Future(jira.userClient.getUser(name).get())
      )
    } yield Option(user).map(JiraUser.apply)
}

object JiraUserRepoImpl {
  def layer: URLayer[Jira, JiraUserRepo] =
    ZLayer(
      for jira <- ZIO.service[Jira]
      yield JiraUserRepoImpl(jira)
    )
}
