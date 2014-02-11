package com.social

import grails.plugins.springsecurity.Secured
import org.social.SocialProfile
import org.social.login.User

@Secured(['ROLE_ADMIN', 'ROLE_USER'])
class HomeController {

    def springSecurityService

    def index = {
        User user = springSecurityService.currentUser as User
        List<SocialProfile> socialProfileList = SocialProfile.findAllByUser(user)
        [user: user, socialProfileList: socialProfileList]
    }

    def profile = {
        User user = springSecurityService.currentUser as User
        render(view: '/home/profile', model: [user: user])
    }

    def updateProfile = {
        User user = springSecurityService.currentUser as User
        user.username = params.username
        user.name = params.name
        user.save(flush: true)
        flash.message = 'User updated successfully'
        render(view: '/home/profile', model: [user: user])
    }
}
