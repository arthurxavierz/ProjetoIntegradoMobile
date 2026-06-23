package com.example.cicloestudos3.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.cicloestudos3.notification.NotificationHelper
import java.util.UUID
import java.util.concurrent.TimeUnit

class RevisionReminderWorker(
    ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val revisionId  = inputData.getInt(KEY_REVISION_ID, -1)
        val topicTitle  = inputData.getString(KEY_TOPIC_TITLE)  ?: return Result.failure()
        val subjectName = inputData.getString(KEY_SUBJECT_NAME) ?: ""

        NotificationHelper.sendRevisionNotification(
            applicationContext, revisionId, topicTitle, subjectName
        )
        return Result.success()
    }

    companion object {
        const val KEY_REVISION_ID  = "revision_id"
        const val KEY_TOPIC_TITLE  = "topic_title"
        const val KEY_SUBJECT_NAME = "subject_name"

        /**
         * Schedules a one-time notification at [scheduledAt] and returns the
         * WorkRequest UUID (to be stored in the Revision entity).
         */
        fun schedule(
            context: Context,
            revisionId: Int,
            topicTitle: String,
            subjectName: String,
            scheduledAt: Long
        ): UUID {
            val delay = (scheduledAt - System.currentTimeMillis()).coerceAtLeast(0L)
            val data  = workDataOf(
                KEY_REVISION_ID  to revisionId,
                KEY_TOPIC_TITLE  to topicTitle,
                KEY_SUBJECT_NAME to subjectName
            )
            val request = OneTimeWorkRequestBuilder<RevisionReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag("revision_$revisionId")
                .build()

            WorkManager.getInstance(context).enqueue(request)
            return request.id
        }

        /** Cancels any pending reminder for the given revision. */
        fun cancel(context: Context, revisionId: Int) {
            WorkManager.getInstance(context).cancelAllWorkByTag("revision_$revisionId")
        }
    }
}
