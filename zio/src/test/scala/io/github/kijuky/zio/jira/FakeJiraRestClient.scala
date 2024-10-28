package io.github.kijuky.zio.jira

import com.atlassian.jira.rest.client.api.*

case class FakeJiraRestClient(
  issueClient: IssueRestClient = null,
  sessionClient: SessionRestClient = null,
  userClient: UserRestClient = null,
  groupClient: GroupRestClient = null,
  projectClient: ProjectRestClient = null,
  componentClient: ComponentRestClient = null,
  metadataClient: MetadataRestClient = null,
  searchClient: SearchRestClient = null,
  versionClient: VersionRestClient = null,
  projectRolesClient: ProjectRolesRestClient = null,
  auditClient: AuditRestClient = null,
  myPermissionsClient: MyPermissionsRestClient = null,
  doClose: () => Unit = null
) extends JiraRestClient:
  override def getIssueClient: IssueRestClient =
    issueClient
  override def getSessionClient: SessionRestClient =
    sessionClient
  override def getUserClient: UserRestClient =
    userClient
  override def getGroupClient: GroupRestClient =
    groupClient
  override def getProjectClient: ProjectRestClient =
    projectClient
  override def getComponentClient: ComponentRestClient =
    componentClient
  override def getMetadataClient: MetadataRestClient =
    metadataClient
  override def getSearchClient: SearchRestClient =
    searchClient
  override def getVersionRestClient: VersionRestClient =
    versionClient
  override def getProjectRolesRestClient: ProjectRolesRestClient =
    projectRolesClient
  override def getAuditRestClient: AuditRestClient =
    auditClient
  override def getMyPermissionsRestClient: MyPermissionsRestClient =
    myPermissionsClient
  override def close(): Unit =
    doClose()
