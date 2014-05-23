package com.social.user

import org.codehaus.groovy.grails.validation.Validateable
import org.social.login.User

@Validateable
class SignUpVO {

    String name
    String email
    String password
    String confirmPassword

    static constraints = {
        name(nullable: false)
        email(nullable: false, email: true, blank: false, validator: { val, obj ->
            if (User.findByUsername(obj.email)) {
                return "user.already.exists.message"
            }
        })
        password(nullable: false, blank: false)
        confirmPassword(nullable: false, blank: false, validator: { val, obj ->
            if (obj.password != val) {
                return "password.confirm.password.do.not.match"
            }
        })
    }
}
