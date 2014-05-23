package org.social.login

import com.social.SocialConnectVO
import org.social.SocialProfile

class User {

    transient springSecurityService

    String name
    String username
    String password
    boolean enabled
    boolean accountExpired
    boolean accountLocked
    boolean passwordExpired


    List<SocialProfile> socialProfileList = []


    static hasMany = [socialProfileList: SocialProfile]

    static constraints = {
        username(blank: false, unique: true, email: true)
        name(blank: false)
        password(blank: false)
    }

    static mapping = {
        password column: '`password`'
    }

    Set<Role> getAuthorities() {
        UserRole.findAllByUser(this).collect { it.role } as Set
    }

    def beforeInsert() {
        encodePassword()
    }

    def beforeUpdate() {
        if (isDirty('password')) {
            encodePassword()
        }
    }


    protected void encodePassword() {
//        password = springSecurityService.encodePassword(password)
    }

    User() {

    }

    User(SocialConnectVO socialConnectVO) {
        this.name = socialConnectVO?.firstName
        this.username = socialConnectVO?.email
        this.accountLocked = false
        this.accountExpired = false
        this.enabled = true
        this.password = socialConnectVO.defaultPassword
    }


    User(twitter4j.User twitterUser) {
        this.name = twitterUser?.screenName
        this.username = "${twitterUser?.name}@twitter.com"
        this.accountLocked = false
        this.accountExpired = false
        this.enabled = true
        this.password = UUID.randomUUID().toString()
    }
}
