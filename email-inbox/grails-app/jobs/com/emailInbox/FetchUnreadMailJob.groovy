package com.emailInbox

class FetchUnreadMailJob {
    def emailInboxService

    static concurrent = false
    static triggers = {
        simple repeatInterval: 1000 * 60 * 60l // execute job once in 5 seconds
    }

    def execute() {
        println("**************started fetching unread mail*************")
        emailInboxService.fetchUnreadMail()
        println("****************completed unread mail*************")
    }
}
