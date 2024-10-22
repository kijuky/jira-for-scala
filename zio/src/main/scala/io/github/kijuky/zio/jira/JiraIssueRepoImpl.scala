package io.github.kijuky.zio.jira

import com.atlassian.jira.rest.client.api.domain.input.TransitionInput
import zio.*

import scala.concurrent.Future
import scala.jdk.CollectionConverters.*

private case class JiraIssueRepoImpl(jira: Jira) extends JiraIssueRepo {
  override def list(filterId: Long): Task[Seq[JiraIssue]] =
    for {
      filter <- ZIO.fromFuture(implicit ec =>
        Future(jira.searchClient.getFilter(filterId).get())
      )
      jiraIssues <- list(filter.getJql)
    } yield jiraIssues

  override def list(jql: String): Task[Seq[JiraIssue]] =
    for {
      searchResult <- ZIO.fromFuture(implicit ec =>
        Future(jira.searchClient.searchJql(jql).get())
      )
      jiraIssues <- ZIO.foreach(searchResult.getIssues.asScala.toSeq)(
        JiraIssue.applyZIO
      )
    } yield jiraIssues

  override def save(issue: JiraIssue): Task[Unit] =
    for {
      _ <- ZIO.fromFuture(implicit ec =>
        Future(jira.issueClient.updateIssue(issue.key, issue.input).get())
      )
    } yield ()

  override def transition(issue: JiraIssue, id: Int): Task[Unit] = {
    for {
      _ <- ZIO.fromFuture(implicit ec =>
        Future(
          jira.issueClient
            .transition(issue.underlying, TransitionInput(id))
            .get()
        )
      )
    } yield ()
  }
}

object JiraIssueRepoImpl {
  def layer: URLayer[Jira, JiraIssueRepo] =
    ZLayer(
      for jira <- ZIO.service[Jira]
      yield JiraIssueRepoImpl(jira)
    )
}
