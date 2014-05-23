package com.emailInbox



class EmailMessageJob {

    def emailInboxService

    static triggers = {
    }

    def execute() {

        emailInboxService.fetchUnreadMail()
    }
}
