package com.social

import com.social.user.SignUpVO
import grails.converters.JSON
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.social.SocialProfile
import org.social.login.Role
import org.social.login.User
import org.social.login.UserRole
import twitter4j.auth.AccessToken
import twitter4j.auth.RequestToken

class SignInController {

    public static final String FACEBOOK_PERMISSIONS = "read_stream,publish_stream,user_about_me,user_status,user_birthday,user_education_history,user_hometown,user_religion_politics,user_location,user_work_history,email,user_website,user_likes,user_religion_politics,user_friends"
    private static final String GOOGLE_PROFILE_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
    private static final String GOOGLE_EMAIL_SCOPE = "https://www.googleapis.com/auth/userinfo.email";
    public static final String REQUEST_TOKEN = "REQUEST_TOKEN";
    public static final String TWITTER_TOKEN = "TWITTER_TOKEN";
    public static final String TWITTER_SECRET = "TWITTER_SECRET";


    def signinService
    def springSecurityService

    def grailsApplication


    def signup = {
        SignUpVO signUpVO = new SignUpVO()
        render(view: '/signIn/index', model: [signUpVO: signUpVO])

    }

    def createUser = { SignUpVO signUpVO ->
        println '----------------------------' + params
        if (signUpVO.validate()) {
            User user = new User()
            user.name = signUpVO.name
            user.username = signUpVO.email
            user.password = springSecurityService.encodePassword(signUpVO.password)
            signUpVO.password
            user.enabled = true
            user.accountExpired = false
            user.accountLocked = false
            user.passwordExpired = false
            if (user.save(flush: true)) {
                def userRole = Role.findByAuthority('ROLE_USER')
                UserRole.create(user, userRole)
            }
            flash.message = 'User Created Successfully'
            springSecurityService.reauthenticate(user.username)
            redirect(action: 'index', controller: 'home')
        } else {
            signUpVO.errors.allErrors.each {
                println '----------Error------------' + it
            }
            render(view: '/signIn/index', model: [signUpVO: signUpVO])
        }
    }

//  ===========================  Handle LinkedIn Signup/Login  ===================================
    def connectLinkedIn = {
        String serverUrl = grailsApplication.config.grails.serverURL
        String callbackurl = "${serverUrl}/signIn/linkedInCallback"
        String randomId = UUID.randomUUID().toString()
        println("***************SERVER URL ***************** " + serverUrl)
        String linkedApiKey = grailsApplication.config.linkedin.key
        println("******************** LinkedIn API Key ***************" + linkedApiKey)
        String url = "https://www.linkedin.com/uas/oauth2/authorization?response_type=code&client_id=${linkedApiKey}&scope=r_fullprofile%20r_emailaddress&state=${randomId}&redirect_uri=${callbackurl}"
        redirect(url: url)
    }

    def linkedInCallback = {
        SocialProfile socialProfile = null
        println("******************** LinkedIn Callback *******************")
        String authCode = params.code
        if (authCode) {
            String accessToken = signinService.postCallForLinkedInAccessToken(authCode)
            if (accessToken) {
                LinkedInConnectVO linkedInSignupVO = signinService.getLinkedInUserProfile(accessToken)
                User user = springSecurityService.currentUser as User
                if (!user) {
                    user = User.findByUsername(linkedInSignupVO?.email)
                }
                if (user) {
                    println("****************************** User Found : ${user}***************************")
                    handleSocialProfileCreateAndUpdateWhenUserExists(user, linkedInSignupVO)
                } else {
                    socialProfile = SocialProfile.findByEmail(linkedInSignupVO?.email)
                    if (socialProfile) {
                        user = socialProfile?.user
                        if (socialProfile?.type == SocialProfileType.LINKED_IN) {
                            signinService.updateLinkedInSocialProfile(linkedInSignupVO, socialProfile)
                        } else {
                            socialProfile = signinService.createLinkedInSocialProfile(user, linkedInSignupVO)
                        }
                    } else {
                        println("************************Creating New LinkedIn Social Profile & User ****************")
                        user = new User(linkedInSignupVO)
                        user.password = springSecurityService.encodePassword(user.password)
                        if (!user.save(flush: true)) {
                            redirect(action: 'auth', controller: 'login', params: [messageString: 'Unable to Create user.'])
                        } else {
                            UserRole.create(user, Role.findByAuthority('ROLE_USER'), true)
                            socialProfile = signinService.createLinkedInSocialProfile(user, linkedInSignupVO)
                        }
                    }
                }
                springSecurityService?.reauthenticate(user?.username)
                redirect(action: 'index', controller: 'home')
            } else {
                redirect(action: 'auth', controller: 'login')
            }
        }
    }

    private SocialProfile handleSocialProfileCreateAndUpdateWhenUserExists(User user, LinkedInConnectVO linkedInSignupVO) {
        SocialProfile socialProfile = SocialProfile.findByUserAndType(user, SocialProfileType.LINKED_IN)
        if (socialProfile) {
            println("************************LinkedIn Social Profile Found ****************")
            signinService.updateLinkedInSocialProfile(linkedInSignupVO, socialProfile)
        } else {
            println("************************LinkedIn Social Profile Not Found, so creating it ****************")
            socialProfile = signinService.createLinkedInSocialProfile(user, linkedInSignupVO)
        }
        return socialProfile
    }

// ====================== Handle Facebook Signup & Login ===================
    def connectFacebook = {
        println("************************* Connecting Facebook ***********************")
        String serverUrl = grailsApplication.config.grails.serverURL
        String callbackurl = "${serverUrl}/signIn/facebookCallback"
        String facebookApiKey = grailsApplication.config.facebook.key
        println("********************* Facebook Callback URL ******************" + callbackurl)
        String facebookUrl = "https://graph.facebook.com/oauth/authorize?client_id=${facebookApiKey}&redirect_uri=${callbackurl}?cmd=add&scope=${FACEBOOK_PERMISSIONS}"
        println("********************** Facebook Redirect URL ***************" + facebookUrl)
        redirect(url: facebookUrl)
    }

    def facebookCallback = {
        String authCode = params.code
        SocialProfile socialProfile = null
        if (authCode) {
            String serverUrl = grailsApplication.config.grails.serverURL
            String facebookApiKey = grailsApplication.config.facebook.key
            String facebookApiSecret = grailsApplication.config.facebook.secret
            String callbackurl = "${serverUrl}/signIn/facebookCallback"
            String facebookTokenUrl = "https://graph.facebook.com/oauth/access_token?client_id=${facebookApiKey}&client_secret=${facebookApiSecret}&code=${authCode}&redirect_uri=${callbackurl}?cmd=add&scope=${FACEBOOK_PERMISSIONS}"
            URL url = new URL(facebookTokenUrl)
            String response = url.text
            String accessToken
            if (response.contains('access_token=')) {
                String[] resp = response.split('access_token=')
                accessToken = resp[1]
                if (accessToken.contains('&')) {
                    accessToken = accessToken.substring(0, accessToken.indexOf('&'))
                }
                String facebookUrl = "https://graph.facebook.com/me?fields=name,first_name,last_name,link,username,updated_time,address,work,relationship_status,gender,languages,email,religion,birthday,bio,location,political,education,friends,age_range,about,hometown&access_token=${accessToken}"
                URL profileDataUrl = new URL(facebookUrl)
                def jsonString = JSON.parse(profileDataUrl.text)
                FacebookConnectVO facebookConnectVO = new FacebookConnectVO(jsonString)
                facebookConnectVO.accessToken = accessToken
                User user = springSecurityService.currentUser as User
                if (!user) {
                    user = User.findByUsername(facebookConnectVO.email)
                }
                if (user) {
                    socialProfile = handleFacebookSocialProfileCreateUpdateWhenUserExists(user, facebookConnectVO)
                } else {
                    socialProfile = SocialProfile.findByEmail(facebookConnectVO.email)
                    if (socialProfile) {
                        user = socialProfile?.user
                        if (socialProfile.type == SocialProfileType.FACEBOOK) {
                            signinService.updateFacebookSocialProfile(user, facebookConnectVO, socialProfile)
                        } else {
                            signinService.createFacebookSocialProfile(user, facebookConnectVO)
                        }
                    } else {
                        user = new User(facebookConnectVO)
                        user.password = springSecurityService.encodePassword(user.password)
                        if (!user.save(flush: true)) {
                            redirect(action: 'auth', controller: 'login', params: [messageString: 'Unable to Create user.'])
                        } else {
                            UserRole.create(user, Role.findByAuthority('ROLE_USER'), true)
                            signinService.createFacebookSocialProfile(user, facebookConnectVO)
                        }
                    }
                }
                springSecurityService?.reauthenticate(user?.username)
                redirect(controller: 'home')

            } else {
                flash.message = params.error_message
                redirect(action: 'auth', controller: 'login')
            }
        } else {
            flash.message = params.error_message
            redirect(action: 'auth', controller: 'login')
        }
    }

    private SocialProfile handleFacebookSocialProfileCreateUpdateWhenUserExists(User user, FacebookConnectVO facebookDetails) {
        SocialProfile socialProfile = SocialProfile.findByUserAndType(user, SocialProfileType.FACEBOOK)
        if (socialProfile) {
            println("************************Facebook Social Profile Found ****************")
            signinService.updateFacebookSocialProfile(user, facebookDetails, socialProfile)
        } else {
            println("************************Facebook Social Profile Not Found, so creating one ****************")
            socialProfile = signinService.createFacebookSocialProfile(user, facebookDetails)
        }
        return socialProfile
    }

//  ====================  Google Connect/Signup Code   ================

    def connectGoogle = {
        String serverUrl = grailsApplication.config.grails.serverURL
        String callbackurl = "${serverUrl}/signIn/googleCallback"
        println("****************** Google Callback URL ***************** " + callbackurl)
        String googleApiKey = grailsApplication.config.google.key
        String authorizeUrl = "https://accounts.google.com/o/oauth2/auth?scope=${GOOGLE_EMAIL_SCOPE}+${GOOGLE_PROFILE_SCOPE}&state=xyz&redirect_uri=${callbackurl}&response_type=code&client_id=${googleApiKey}&access_type=offline&approval_prompt=force"
        println '******************* Google Redirect URL **************** ' + authorizeUrl
        URL urlForGooglePlus = new URL(authorizeUrl)
        redirect(url: urlForGooglePlus)
    }

    def googleCallback = {
        String code = params?.code
        SocialProfile socialProfile
        User user = springSecurityService.currentUser as User
        if (code) {
            String accessToken = signinService.postCallForGoogleAccessToken(code)
            if (accessToken) {
                GoogleConnectVO googleSignupVO = signinService.getGoogleUserProfile(accessToken)
                if (!user) {
                    user = User.findByUsername(googleSignupVO.email)
                }
                if (user) {
                    handleGoogleSocialProfileCreateAndUpdateWhenUserExists(user, googleSignupVO)
                } else {
                    socialProfile = SocialProfile.findByEmail(googleSignupVO.email)
                    if (socialProfile) {
                        user = socialProfile?.user
                        if (socialProfile?.type == SocialProfileType.GOOGLE) {
                            signinService.updateGoogleSocialProfile(user, googleSignupVO, socialProfile)
                        } else {
                            signinService.createGoogleSocialProfile(user, googleSignupVO)
                        }
                    } else {
                        user = new User(googleSignupVO)
                        user.password = springSecurityService.encodePassword(user.password)
                        if (!user.save(flush: true)) {
                            redirect(action: 'auth', controller: 'login', params: [messageString: 'Unable to Create user.'])
                        } else {
                            UserRole.create(user, Role.findByAuthority('ROLE_USER'), true)
                            signinService.createGoogleSocialProfile(user, googleSignupVO)
                        }
                    }
                }
                springSecurityService?.reauthenticate(user?.username)
                redirect(controller: 'home', action: 'index')
            }
        } else {
            redirect(action: 'auth', controller: 'login')
        }
    }

    private SocialProfile handleGoogleSocialProfileCreateAndUpdateWhenUserExists(User user, GoogleConnectVO googleSignupVO) {
        SocialProfile socialProfile = SocialProfile.findByUserAndType(user, SocialProfileType.GOOGLE)
        if (socialProfile) {
            signinService.updateGoogleSocialProfile(user, googleSignupVO, socialProfile)
            println("************************Google Social Profile Found ****************")
        } else {
            println("************************Google Social Profile Not Found, so creating one ****************")
            signinService.createGoogleSocialProfile(user, googleSignupVO)
        }
        return socialProfile
    }

    private SocialProfile handleTwitterSocialProfileCreateAndUpdateWhenUserExists(User user, twitter4j.User twitterUser) {
        SocialProfile socialProfile = SocialProfile.findByUserAndType(user, SocialProfileType.TWITTER)
        if (socialProfile) {
            signinService.updateTwitterSocialProfile(user, twitterUser, socialProfile)
            println("************************ Twitter Social Profile Found ****************")
        } else {
            println("************************ Twitter Social Profile Not Found, so creating one ****************")
            signinService.createTwitterSocialProfile(user, twitterUser)
        }
        return socialProfile
    }

    def connectTwitter = {
        String callbackUrl = "${grailsApplication.config.grails.serverURL}/signIn/twitterCallback"
        RequestToken requestToken = signinService.getRequestToken(callbackUrl)
        session[REQUEST_TOKEN] = requestToken
        String authUrl = requestToken.getAuthorizationURL()
        println '-----------authUrl-------------' + authUrl
        redirect(url: authUrl)
    }

    def twitterCallback = {
        if (params.denied) {
            redirect(action: 'create')
            return
        } else {
            String oauthToken = params.oauth_token
            String oauthVerifier = params.oauth_verifier
            println '---------oauthToken-----------' + oauthToken
            println '---------oauthVerifier-----------' + oauthVerifier
            redirect(action: 'accessToken', params: [oauthToken: oauthToken, oauthVerifier: oauthVerifier])
        }
    }

    def accessToken = {
        SocialProfile socialProfile
        User user = springSecurityService.currentUser as User
        RequestToken tokenCopy = (RequestToken) session[REQUEST_TOKEN]
        String verifier = params.oauthVerifier
        println '=========verifier================' + verifier
        AccessToken accessToken = signinService.getAccessTokenFromRequestToken(tokenCopy, verifier)
        session[TWITTER_TOKEN] = accessToken.token
        session[TWITTER_SECRET] = accessToken.tokenSecret
        println '----------accessToken.token----------' + accessToken.token
        println '----------accessToken.tokenSecret----------' + accessToken.tokenSecret
        twitter4j.User twitterUser = signinService.getTwitterUser(session[TWITTER_TOKEN].toString(), session[TWITTER_SECRET].toString())
        println '---------twitterUser.name-----------' + twitterUser.name
        String twitterEmail = "${twitterUser.name}@twitter.com"
        if (!user) {
            user = User.findByUsername(twitterEmail)
        }
        if (user) {
            handleTwitterSocialProfileCreateAndUpdateWhenUserExists(user, twitterUser)
        } else {
            socialProfile = SocialProfile.findByEmail(twitterEmail)
            if (socialProfile) {
                user = socialProfile?.user
                if (socialProfile?.type == SocialProfileType.TWITTER) {
                    signinService.updateTwitterSocialProfile(user, twitterUser, socialProfile)
                } else {
                    signinService.createTwitterSocialProfile(user, twitterUser)
                }
            } else {
                user = new User(twitterUser)
                user.password = springSecurityService.encodePassword(user.password)
                if (!user.save(flush: true)) {
                    redirect(action: 'auth', controller: 'login', params: [messageString: 'Unable to Create user.'])
                } else {
                    UserRole.create(user, Role.findByAuthority('ROLE_USER'), true)
                    signinService.createTwitterSocialProfile(user, twitterUser)
                }
            }
        }
        springSecurityService?.reauthenticate(user?.username)
        redirect(controller: 'home', action: 'index')
    }
}


