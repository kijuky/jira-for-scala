package io.github.kijuky.zio.jira

import com.atlassian.httpclient.api.Request
import com.atlassian.jira.rest.client.api.*
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory

import java.io.Closeable
import java.net.URI

final class Jira(val serverUri: URI, restClient: JiraRestClient)
    extends Closeable {
  val issueClient: IssueRestClient =
    restClient.getIssueClient
  val sessionClient: SessionRestClient =
    restClient.getSessionClient
  val userClient: UserRestClient =
    restClient.getUserClient
  val groupClient: GroupRestClient =
    restClient.getGroupClient
  val projectClient: ProjectRestClient =
    restClient.getProjectClient
  val componentClient: ComponentRestClient =
    restClient.getComponentClient
  val metadataClient: MetadataRestClient =
    restClient.getMetadataClient
  val searchClient: SearchRestClient =
    restClient.getSearchClient
  val versionClient: VersionRestClient =
    restClient.getVersionRestClient
  val projectRolesClient: ProjectRolesRestClient =
    restClient.getProjectRolesRestClient
  val auditClient: AuditRestClient =
    restClient.getAuditRestClient
  val myPermissionsClient: MyPermissionsRestClient =
    restClient.getMyPermissionsRestClient
  def close(): Unit =
    restClient.close()
}

object Jira {
  def apply(serverUri: String, accessToken: String): Jira = {
    val server = new URI(serverUri)
    new Jira(
      server,
      new AsynchronousJiraRestClientFactory().create(
        server,
        { (builder: Request.Builder) =>
          builder.setHeader("Authorization", s"Bearer $accessToken")
        }: AuthenticationHandler
      )
    )
  }
}
