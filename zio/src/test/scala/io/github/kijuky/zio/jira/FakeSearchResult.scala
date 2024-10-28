package io.github.kijuky.zio.jira

import com.atlassian.jira.rest.client.api.domain.{Issue, SearchResult}

import scala.jdk.CollectionConverters.*

object FakeSearchResult:
  def apply(
    startIndex: Int = 0,
    maxResults: Int = 0,
    total: Int = 0,
    issues: Seq[Issue] = null
  ): SearchResult =
    SearchResult(startIndex, maxResults, total, issues.asJava)
