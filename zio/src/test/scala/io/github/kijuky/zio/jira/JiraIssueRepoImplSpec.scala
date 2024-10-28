package io.github.kijuky.zio.jira

import zio.*
import zio.test.*

object JiraIssueRepoImplSpec extends ZIOSpecDefault:
  def spec: Spec[TestEnvironment, Any] = suiteAll("JiraIssueRepoImplSpec"):
    suiteAll("list(filterId)"):
      suite("searchJqlが課題を返した場合"):
        for
          sut <- ZIO.service[JiraIssueRepoImpl]
          actual <- sut.list(123456)
          Seq(issue) = actual
        yield Chunk(test("課題を返すこと"):
          assertTrue:
            issue.summary == "Fake Issue"
        )
      .provide(ZLayer:
        val issues = Seq(FakeIssue(summary = "Fake Issue"))
        val searchClient = FakeSearchRestClient(
          doGetFilterId = _ => FakeFilter(),
          doSearchJql = _ => FakeSearchResult(issues = issues)
        )
        val restClient = FakeJiraRestClient(searchClient = searchClient)
        for service <- ZIO.succeed(FakeJiraService(restClient = restClient))
        yield JiraIssueRepoImpl(service)
      )

      suite("searchJqlが課題を返さなかった場合"):
        for
          sut <- ZIO.service[JiraIssueRepoImpl]
          actual <- sut.list(123456)
        yield Chunk(test("課題を返さないこと"):
          assertTrue:
            actual.isEmpty
        )
      .provide(ZLayer:
        val searchClient = FakeSearchRestClient(
          doGetFilterId = _ => FakeFilter(),
          doSearchJql = _ => FakeSearchResult(issues = Nil)
        )
        val restClient = FakeJiraRestClient(searchClient = searchClient)
        for service <- ZIO.succeed(FakeJiraService(restClient = restClient))
        yield JiraIssueRepoImpl(service)
      )

      suite("searchJqlでエラーが発生した場合"):
        for
          sut <- ZIO.service[JiraIssueRepoImpl]
          isFailure <- sut.list(123456).isFailure
        yield Chunk(test("失敗すること"):
          assertTrue:
            isFailure
        )
      .provide(ZLayer:
        val searchClient = FakeSearchRestClient(
          doGetFilterId = _ => FakeFilter(),
          doSearchJql = _ => throw RuntimeException()
        )
        val restClient = FakeJiraRestClient(searchClient = searchClient)
        for service <- ZIO.succeed(FakeJiraService(restClient = restClient))
        yield JiraIssueRepoImpl(service)
      )

      suite("getFilterでエラーが発生した場合"):
        for
          sut <- ZIO.service[JiraIssueRepoImpl]
          isFailure <- sut.list(123456).isFailure
        yield Chunk(test("失敗すること"):
          assertTrue:
            isFailure
        )
      .provide(ZLayer:
        val searchClient =
          FakeSearchRestClient(doGetFilterId = _ => throw RuntimeException())
        val restClient = FakeJiraRestClient(searchClient = searchClient)
        for service <- ZIO.succeed(FakeJiraService(restClient = restClient))
        yield JiraIssueRepoImpl(service)
      )

    suiteAll("list(jql)"):
      suite("searchJqlが課題を返した場合"):
        for
          sut <- ZIO.service[JiraIssueRepoImpl]
          actual <- sut.list("filterId = 123456")
          Seq(issue) = actual
        yield Chunk(test("課題を返すこと"):
          assertTrue:
            issue.summary == "Fake Issue"
        )
      .provide(ZLayer:
        val issues = Seq(FakeIssue(summary = "Fake Issue"))
        val searchClient = FakeSearchRestClient(doSearchJql =
          _ => FakeSearchResult(issues = issues)
        )
        val restClient = FakeJiraRestClient(searchClient = searchClient)
        for service <- ZIO.succeed(FakeJiraService(restClient = restClient))
        yield JiraIssueRepoImpl(service)
      )

      suite("searchJqlが課題を返さなかった場合"):
        for
          sut <- ZIO.service[JiraIssueRepoImpl]
          actual <- sut.list("filterId = 123456")
        yield Chunk(test("課題を返さないこと"):
          assertTrue:
            actual.isEmpty
        )
      .provide(ZLayer:
        val searchClient =
          FakeSearchRestClient(doSearchJql =
            _ => FakeSearchResult(issues = Nil)
          )
        val restClient = FakeJiraRestClient(searchClient = searchClient)
        for service <- ZIO.succeed(FakeJiraService(restClient = restClient))
        yield JiraIssueRepoImpl(service)
      )

      suite("searchJqlでエラーが発生した場合"):
        for
          sut <- ZIO.service[JiraIssueRepoImpl]
          isFailure <- sut.list(123456).isFailure
        yield Chunk(test("失敗すること"):
          assertTrue:
            isFailure
        )
      .provide(ZLayer:
        val searchClient =
          FakeSearchRestClient(doSearchJql = _ => throw RuntimeException())
        val restClient = FakeJiraRestClient(searchClient = searchClient)
        for service <- ZIO.succeed(FakeJiraService(restClient = restClient))
        yield JiraIssueRepoImpl(service)
      )

    suiteAll("save"):
      suite("updateIssueが正常終了した場合"):
        for
          sut <- ZIO.service[JiraIssueRepoImpl]
          isSuccess <- sut
            .save(FakeJiraIssue(underlying = FakeIssue(key = "FAKE-1")))
            .isSuccess
        yield Chunk(test("正常終了すること"):
          assertTrue:
            isSuccess
        )
      .provide(ZLayer:
        val issueClient = FakeIssueRestClient(doUpdateIssue = (_, _) => ())
        val restClient = FakeJiraRestClient(issueClient = issueClient)
        for service <- ZIO.succeed(FakeJiraService(restClient = restClient))
        yield JiraIssueRepoImpl(service)
      )

      suite("updateIssueが異常終了した場合"):
        for
          sut <- ZIO.service[JiraIssueRepoImpl]
          isFailure <- sut
            .save(FakeJiraIssue(underlying = FakeIssue(key = "FAKE-1")))
            .isFailure
        yield Chunk(test("異常終了すること"):
          assertTrue:
            isFailure
        )
      .provide(ZLayer:
        val issueClient = FakeIssueRestClient(doUpdateIssue =
          (_, _) => throw RuntimeException()
        )
        val restClient = FakeJiraRestClient(issueClient = issueClient)
        for service <- ZIO.succeed(FakeJiraService(restClient = restClient))
        yield JiraIssueRepoImpl(service)
      )

    suiteAll("transition"):
      suite("transitionが正常終了した場合"):
        for
          sut <- ZIO.service[JiraIssueRepoImpl]
          isSuccess <- sut.transition(FakeJiraIssue(), 123).isSuccess
        yield Chunk(test("正常終了すること"):
          assertTrue:
            isSuccess
        )
      .provide(ZLayer:
        val issueClient = FakeIssueRestClient(doTransitionIssue = (_, _) => ())
        val restClient = FakeJiraRestClient(issueClient = issueClient)
        for service <- ZIO.succeed(FakeJiraService(restClient = restClient))
        yield JiraIssueRepoImpl(service)
      )

      suite("transitionが異常終了した場合"):
        for
          sut <- ZIO.service[JiraIssueRepoImpl]
          isFailure <- sut.transition(FakeJiraIssue(), 123).isFailure
        yield Chunk(test("異常終了すること"):
          assertTrue:
            isFailure
        )
      .provide(ZLayer:
        val issueClient = FakeIssueRestClient(doTransitionIssue =
          (_, _) => throw RuntimeException()
        )
        val restClient = FakeJiraRestClient(issueClient = issueClient)
        for service <- ZIO.succeed(FakeJiraService(restClient = restClient))
        yield JiraIssueRepoImpl(service)
      )
