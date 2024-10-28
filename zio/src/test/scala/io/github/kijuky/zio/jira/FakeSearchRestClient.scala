package io.github.kijuky.zio.jira

import com.atlassian.jira.rest.client.api.SearchRestClient
import com.atlassian.jira.rest.client.api.domain.{Filter, SearchResult}
import io.atlassian.util.concurrent.{Promise, Promises}

import java.net.URI
import java.util.concurrent.CompletableFuture
import scala.jdk.CollectionConverters.*

case class FakeSearchRestClient(
  doSearchJql: String => SearchResult = null,
  doSearchJqlDetail: (
    String,
    Integer,
    Integer,
    java.util.Set[String]
  ) => SearchResult = null,
  doGetFavouriteFilters: () => Seq[Filter] = null,
  doGetFilterUri: URI => Filter = null,
  doGetFilterId: Long => Filter = null
) extends SearchRestClient:
  override def searchJql(jql: String): Promise[SearchResult] =
    Promises.forCompletionStage(
      CompletableFuture.supplyAsync(() => doSearchJql(jql))
    )

  override def searchJql(
    jql: String,
    maxResults: Integer,
    startAt: Integer,
    fields: java.util.Set[String]
  ): Promise[SearchResult] =
    Promises.forCompletionStage(
      CompletableFuture.supplyAsync(() =>
        doSearchJqlDetail(jql, maxResults, startAt, fields)
      )
    )

  override def getFavouriteFilters: Promise[java.lang.Iterable[Filter]] =
    Promises.forCompletionStage(
      CompletableFuture.supplyAsync(() => doGetFavouriteFilters().asJava)
    )

  override def getFilter(filterUri: URI): Promise[Filter] =
    Promises.forCompletionStage(
      CompletableFuture.supplyAsync(() => doGetFilterUri(filterUri))
    )

  override def getFilter(id: Long): Promise[Filter] =
    Promises.forCompletionStage(
      CompletableFuture.supplyAsync(() => doGetFilterId(id))
    )
