package io.github.kijuky.jira

import com.atlassian.jira.rest.client.api._

import java.net.URI

class JiraClient(val serverUri: URI, underlying: JiraRestClient)
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
