package io.github.kijuky.zio.jira

import com.atlassian.jira.rest.client.api.domain.User

final case class JiraUser(underlying: User):
  def name: String = underlying.getName
