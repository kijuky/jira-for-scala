package io.github.kijuky.zio.jira

import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.api.domain.input.IssueInput
import zio.*

final case class JiraIssue(underlying: Issue, input: IssueInput) {
  def summary: String = underlying.getSummary
  def description: String = underlying.getDescription
  def key: String = underlying.getKey
  def reporter: JiraUser = JiraUser(underlying.getReporter)
  def reporterName: String = reporter.name
}

object JiraIssue {
  def applyZIO(issue: Issue): UIO[JiraIssue] =
    ZIO.succeed(apply(issue, IssueInput.createWithFields()))
}
