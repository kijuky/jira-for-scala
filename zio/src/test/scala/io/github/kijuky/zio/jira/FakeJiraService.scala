package io.github.kijuky.zio.jira

import com.atlassian.jira.rest.client.api.JiraRestClient

import java.net.URI

object FakeJiraService:
  def apply(
    serverUri: URI = null,
    restClient: JiraRestClient = null
  ): JiraService =
    JiraService(serverUri, restClient)
