
        fun getNotificationIdFromScheme(scheme: String?): String? {
            if (TextUtils.isEmpty(scheme)) {
                return null
            }
            val decodedScheme = URLDecoder.decode(scheme, "UTF-8")
            val uri = Uri.parse(decodedScheme)
            return uri.getQueryParameter("notification_id")
        }

        fun getNotificationDataIdFromScheme(scheme: String?): Int? {
            if (TextUtils.isEmpty(scheme)) {
                return null
            }
            val decodedScheme = URLDecoder.decode(scheme, "UTF-8")
            val uri = Uri.parse(decodedScheme)
            val data_id = uri.getQueryParameter("data_id")
            return if (data_id == null) null else data_id.toInt()
        }

        private fun createSchemeResolver(uri: Uri): SchemeResolver? {
            return when (uri.host) {
                "not-jump" -> null
                "diaries" -> DiarySchemeResolver()
                "online-reports" -> OnlineReportSchemeResolver()
                "refund" -> RefundSchemeResolver()
                "advisories" -> AdvisoriesSchemeResolver()
                "scale-distributions" -> ScaleSchemeResolver()
                "referral-notice", "life-notice" -> NotificationSchemeResolver()
                "cbti-chapters" -> CbtiChapterSchemeResolver()
                "cbti-final-reports" -> CbtiFinalReportSchemeResolver()
                "relaxations" -> RelaxationSchemeResolver()
                "anxieties-and-faiths" -> AnxietyFaithReminderSchemeResolver()
                "advisory-list" -> AdvisoryListSchemeResolver()
                "booking-list" -> BookingListSchemeResolver()
                "diary-evaluation-list" -> DiaryEvaluationListSchemeResolver()
                "booking-detail" -> TelBookingDetailSchemeResolver()
                "diary-evaluations" -> DiaryEvaluationSchemeResolver()
                "message-boards" -> null
                else -> null
            }
        }
    }
}