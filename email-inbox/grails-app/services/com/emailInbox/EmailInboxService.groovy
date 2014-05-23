package com.emailInbox

import grails.util.GrailsUtil
import org.codehaus.groovy.grails.web.context.ServletContextHolder

import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.mail.*
import javax.mail.search.AndTerm
import javax.mail.search.ComparisonTerm
import javax.mail.search.FlagTerm
import javax.mail.search.ReceivedDateTerm
import javax.mail.search.SearchTerm
import javax.mail.Flags.Flag
import javax.mail.internet.*
import javax.mail.*;
import javax.mail.search.MessageIDTerm
import javax.mail.internet.*
import com.emailInbox.EmailVO
import com.emailInbox.EmailAttachment
import com.emailInbox.EmailMessage


class EmailInboxService {
    def grailsApplication

    public String getStaticResourcesDirPath() {
        String path = '';
        if (GrailsUtil.developmentEnv) {
            path = ServletContextHolder.getServletContext().getRealPath("/")
        } else {
            path = grailsApplication.config.grails.defaultDocumentPath
        }
        return path
    }

    def fetchUnreadMail() {
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        List<EmailVO> emailVOs = []
        try {
            Session session = Session.getInstance(props, null);
            Store store = session.getStore("imaps");
            final String username = grailsApplication.config.grails.mail.username
            final String password = grailsApplication.config.grails.mail.password
            store.connect("imap.gmail.com", 993, username, password);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT, new Date() - 6);
            List messages = inbox.search(newerThan);


            messages.eachWithIndex { Message message, int i1 ->
                EmailVO emailVO = new EmailVO()
                Part part = message as Part
                emailVO = writePart(part, emailVO)
                emailVOs.add(emailVO)
            }
        } catch (Exception mex) {
            mex.printStackTrace();
        }
        emailVOs.eachWithIndex { EmailVO emailVO, int i2 ->
            EmailMessage unreadMail = EmailMessage.findByMessagId(emailVO?.messagId)
            if (!unreadMail) {
                unreadMail = new EmailMessage(emailVO)
                if (unreadMail.validate()) {
                    unreadMail.save(flush: true)
                    saveEmailAttachment(emailVO, unreadMail)
                } else {
                    unreadMail.errors.allErrors.each {
                        println("********************" + it)
                    }
                }
            }
        }
    }

    def saveEmailAttachment(EmailVO emailVO, EmailMessage emailMessage) {

        emailVO.inputStream.eachWithIndex { InputStream is, int i ->
            String path = staticResourcesDirPath
            File file1 = new File(path + "/email_document/")
            if (!file1.exists()) {
                if (file1.mkdir()) {
                    System.out.println("Directory is created!");
                } else {
                    System.out.println("Failed to create directory!");
                }
            }
            File file2 = new File(path + "/email_document/emailMessage_${emailMessage?.id}/")
            if (!file2.exists()) {
                if (file2.mkdir()) {
                    System.out.println(" sub Directory is created!");
                } else {
                    System.out.println("Failed to create sub directory!");
                }
            }
            String contentType = emailVO.attachmentType[i]
            String attachmentName = emailVO.attachmentNames[i]
            String extension = emailVO.extensions[i]
            String uuid = UUID.randomUUID().toString()
            File file = new File(path + "/email_document/emailMessage_${emailMessage?.id}/" + uuid);

            if (file.createNewFile()) {
                System.out.println("File is created!");
            } else {
                System.out.println("File already exists.");
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file)
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = is.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, read);
            }

            EmailAttachment attachment = new EmailAttachment()
            attachment.attachmentName = attachmentName
            attachment.attachmentPathName = uuid
            attachment.attachmentContentType = contentType
            attachment.emailMessage = emailMessage
            emailMessage.addToEmailAttachments(attachment)
            attachment.save(flush: true)

        }
    }

    public static EmailVO writePart(Part p, EmailVO emailVO) throws Exception {
        if (p instanceof Message)
        //Call methos writeEnvelope
            emailVO = writeEnvelope((Message) p, emailVO);

        if (p.isMimeType("text/plain")) {
//            System.out.println("This is plain text");
//            System.out.println((String) p.getContent());
        }
        //check if the content has attachment
        else if (p.isMimeType("multipart/*")) {
//            System.out.println("This is a Multipart");
//            System.out.println("---------------------------");
            Multipart mp = (Multipart) p.getContent();
            int count = mp.getCount();
            for (int i = 0; i < count; i++)
                emailVO = writePart(mp.getBodyPart(i), emailVO);
        }
        else if (p.isMimeType("message/rfc822")) {
//            System.out.println("This is a Nested Message");
//            System.out.println("---------------------------");
//            writePart((Part) p.getContent());
        }
        //check if the content is an inline image
        else if (p.isMimeType("image/jpeg")) {
//            System.out.println("--------> image/jpeg");
//            Object o = p.getContent();
//
//            InputStream x = (InputStream) o;
            // Construct the required byte array
//            System.out.println("x.length = " + x.available());
//            int i = 0;
//            byte[] bArray = new byte[x.available()];
//
//            while ((i = (int) ((InputStream) x).available()) > 0) {
//                int result = (int) (((InputStream) x).read(bArray));
//                if (result == -1)
//                    break;
//            }
//            FileOutputStream f2 = new FileOutputStream("/home/nextthoughts/credio_document/emailMessage/image.jpg");
//            f2.write(bArray);
        } else if (p.isMimeType("image/png")) {
//            System.out.println("--------> image/png");
//            Object o = p.getContent();
//
//            InputStream x = (InputStream) o;
//            Construct the required byte array
//            System.out.println("x.length = " + x.available());
//            int i = 0;
//            byte[] bArray = new byte[x.available()];
//
//            while ((i = (int) ((InputStream) x).available()) > 0) {
//                int result = (int) (((InputStream) x).read(bArray));
//                if (result == -1)
//                    break;
//            }
//            File f2 = new File("/home/hitenpratap/TLA2/image.png");
//            FileUtils.writeByteArrayToFile(f2, bArray)
        } else if (p.getContentType().contains("image/")) {
//            System.out.println("content type" + p.getContentType());
//            File f = new File("image" + new Date().getTime() + ".jpg");
//            DataOutputStream output = new DataOutputStream(
//                    new BufferedOutputStream(new FileOutputStream(f)));
//            com.sun.mail.util.BASE64DecoderStream test =
//                    (com.sun.mail.util.BASE64DecoderStream) p
//                            .getContent();
//            byte[] buffer = new byte[1024];
//            int bytesRead;
//            while ((bytesRead = test.read(buffer)) != -1) {
//                output.write(buffer, 0, bytesRead);
//            }
        } else {
            Object o = p.getContent();
            if (o instanceof String) {
//                System.out.println("This iss a string");
//                System.out.println("---------------------------");
//                System.out.println((String) o);
                emailVO.messageBody = (String) o
            } else if (o instanceof InputStream) {
//                System.out.println("This is just an input stream");
//                System.out.println("---------------------------");
                InputStream is = (InputStream) o;
                is = (InputStream) o;
                emailVO.inputStream.add(is)
                List<String> contentTypes = p.getContentType()?.split(";")
                String contentType = contentTypes.first()
                List<String> list = contentTypes.last().tokenize(".")
                String extension = list.last().trim()
                extension = extension.endsWith("\"") ? extension.substring(0, extension.size() - 1) : extension
                extension = extension.endsWith("'") ? extension.substring(0, extension.size() - 1) : extension
                extension = extension.trim()
                extension = "." + extension
                emailVO.extensions.add(extension)
                String attachmentName = contentTypes.last()
                attachmentName = attachmentName.replace("name=", "")
                attachmentName = attachmentName.trim()
                attachmentName = attachmentName.startsWith("\"") ? attachmentName.substring(1, attachmentName.size() - 1) : attachmentName
                attachmentName = attachmentName.trim()
                emailVO.attachmentNames.add(attachmentName)
                emailVO.attachmentType.add(contentType)

            } else {
//                System.out.println("This is an unknown type");
//                System.out.println("---------------------------");
//                System.out.println(o.toString());
            }
        }
        return emailVO
    }


    public static EmailVO writeEnvelope(Message m, EmailVO emailVO) throws Exception {
        System.out.println("---------------------------");

        Address[] a;
        String pattern = ""
        // FROM
        if ((a = m.getFrom()) != null) {
            for (int j = 0; j < a.length; j++) {
//                System.out.println("FROM: " + a[j].toString());
                pattern = a[j].toString()
            }
        }

        // TO
//        if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
//            for (int j = 0; j < a.length; j++)
//                System.out.println("TO: " + a[j].toString());
//        }

        // SUBJECT
        if (m.getSubject() != null)
//            System.out.println("SUBJECT: " + m.getSubject());
            emailVO.subject = m.getSubject()

        Pattern p = Pattern.compile("<.*?>");
        Matcher m1 = p.matcher(pattern);
        String email = ""
        if (m1.find()) {
            email = m1.group(0)
        }
        if (email) {
            String name = pattern.replace(email, "").trim()
            email = email.substring(1, email?.size() - 1)
            emailVO.senderName = name
            emailVO.senderEmail = email
        } else {
            emailVO.senderName = pattern?.trim()
            emailVO.senderEmail = pattern?.trim()
        }
        String messageID = m.messageID.substring(1, m.messageID.size() - 1)
        String messageSendDate = m.getSentDate()?.toString()
        emailVO.messagId = messageID
        emailVO.sentDate = messageSendDate

        return emailVO
    }


    def replyEmail(EmailMessage emailMessage, String replyText) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        final String username = grailsApplication?.config?.grails?.mail?.username
        final String password = grailsApplication?.config?.grails?.mail?.password
        Session session = Session.getDefaultInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", username, password);

            //Create a Folder object and open the folder
            Folder folder = store.getFolder("inbox");
            folder.open(Folder.READ_ONLY);


            String messageId = "<" + emailMessage?.messagId + ">"
            SearchTerm searchTerm = new MessageIDTerm(messageId);
            List messages = folder.search(searchTerm);

            Message message = messages?.first()

            // Get all the information from the message
            String from = InternetAddress.toString(message.getFrom());
            if (from != null) {
                System.out.println("From: " + from);
            }
            String replyTo = InternetAddress.toString(message.getReplyTo());
            if (replyTo != null) {
                System.out.println("Reply-to: " + replyTo);
            }
            String to = emailMessage?.senderEmail
            if (to != null) {
                System.out.println("To: " + to);
            }

            String subject = message.getSubject();
            if (subject != null) {
                System.out.println("Subject: " + subject);
            }
            Date sent = message.getSentDate();
            if (sent != null) {
                System.out.println("Sent: " + sent);
            }
            System.out.println(message.getContent());

            // compose the message to forward
            Message message2 = new MimeMessage(session);
            message2 = (MimeMessage) message.reply(false);
            message2.setSubject("RE: " + message.getSubject());
            String defaultFrom = grailsApplication.config.grails.mail.default.from
            message2.setFrom(new InternetAddress(defaultFrom));
            message2.setReplyTo(message.getReplyTo());

            message2.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
//            message2.addRecipient(Message.RecipientType.TO);

            // Create your new message part
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(replyText);

            // Create a multi-part to combine the parts
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // Create and fill part for the forwarded content
//            messageBodyPart = new MimeBodyPart();
//            messageBodyPart.setDataHandler(message.getDataHandler());

            // Add part to multi part
//            multipart.addBodyPart(messageBodyPart);

            // Associate multi-part with message
            message2.setContent(multipart);

            // Send message
            Transport.send(message2);

            System.out.println("message replied successfully ....");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
