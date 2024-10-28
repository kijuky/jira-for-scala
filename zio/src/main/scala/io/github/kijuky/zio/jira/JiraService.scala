package io.github.kijuky.zio.jira

import com.atlassian.httpclient.api.Request
import com.atlassian.jira.rest.client.api.*
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory

import java.io.Closeable
import java.net.URI

final case class JiraService(serverUri: URI, underlying: JiraRestClient)
    extends Closeable:
  lazy val issueClient: IssueRestClient =
    underlying.getIssueClient
  lazy val sessionClient: SessionRestClient =
    underlying.getSessionClient
  lazy val userClient: UserRestClient =
    underlying.getUserClient
  lazy val groupClient: GroupRestClient =
    underlying.getGroupClient
  lazy val projectClient: ProjectRestClient =
    underlying.getProjectClient
  lazy val componentClient: ComponentRestClient =
    underlying.getComponentClient
  lazy val metadataClient: MetadataRestClient =
    underlying.getMetadataClient
  lazy val searchClient: SearchRestClient =
    underlying.getSearchClient
  lazy val versionClient: VersionRestClient =
    underlying.getVersionRestClient
  lazy val projectRolesClient: ProjectRolesRestClient =
    underlying.getProjectRolesRestClient
  lazy val auditClient: AuditRestClient =
    underlying.getAuditRestClient
  lazy val myPermissionsClient: MyPermissionsRestClient =
    underlying.getMyPermissionsRestClient
  def close(): Unit =
    underlying.close()

object JiraService:
  def apply(serverUri: String, accessToken: String): JiraService =
    val server = URI(serverUri)
    apply(
      server,
      AsynchronousJiraRestClientFactory().create(
        server,
        { (builder: Request.Builder) =>
          builder.setHeader("Authorization", s"Bearer $accessToken")
        }: AuthenticationHandler
      )
    )
