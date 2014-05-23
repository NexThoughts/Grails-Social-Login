package email

class EmailMessage {

    String messagId
    String senderName
    String senderEmail
    String messageSentDate
    String messageSubject
    String messageBody

    static hasMany = [emailAttachments: EmailAttachment]
    static constraints = {
        messagId(nullable: false)
        senderName(nullable: false)
        senderEmail(nullable: false)
        messageSentDate(nullable: false)
        messageSubject(nullable: true)
        messageBody(nullable: true)
    }
    static mapping = {
        messageBody type: "text"
        messageSubject type: "text"
    }

    EmailMessage(EmailVO emailVO){
        this.senderEmail = emailVO?.senderEmail
        this.senderName = emailVO?.senderName
        this.messagId = emailVO?.messagId
        this.messageSentDate = emailVO?.sentDate
        this.messageSubject = emailVO?.subject
        this.messageBody = emailVO?.messageBody
    }
}

