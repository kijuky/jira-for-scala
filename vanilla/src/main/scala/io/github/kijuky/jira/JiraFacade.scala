package io.github.kijuky.jira

import com.atlassian.jira.rest.client.api.domain.{BasicIssue, Issue, Version}
import io.github.kijuky.jira.Implicits._

object JiraFacade {
  def apply(serverUri: String, accessToken: String): JiraFacade =
    apply(Implicits.createJiraClient(serverUri, accessToken))
}

final case class JiraFacade(client: JiraClient) { jira =>
  object Implicits {
    implicit class RichJiraFacade(jira: JiraFacade)
        extends RichJiraClient(jira.client)

    implicit class RichVersion2(version: Version)
        extends RichVersion(version)(jira.client)

    implicit class RichIssue2(issue: Issue)
        extends RichIssue(issue)(jira.client)

    implicit class RichBasicIssue2[I <: BasicIssue](basicIssue: I)
        extends RichBasicIssue(basicIssue)(jira.client)
  }
}
