package email

import org.codehaus.groovy.grails.validation.Validateable

@Validateable
class EmailVO {

    String messagId
    String senderName
    String senderEmail
    String sentDate
    String subject
    String messageBody
    List<String> attachmentType = []
    List<InputStream> inputStream = []
    List<String> extensions = []
    List<String> attachmentNames=[]


    static constraints = {
        messagId(nullable: false)
        senderName(nullable: false)
        senderEmail(nullable: false)
        sentDate(nullable: false)
        subject(nullable: true)
        messageBody(nullable: true)
        inputStream(nullable: true)
    }
}
