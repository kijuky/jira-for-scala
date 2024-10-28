package io.github.kijuky.zio.jira

import com.atlassian.jira.rest.client.api.domain.*
import org.joda.time.DateTime

import java.net.URI
import scala.jdk.CollectionConverters.*

object FakeIssue:
  def apply(
    summary: String = null,
    self: URI = null,
    key: String = null,
    id: Long = 0,
    project: BasicProject = null,
    issueType: IssueType = null,
    status: Status = null,
    description: String = null,
    priority: BasicPriority = null,
    resolution: Resolution = null,
    attachments: Seq[Attachment] = null,
    reporter: User = null,
    assignee: User = null,
    creationDate: DateTime = null,
    updateDate: DateTime = null,
    dueDate: DateTime = null,
    affectedVersions: Seq[Version] = null,
    fixVersions: Seq[Version] = null,
    components: Seq[BasicComponent] = null,
    timeTracking: TimeTracking = null,
    issueFields: Seq[IssueField] = null,
    comments: Seq[Comment] = null,
    transitionsUri: URI = null,
    issueLinks: Seq[IssueLink] = null,
    votes: BasicVotes = null,
    worklogs: Seq[Worklog] = null,
    watchers: BasicWatchers = null,
    expandos: Seq[String] = null,
    subtasks: Seq[Subtask] = null,
    changelog: Seq[ChangelogGroup] = null,
    operations: Operations = null,
    labels: Set[String] = null
  ): Issue =
    Issue(
      summary,
      self,
      key,
      id,
      project,
      issueType,
      status,
      description,
      priority,
      resolution,
      Option(attachments).map(_.asJava).orNull,
      reporter,
      assignee,
      creationDate,
      updateDate,
      dueDate,
      Option(affectedVersions).map(_.asJava).orNull,
      Option(fixVersions).map(_.asJava).orNull,
      Option(components).map(_.asJava).orNull,
      timeTracking,
      Option(issueFields).map(_.asJava).orNull,
      Option(comments).map(_.asJava).orNull,
      transitionsUri,
      Option(issueLinks).map(_.asJava).orNull,
      votes,
      Option(worklogs).map(_.asJava).orNull,
      watchers,
      Option(expandos).map(_.asJava).orNull,
      Option(subtasks).map(_.asJava).orNull,
      Option(changelog).map(_.asJava).orNull,
      operations,
      Option(labels).map(_.asJava).orNull
    )
