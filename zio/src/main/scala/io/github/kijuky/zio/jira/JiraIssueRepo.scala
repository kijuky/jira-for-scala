package io.github.kijuky.zio.jira

import zio.*

trait JiraIssueRepo {
  def list(filterId: Long): Task[Seq[JiraIssue]]
  def list(jql: String): Task[Seq[JiraIssue]]
  def save(issue: JiraIssue): Task[Unit]
  def transition(issue: JiraIssue, id: Int): Task[Unit]
}

object JiraIssueRepo {
  def list(filterId: Long): RIO[JiraIssueRepo, Seq[JiraIssue]] =
    ZIO.serviceWithZIO(_.list(filterId))
  def list(jql: String): RIO[JiraIssueRepo, Seq[JiraIssue]] =
    ZIO.serviceWithZIO(_.list(jql))
  def save(issue: JiraIssue): RIO[JiraIssueRepo, Unit] =
    ZIO.serviceWithZIO(_.save(issue))
  def transition(issue: JiraIssue, id: Int): RIO[JiraIssueRepo, Unit] =
    ZIO.serviceWithZIO(_.transition(issue, id))
}
