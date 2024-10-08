package io.github.kijuky.jira

import com.atlassian.httpclient.api.Request
import com.atlassian.jira.rest.client.api.domain._
import com.atlassian.jira.rest.client.api.domain.input.{
  IssueInputBuilder,
  LinkIssuesInput
}
import com.atlassian.jira.rest.client.api._
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import org.joda.time.DateTime

import java.net.{URI, URL, URLEncoder}
import java.nio.charset.StandardCharsets
import java.time.{Instant, OffsetDateTime, ZoneOffset}
import scala.collection.JavaConverters._

object Implicits {
  def createJiraClient(serverUri: String, accessToken: String): JiraClient =
    new JiraClient(
      serverUri,
      new AsynchronousJiraRestClientFactory().create(
        new URI(serverUri),
        { (builder: Request.Builder) =>
          builder.setHeader("Authorization", s"Bearer $accessToken")
        }: AuthenticationHandler
      )
    )

  class JiraClient(val serverUri: String, underlying: JiraRestClient)
      extends JiraRestClient {
    override def getIssueClient: IssueRestClient =
      underlying.getIssueClient
    override def getSessionClient: SessionRestClient =
      underlying.getSessionClient
    override def getUserClient: UserRestClient =
      underlying.getUserClient
    override def getGroupClient: GroupRestClient =
      underlying.getGroupClient
    override def getProjectClient: ProjectRestClient =
      underlying.getProjectClient
    override def getComponentClient: ComponentRestClient =
      underlying.getComponentClient
    override def getMetadataClient: MetadataRestClient =
      underlying.getMetadataClient
    override def getSearchClient: SearchRestClient =
      underlying.getSearchClient
    override def getVersionRestClient: VersionRestClient =
      underlying.getVersionRestClient
    override def getProjectRolesRestClient: ProjectRolesRestClient =
      underlying.getProjectRolesRestClient
    override def getAuditRestClient: AuditRestClient =
      underlying.getAuditRestClient
    override def getMyPermissionsRestClient: MyPermissionsRestClient =
      underlying.getMyPermissionsRestClient
    override def close(): Unit =
      underlying.close()
  }

  implicit class RichJiraClient(jiraClient: JiraClient) {
    private implicit val implicitJiraClient: JiraClient = jiraClient
    private lazy val serverUri = jiraClient.serverUri
    lazy val projectClient: ProjectRestClient = jiraClient.getProjectClient
    lazy val issueClient: IssueRestClient = jiraClient.getIssueClient
    lazy val searchClient: SearchRestClient = jiraClient.getSearchClient
    lazy val userClient: UserRestClient = jiraClient.getUserClient

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

  implicit class RichIssue(issue: Issue)(implicit jira: JiraClient) {
    def assignee: Option[User] = Option(issue.getAssignee)
    def assigneeName: Option[String] = assignee.map(_.getName)
    def baseUrl: URL = URI.create(url.toString.split("/rest/")(0)).toURL
    def browseUrl = s"$baseUrl/browse/$key"
    def description: String = issue.getDescription
    def dueDate: OffsetDateTime = toOffsetDateTime(issue.getDueDate)
    def dueDate_=(dueDate: OffsetDateTime): Unit = {
      val dueDateTime = toDateTime(dueDate)
      val input = new IssueInputBuilder().setDueDate(dueDateTime).build()
      jira.issueClient.updateIssue(key, input).claim()
    }
    def key: String = issue.getKey
    def reporter: User = issue.getReporter
    def reporter_=(reporter: User): Unit = {
      val input = new IssueInputBuilder().setReporter(reporter).build()
      jira.issueClient.updateIssue(key, input).claim()
    }
    def reporterName: String = reporter.getName
    def reporterName_=(reporterName: String): Unit = {
      reporter = jira.userClient.getUser(reporterName).get()
    }
    def self: URI = issue.getSelf
    def summary: String = issue.getSummary
    def updatedDate: OffsetDateTime = toOffsetDateTime(issue.getUpdateDate)
    def url: URL = self.toURL
    private def toOffsetDateTime(dateTime: DateTime): OffsetDateTime =
      Instant.ofEpochMilli(dateTime.getMillis).atOffset(ZoneOffset.UTC)
    private def toDateTime(offsetDateTime: OffsetDateTime): DateTime =
      new DateTime(offsetDateTime.toInstant.toEpochMilli)
  }

  implicit class RichBasicIssue[I <: BasicIssue](basicIssue: I)(implicit
    jira: JiraClient
  ) {
    private def toIssue: Issue = basicIssue match {
      case issue: Issue => issue
      case _            => jira.issue(key)
    }

    def assignee: Option[User] = toIssue.assignee
    def assigneeName: Option[String] = toIssue.assigneeName
    def key: String = basicIssue.getKey
    def link(toIssue: BasicIssue, linkType: String = "Relates"): I = {
      jira.link(basicIssue, toIssue, linkType)
      basicIssue
    }
  }
}
