package io.github.kijuky.zio.jira

import com.atlassian.jira.rest.client.api.domain.Issue
import com.atlassian.jira.rest.client.api.domain.input.IssueInput

object FakeJiraIssue:
  def apply(underlying: Issue = null, input: IssueInput = null): JiraIssue =
    JiraIssue(underlying, input)
