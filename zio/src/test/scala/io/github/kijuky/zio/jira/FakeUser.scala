package io.github.kijuky.zio.jira

import com.atlassian.jira.rest.client.api.ExpandableProperty
import com.atlassian.jira.rest.client.api.domain.User

import java.net.URI
import scala.jdk.CollectionConverters.*

object FakeUser:
  def apply(
    self: URI = null,
    name: String = null,
    displayName: String = null,
    accountId: String = null,
    emailAddress: String = null,
    active: Boolean = false,
    groups: Seq[String] = null,
    avatarUris: Map[String, URI] = Map(
      User.S48_48 -> URI("https://example.com/avatar.png")
    ),
    timezone: String = null
  ): User =
    User(
      self,
      name,
      displayName,
      accountId,
      emailAddress,
      active,
      Option(groups).map(g => ExpandableProperty(g.asJava)).orNull,
      avatarUris.asJava,
      timezone
    )
