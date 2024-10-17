package io.github.kijuky.zio.jira

import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.api.domain.input.IssueInput
import org.joda.time.DateTime
import zio.*

import java.net.{URI, URL}
import java.time.{Instant, OffsetDateTime, ZoneOffset}

final case class JiraIssue(underlying: Issue, input: IssueInput) {
  def assignee: Option[JiraUser] =
    Option(underlying.getAssignee).map(JiraUser.apply)
  def assigneeName: Option[String] = assignee.map(_.name)
  def description: Option[String] = Option(underlying.getDescription)
  def dueDate: OffsetDateTime = toOffsetDateTime(underlying.getDueDate)
  def key: String = underlying.getKey
  def reporter: Option[JiraUser] =
    Option(underlying.getReporter).map(JiraUser.apply)
  def reporterName: Option[String] = reporter.map(_.name)
  def self: URI = underlying.getSelf
  def summary: String = underlying.getSummary

  def url: URL = self.toURL
  def baseUrl: URL = URI.create(url.toString.split("/rest/")(0)).toURL
  def browseUrl = s"${baseUrl.toString}/browse/$key"

  private def toOffsetDateTime(dateTime: DateTime): OffsetDateTime =
    Instant.ofEpochMilli(dateTime.getMillis).atOffset(ZoneOffset.UTC)
  private def toDateTime(offsetDateTime: OffsetDateTime): DateTime =
    new DateTime(offsetDateTime.toInstant.toEpochMilli)
}

object JiraIssue {
  def applyZIO(issue: Issue): UIO[JiraIssue] =
    ZIO.succeed(apply(issue, IssueInput.createWithFields()))
}
