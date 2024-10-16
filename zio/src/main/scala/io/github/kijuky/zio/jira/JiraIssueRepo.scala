package io.github.kijuky.zio.jira

import com.atlassian.jira.rest.client.api.domain.input.IssueInput
import zio.*

trait JiraIssueRepo {
  def list(filterId: Long): Task[Seq[JiraIssue]]
  def list(jql: String): Task[Seq[JiraIssue]]
  def save(issue: JiraIssue): Task[Unit]
}

object JiraIssueRepo {
  def list(filterId: Long): RIO[JiraIssueRepo, Seq[JiraIssue]] =
    ZIO.serviceWithZIO(_.list(filterId))
  def list(jql: String): RIO[JiraIssueRepo, Seq[JiraIssue]] =
    ZIO.serviceWithZIO(_.list(jql))
  def save(issue: JiraIssue): RIO[JiraIssueRepo, Unit] =
    ZIO.serviceWith(_.save(issue))
}
