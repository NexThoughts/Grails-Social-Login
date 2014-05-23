package com.emailInbox


class EmailAttachment {

    String attachmentName
    String attachmentPathName
    String attachmentContentType

    static belongsTo = [emailMessage: EmailMessage]
    static constraints = {
        attachmentPathName(nullable: true)
        attachmentName(nullable: true)
        attachmentContentType(nullable: true)
    }
}

