package io.github.kijuky.zio.jira

import com.atlassian.jira.rest.client.api.UserRestClient
import com.atlassian.jira.rest.client.api.domain.User
import com.atlassian.jira.rest.client.api.domain.input.UserInput
import io.atlassian.util.concurrent.{Promise, Promises}

import java.net.URI
import java.util.concurrent.CompletableFuture
import scala.jdk.CollectionConverters.*

case class FakeUserRestClient(
  doGetUser: String => User = null,
  doGetUserUri: URI => User = null,
  doCreateUser: UserInput => User = null,
  doUpdateUser: (URI, UserInput) => User = null,
  doRemoveUser: URI => Unit = null,
  doFindUsers: String => Seq[User] = null,
  doFindUsersDetail: (String, Int, Int, Boolean, Boolean) => Seq[User] = null
) extends UserRestClient:
  override def getUser(username: String): Promise[User] =
    Promises.forCompletionStage(
      CompletableFuture.supplyAsync(() => doGetUser(username))
    )

  override def getUser(userUri: URI): Promise[User] =
    Promises.forCompletionStage(
      CompletableFuture.supplyAsync(() => doGetUserUri(userUri))
    )

  override def createUser(userInput: UserInput): Promise[User] =
    Promises.forCompletionStage(
      CompletableFuture.supplyAsync(() => doCreateUser(userInput))
    )

  override def updateUser(userUri: URI, userInput: UserInput): Promise[User] =
    Promises.forCompletionStage(
      CompletableFuture.supplyAsync(() => doUpdateUser(userUri, userInput))
    )

  override def removeUser(userUri: URI): Promise[Void] =
    Promises.forCompletionStage(
      CompletableFuture.runAsync(() => doRemoveUser(userUri))
    )

  override def findUsers(username: String): Promise[java.lang.Iterable[User]] =
    Promises.forCompletionStage(
      CompletableFuture.supplyAsync(() => doFindUsers(username).asJava)
    )

  override def findUsers(
    username: String,
    startAt: Integer,
    maxResults: Integer,
    includeActive: java.lang.Boolean,
    includeInactive: java.lang.Boolean
  ): Promise[java.lang.Iterable[User]] =
    Promises.forCompletionStage(
      CompletableFuture.supplyAsync(() =>
        doFindUsersDetail(
          username,
          startAt.intValue(),
          maxResults.intValue(),
          includeActive,
          includeInactive
        ).asJava
      )
    )
