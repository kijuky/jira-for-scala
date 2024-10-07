package io.github.kijuky.jira

import com.atlassian.httpclient.api.Request
import com.atlassian.jira.rest.client.api.AuthenticationHandler
import com.atlassian.jira.rest.client.api.domain._
import com.atlassian.jira.rest.client.api.domain.input.{
  IssueInputBuilder,
  LinkIssuesInput
}
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory

import java.net.{URI, URLEncoder}
import java.nio.charset.StandardCharsets
import scala.collection.JavaConverters._

object Implicits {
  def createJiraClient(serverUri: URI, accessToken: String): JiraClient =
    new JiraClient(
      serverUri,
      new AsynchronousJiraRestClientFactory().create(
        serverUri,
        { (builder: Request.Builder) =>
          builder.setHeader("Authorization", s"Bearer $accessToken")
        }: AuthenticationHandler
      )
    )

  implicit class RichJiraClient(jiraClient: JiraClient) {
    private implicit val implicitJiraClient: JiraClient = jiraClient
    private lazy val serverUri = jiraClient.serverUri
    private lazy val projectClient = jiraClient.getProjectClient
    private lazy val issueClient = jiraClient.getIssueClient
    private lazy val searchClient = jiraClient.getSearchClient

    def project(name: String): Project =
      projectClient.getProject(name).get()
    def versions(projectName: String): Seq[Version] =
      project(projectName).getVersions.asScala.toSeq
    def issue(name: String): Issue = issueClient.getIssue(name).get()
    def issues(filterId: Int): Iterable[Issue] =
      issues(searchClient.getFilter(filterId).get().getJql)
    def issues(jql: String): Seq[Issue] =
      searchClient.searchJql(jql).get().getIssues.asScala.toSeq
    def createIssue(
      projectKey: String,
      issueTypeId: Long,
      summary: String = "",
      reporterName: String = "",
      assigneeName: String = "",
      description: String = "",
      componentNames: Seq[String] = Nil
    ): BasicIssue =
      issueClient
        .createIssue(
          new IssueInputBuilder(projectKey, issueTypeId)
            .setSummary(summary)
            .setReporterName(reporterName)
            .setAssigneeName(assigneeName)
            .setDescription(description)
            .setComponentsNames(componentNames.asJava)
            .build()
        )
        .get()
    def link(
      fromIssue: BasicIssue,
      toIssue: BasicIssue,
      linkType: String
    ): Unit =
      issueClient
        .linkIssue(new LinkIssuesInput(fromIssue.key, toIssue.key, linkType))
        .get()

    def createCreateIssueUrl(
      projectId: Int,
      issueTypeId: Int,
      summary: String = "",
      reporterName: String = "",
      assigneeName: String = "",
      description: String = "",
      priority: Int = 3
    ): String = {
      val charset = StandardCharsets.UTF_8.name
      val params = Map[String, String](
        "pid" -> s"$projectId",
        "issuetype" -> s"$issueTypeId",
        "summary" -> URLEncoder.encode(summary.trim, charset),
        "priority" -> s"$priority",
        "reporter" -> reporterName,
        "description" -> URLEncoder.encode(description.trim, charset),
        "assignee" -> assigneeName
      ).map { case (key, value) => s"$key=$value" }.mkString("&")
      s"$serverUri/secure/CreateIssueDetails!init.jspa?$params"
    }
  }

  implicit class RichVersion(version: Version)(implicit jira: JiraClient) {
    private val serverUri = jira.serverUri
    def browseUrl(projectKey: String) =
      s"$serverUri/projects/$projectKey/versions/$id"
    def id: Long = version.getId
    def isUnreleased: Boolean = !version.isReleased && !version.isArchived
    def name: String = version.getName
  }

  implicit class RichBasicIssue[I <: BasicIssue](basicIssue: I)(implicit
    jira: JiraClient
  ) {
    private def toIssue: Issue = basicIssue match {
      case issue: Issue => issue
      case _            => jira.issue(key)
    }

    def assignee: User = toIssue.getAssignee
    def assigneeName: String = assignee.getName
    def key: String = basicIssue.getKey
    def link(toIssue: BasicIssue, linkType: String = "Relates"): I = {
      jira.link(basicIssue, toIssue, linkType)
      basicIssue
    }
  }
}
