package io.github.kijuky.zio.jira

import com.atlassian.jira.rest.client.api.domain.{BasicUser, Filter}

import java.net.URI

object FakeFilter:
  def apply(
    self: URI = null,
    id: Long = 0,
    name: String = null,
    description: String = null,
    jql: String = null,
    viewUrl: URI = null,
    searchUrl: URI = null,
    owner: BasicUser = null,
    favourite: Boolean = false
  ): Filter =
    Filter(
      self,
      id,
      name,
      description,
      jql,
      viewUrl,
      searchUrl,
      owner,
      favourite
    )
