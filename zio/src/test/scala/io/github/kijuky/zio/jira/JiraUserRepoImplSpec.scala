package io.github.kijuky.zio.jira

import zio.*
import zio.test.*

object JiraUserRepoImplSpec extends ZIOSpecDefault:
  def spec: Spec[TestEnvironment, Any] = suiteAll("JiraUserRepoImplSpec"):
    suiteAll("findByName"):
      suite("getUserがユーザーを返した場合"):
        for
          sut <- ZIO.service[JiraUserRepoImpl]
          actual <- sut.findByName("test-user")
          Some(user) = actual: @unchecked
        yield Chunk(test("ユーザーを返すこと"):
          assertTrue:
            user.name == "test-user"
        )
      .provide(ZLayer:
        val userClient =
          FakeUserRestClient(doGetUser = _ => FakeUser(name = "test-user"))
        val restClient = FakeJiraRestClient(userClient = userClient)
        for service <- ZIO.succeed(FakeJiraService(restClient = restClient))
        yield JiraUserRepoImpl(service)
      )

      suite("getUserがエラーになった場合"):
        for
          sut <- ZIO.service[JiraUserRepoImpl]
          isFailure <- sut.findByName("test-user").isFailure
        yield Chunk(test("失敗すること"):
          assertTrue:
            isFailure
        )
      .provide(ZLayer:
        val userClient =
          FakeUserRestClient(doGetUser = _ => throw RuntimeException())
        val restClient = FakeJiraRestClient(userClient = userClient)
        for service <- ZIO.succeed(FakeJiraService(restClient = restClient))
        yield JiraUserRepoImpl(service)
      )
