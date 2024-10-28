package io.github.kijuky.zio.jira

import com.atlassian.jira.rest.client.api.IssueRestClient.Expandos
import com.atlassian.jira.rest.client.api.domain.*
import com.atlassian.jira.rest.client.api.domain.input.*
import com.atlassian.jira.rest.client.api.{
  GetCreateIssueMetadataOptions,
  IssueRestClient
}
import io.atlassian.util.concurrent.{Promise, Promises}

import java.io.{File, InputStream}
import java.net.URI
import java.util.concurrent.CompletableFuture
import scala.jdk.CollectionConverters.*

case class FakeIssueRestClient(
  doCreateIssue: IssueInput => BasicIssue = null,
  doUpdateIssue: (String, IssueInput) => Unit = null,
  doGetCreateIssueMetadata: GetCreateIssueMetadataOptions => Seq[CimProject] =
    null,
  doCreateIssues: Seq[IssueInput] => BulkOperationResult[BasicIssue] = null,
  doGetCreateIssueMetaProjectIssueTypes: (
    String,
    Long,
    Int
  ) => Page[IssueType] = null,
  doGetCreateIssueMetaFields: (
    String,
    String,
    Long,
    Int
  ) => Page[CimFieldInfo] = null,
  doGetIssue: String => Issue = null,
  doGetIssueDetail: (String, Seq[Expandos]) => Issue = null,
  doDeleteIssue: (String, Boolean) => Unit = null,
  doGetWatchers: URI => Watchers = null,
  doGetVotes: URI => Votes = null,
  doGetTransitionsUri: URI => Seq[Transition] = null,
  doGetTransitionsIssue: Issue => Seq[Transition] = null,
  doTransitionUri: (URI, TransitionInput) => Unit = null,
  doTransitionIssue: (Issue, TransitionInput) => Unit = null,
  doVote: URI => Unit = null,
  doUnvote: URI => Unit = null,
  doWatch: URI => Unit = null,
  doUnwatch: URI => Unit = null,
  doAddWatcher: (URI, String) => Unit = null,
  doRemoveWatcher: (URI, String) => Unit = null,
  doLinkIssue: LinkIssuesInput => Unit = null,
  doAddAttachment: (URI, InputStream, String) => Unit = null,
  doAddAttachments: (URI, Seq[AttachmentInput]) => Unit = null,
  doAddAttachmentFiles: (URI, Seq[File]) => Unit = null,
  doAddComment: (URI, Comment) => Unit = null,
  doGetAttachment: URI => InputStream = null,
  doAddWorklog: (URI, WorklogInput) => Unit = null
) extends IssueRestClient:
  override def createIssue(issue: IssueInput): Promise[BasicIssue] =
    Promises.forCompletionStage(
      CompletableFuture.supplyAsync(() => doCreateIssue(issue))
    )

  override def updateIssue(issueKey: String, issue: IssueInput): Promise[Void] =
    Promises.forCompletionStage(
      CompletableFuture.runAsync(() => doUpdateIssue(issueKey, issue))
    )

  override def getCreateIssueMetadata(
    options: GetCreateIssueMetadataOptions
  ): Promise[java.lang.Iterable[CimProject]] =
    Promises.forCompletionStage(
      CompletableFuture.supplyAsync(() =>
        doGetCreateIssueMetadata(options).asJava
      )
    )

  override def createIssues(
    issues: java.util.Collection[IssueInput]
  ): Promise[BulkOperationResult[BasicIssue]] =
    Promises.forCompletionStage(
      CompletableFuture.supplyAsync(() => doCreateIssues(issues.asScala.toSeq))
    )

  override def getCreateIssueMetaProjectIssueTypes(
    projectIdOrKey: String,
    startAt: java.lang.Long,
    maxResults: Integer
  ): Promise[Page[IssueType]] =
    Promises.forCompletionStage(
      CompletableFuture.supplyAsync(() =>
        doGetCreateIssueMetaProjectIssueTypes(
          projectIdOrKey,
          startAt.longValue(),
          maxResults.intValue()
        )
      )
    )

  override def getCreateIssueMetaFields(
    projectIdOrKey: String,
    issueTypeId: String,
    startAt: java.lang.Long,
    maxResults: Integer
  ): Promise[Page[CimFieldInfo]] =
    Promises.forCompletionStage(
      CompletableFuture.supplyAsync(() =>
        doGetCreateIssueMetaFields(
          projectIdOrKey,
          issueTypeId,
          startAt.longValue(),
          maxResults.intValue()
        )
      )
    )

  override def getIssue(issueKey: String): Promise[Issue] =
    Promises.forCompletionStage(
      CompletableFuture.supplyAsync(() => doGetIssue(issueKey))
    )

  override def getIssue(
    issueKey: String,
    expand: java.lang.Iterable[Expandos]
  ): Promise[Issue] =
    Promises.forCompletionStage(
      CompletableFuture.supplyAsync(() =>
        doGetIssueDetail(issueKey, expand.asScala.toSeq)
      )
    )

  override def deleteIssue(
    issueKey: String,
    deleteSubtasks: Boolean
  ): Promise[Void] =
    Promises.forCompletionStage(
      CompletableFuture.runAsync(() => doDeleteIssue(issueKey, deleteSubtasks))
    )

  override def getWatchers(watchersUri: URI): Promise[Watchers] =
    Promises.forCompletionStage(
      CompletableFuture.supplyAsync(() => doGetWatchers(watchersUri))
    )

  override def getVotes(votesUri: URI): Promise[Votes] =
    Promises.forCompletionStage(
      CompletableFuture.supplyAsync(() => doGetVotes(votesUri))
    )

  override def getTransitions(
    transitionsUri: URI
  ): Promise[java.lang.Iterable[Transition]] =
    Promises.forCompletionStage(
      CompletableFuture.supplyAsync(() =>
        doGetTransitionsUri(transitionsUri).asJava
      )
    )

  override def getTransitions(
    issue: Issue
  ): Promise[java.lang.Iterable[Transition]] =
    Promises.forCompletionStage(
      CompletableFuture.supplyAsync(() => doGetTransitionsIssue(issue).asJava)
    )

  override def transition(
    transitionsUri: URI,
    transitionInput: TransitionInput
  ): Promise[Void] =
    Promises.forCompletionStage(
      CompletableFuture.runAsync(() =>
        doTransitionUri(transitionsUri, transitionInput)
      )
    )

  override def transition(
    issue: Issue,
    transitionInput: TransitionInput
  ): Promise[Void] =
    Promises.forCompletionStage(
      CompletableFuture.runAsync(() =>
        doTransitionIssue(issue, transitionInput)
      )
    )

  override def vote(votesUri: URI): Promise[Void] =
    Promises.forCompletionStage(
      CompletableFuture.runAsync(() => doVote(votesUri))
    )

  override def unvote(votesUri: URI): Promise[Void] =
    Promises.forCompletionStage(
      CompletableFuture.runAsync(() => doUnvote(votesUri))
    )

  override def watch(watchersUri: URI): Promise[Void] =
    Promises.forCompletionStage(
      CompletableFuture.runAsync(() => doWatch(watchersUri))
    )

  override def unwatch(watchersUri: URI): Promise[Void] =
    Promises.forCompletionStage(
      CompletableFuture.runAsync(() => doUnwatch(watchersUri))
    )

  override def addWatcher(watchersUri: URI, username: String): Promise[Void] =
    Promises.forCompletionStage(
      CompletableFuture.runAsync(() => doAddWatcher(watchersUri, username))
    )

  override def removeWatcher(
    watchersUri: URI,
    username: String
  ): Promise[Void] =
    Promises.forCompletionStage(
      CompletableFuture.runAsync(() => doRemoveWatcher(watchersUri, username))
    )

  override def linkIssue(linkIssuesInput: LinkIssuesInput): Promise[Void] =
    Promises.forCompletionStage(
      CompletableFuture.runAsync(() => doLinkIssue(linkIssuesInput))
    )

  override def addAttachment(
    attachmentsUri: URI,
    in: InputStream,
    filename: String
  ): Promise[Void] =
    Promises.forCompletionStage(
      CompletableFuture.runAsync(() =>
        doAddAttachment(attachmentsUri, in, filename)
      )
    )

  override def addAttachments(
    attachmentsUri: URI,
    attachments: Array[? <: AttachmentInput]
  ): Promise[Void] =
    Promises.forCompletionStage(
      CompletableFuture.runAsync(() =>
        doAddAttachments(attachmentsUri, attachments.toIndexedSeq)
      )
    )

  override def addAttachments(
    attachmentsUri: URI,
    files: Array[? <: File]
  ): Promise[Void] =
    Promises.forCompletionStage(
      CompletableFuture.runAsync(() =>
        doAddAttachmentFiles(attachmentsUri, files.toIndexedSeq)
      )
    )

  override def addComment(commentsUri: URI, comment: Comment): Promise[Void] =
    Promises.forCompletionStage(
      CompletableFuture.runAsync(() => doAddComment(commentsUri, comment))
    )

  override def getAttachment(attachmentUri: URI): Promise[InputStream] =
    Promises.forCompletionStage(
      CompletableFuture.supplyAsync(() => doGetAttachment(attachmentUri))
    )

  override def addWorklog(
    worklogUri: URI,
    worklogInput: WorklogInput
  ): Promise[Void] =
    Promises.forCompletionStage(
      CompletableFuture.runAsync(() => doAddWorklog(worklogUri, worklogInput))
    )
