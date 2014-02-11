package com.social

import grails.converters.JSON
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.social.SocialProfile
import org.social.SocialProfileEducation
import org.social.SocialProfileWorkExperience
import org.social.login.User
import twitter4j.Twitter
import twitter4j.TwitterFactory
import twitter4j.auth.AccessToken
import twitter4j.auth.RequestToken

class SigninService {

    def grailsApplication

//  ****************************  HANDLING LinkedIn Data *********************************

    public postCallForLinkedInAccessToken(String authCode) {
        String serverUrl = grailsApplication.config.grails.serverURL
        String callbackurl = "${serverUrl}/signIn/linkedInCallback"
        String linkedInTokenUrl = "https://www.linkedin.com/uas/oauth2/accessToken?"
        StringBuilder sb = new StringBuilder("grant_type=authorization_code&code=");
        sb.append(URLEncoder.encode(authCode, "UTF-8"));
        sb.append("&redirect_uri=");
        sb.append(URLEncoder.encode(callbackurl, "UTF-8"));
        sb.append("&client_id=");
        sb.append(URLEncoder.encode("${grailsApplication.config.linkedin.key}", "UTF-8"));
        sb.append("&client_secret=");
        sb.append(URLEncoder.encode("${grailsApplication.config.linkedin.secret}", "UTF-8"));

        URL url = new URL(linkedInTokenUrl)
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        String accessToken = ''
        try {
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", "" + sb.toString().length());
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
            outputStreamWriter.write(sb.toString());
            outputStreamWriter.flush();
            String resultData = connection?.content?.text
            def responseJson = JSON.parse(resultData)
            accessToken = responseJson.access_token
            println("Response code ${connection.responseCode} , Message : ${connection.responseMessage}")
            if (connection.responseCode != 200) {
                println("Unable to Make Post Call")
            }
        } finally {
            connection?.disconnect()
        }
        println("------------------------access token---------------" + accessToken)
        return accessToken
    }


    public LinkedInConnectVO getLinkedInUserProfile(String accessToken) {
        String urlString = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,email-address,picture-url,public-profile-url,educations,positions,specialties,headline,summary,industry,location:(name),date-of-birth,languages)?oauth2_access_token=${accessToken}&format=json";
        URL url = new URL(urlString)
        String jsonResponse = url.text
        def responseJson = JSON.parse(jsonResponse)

        println '--------responseJson-----------' + responseJson
        LinkedInConnectVO linkedInSignupVO = new LinkedInConnectVO(responseJson)
        linkedInSignupVO.accessToken = accessToken
        return linkedInSignupVO
    }

    public SocialProfile createLinkedInSocialProfile(User user, LinkedInConnectVO linkedInConnectVO) {
        SocialProfile socialProfile = new SocialProfile(user, linkedInConnectVO)
        user.addToSocialProfileList(socialProfile)
        if (socialProfile.validate()) {
            socialProfile.save(flush: true)

            linkedInConnectVO?.userEducations?.each { LinkedInEducationVO educationVO ->
                SocialProfileEducation education = new SocialProfileEducation(educationVO)
                education.socialProfile = socialProfile
                education.save(flush: true)
                socialProfile.addToEducations(education)
            }
            linkedInConnectVO?.userWorkExperiences?.each { LinkedInWorkExperienceVO experienceVO ->
                SocialProfileWorkExperience workExperience = new SocialProfileWorkExperience(experienceVO)
                workExperience.socialProfile = socialProfile
                workExperience.save(flush: true)
                socialProfile.addToExperiences(workExperience)
            }
        } else {
            socialProfile.errors.allErrors.each {
                println("-----------linked in---errror-------it---------" + it)
            }
        }
        return socialProfile
    }

    void updateLinkedInSocialProfile(LinkedInConnectVO linkedInSignupVO, SocialProfile socialProfile) {
        socialProfile.accessSecret = linkedInSignupVO?.accessSecret
        socialProfile.accessToken = linkedInSignupVO?.accessToken
        socialProfile.profileId = linkedInSignupVO.profileId
        socialProfile.email = linkedInSignupVO.email
        socialProfile.fullName = linkedInSignupVO.firstName + " " + linkedInSignupVO.lastName
        socialProfile.location = linkedInSignupVO.location
        socialProfile.industry = linkedInSignupVO.industry
        socialProfile.summary = linkedInSignupVO.summary
        socialProfile.gender = linkedInSignupVO.gender
        socialProfile.birthday = linkedInSignupVO.dateOfBirth
        socialProfile.languages = linkedInSignupVO?.languages?.join(',')
        socialProfile.profileUrl = linkedInSignupVO?.publicProfileUrl
        socialProfile.followers = linkedInSignupVO?.followers
        socialProfile.save(flush: true)

        linkedInSignupVO.userEducations.each { LinkedInEducationVO educationVO ->
            SocialProfileEducation education = SocialProfileEducation.findBySocialProfileAndEducationId(socialProfile, educationVO?.educationId)
            if (!education) {
                SocialProfileEducation education1 = new SocialProfileEducation(educationVO)
                education1.socialProfile = socialProfile
                socialProfile.addToEducations(education1)
                if (!education1.validate()) {
                    education1.errors.allErrors.each {
                        println("-----------education error-------" + it)
                    }
                } else {
                    education1.save(flush: true, failOnError: true)
                }
            }
        }

        linkedInSignupVO.userWorkExperiences.each { LinkedInWorkExperienceVO workExperienceVO ->
            SocialProfileWorkExperience workExperience = SocialProfileWorkExperience.findBySocialProfileAndPositionId(socialProfile, workExperienceVO.positionId)
            if (!workExperience) {
                SocialProfileWorkExperience workExperience1 = new SocialProfileWorkExperience(workExperienceVO)
                workExperience1.socialProfile = socialProfile
                socialProfile.addToExperiences(workExperience1)
                if (!workExperience1.validate()) {
                    workExperience1.errors.allErrors.each {
                        println("----------------------Error work---------------" + it)
                    }
                } else {
                    workExperience1.save(flush: true)
                }
            }
        }
    }

    //  ****************************  HANDLING Facebook Data *********************************


    void updateGoogleSocialProfile(User user, GoogleConnectVO googleConnectVO, SocialProfile socialProfile) {
        socialProfile.user = user
        socialProfile.fullName = googleConnectVO?.fullName
        socialProfile.username = googleConnectVO?.username
        socialProfile.email = googleConnectVO?.email
        socialProfile.profileId = googleConnectVO?.profileId
        socialProfile.gender = googleConnectVO?.gender
        socialProfile.profileUrl = googleConnectVO?.googleURL
        socialProfile.birthday = googleConnectVO?.dateOfBirth
        socialProfile.type = SocialProfileType?.GOOGLE
        if (socialProfile.validate()) {
            socialProfile.save(flush: true, failOnError: true)
        }
    }


    SocialProfile createFacebookSocialProfile(User user, FacebookConnectVO facebookConnectVO) {
        SocialProfile socialProfile = new SocialProfile(user, facebookConnectVO)
        user.addToSocialProfileList(socialProfile)
        if (socialProfile.validate()) {
            socialProfile.save(flush: true)
//            facebookConnectVO.workExperiences.each { FacebookUserWorkExperienceVO workExperienceVO ->
//                SocialProfileWorkExperience workExperience = new SocialProfileWorkExperience(workExperienceVO)
//                workExperience.socialProfile = socialProfile
//                workExperience.save(flush: true)
//                socialProfile.addToExperiences(workExperience)
//            }
//            facebookConnectVO.userEducations.each { FacebookUserEducationVO educationVO ->
//                SocialProfileEducation education = new SocialProfileEducation(educationVO)
//                education.socialProfile = socialProfile
//                education.save(flush: true)
//                socialProfile.addToEducations(education)
//            }
            println("********************** Facebook Social Profile Created Successfully **************" + socialProfile?.id)
        } else {
            socialProfile.errors.allErrors.each {
                println("----------- Facebook Errors ----------- " + it)
            }
        }
        return socialProfile
    }

    def updateFacebookSocialProfile(User user, FacebookConnectVO facebookSignupVO, SocialProfile socialProfile) {
        print("====================== Updating Facebook Social Profile ===================")
        print(facebookSignupVO)
        socialProfile.accessToken = facebookSignupVO?.accessToken
        socialProfile.email = facebookSignupVO?.email
        socialProfile.profileId = facebookSignupVO.profileId
        socialProfile.accessToken = facebookSignupVO.accessToken
        socialProfile.fullName = facebookSignupVO?.firstName + " " + facebookSignupVO?.lastName
        socialProfile.username = facebookSignupVO.username
        socialProfile.hometown = facebookSignupVO.hometown
        socialProfile.gender = facebookSignupVO.gender
        socialProfile.profileUrl = facebookSignupVO.link
        socialProfile.birthday = facebookSignupVO.dateOfBirth
        socialProfile.location = facebookSignupVO.location
        socialProfile.languages = facebookSignupVO?.languages?.join(', ')
        socialProfile.followers = facebookSignupVO?.followers

        facebookSignupVO.userEducations.each { FacebookUserEducationVO educationVO ->
            SocialProfileEducation education = SocialProfileEducation.findBySocialProfileAndEducationId(socialProfile, educationVO?.educationId)
            if (!education) {
                SocialProfileEducation education1 = new SocialProfileEducation(educationVO)
                education1.socialProfile = socialProfile
                socialProfile.addToEducations(education1)
                if (!education1.validate()) {
                    education1.errors.allErrors.each {
                        println("-----------education error-------" + it)
                    }
                } else {
                    education1.save(flush: true, failOnError: true)
                }
            }
        }

        facebookSignupVO.workExperiences.each { FacebookUserWorkExperienceVO workExperienceVO ->
            SocialProfileWorkExperience workExperience = SocialProfileWorkExperience.findBySocialProfileAndPositionId(socialProfile, workExperienceVO.positionId)
            if (!workExperience) {
                SocialProfileWorkExperience workExperience1 = new SocialProfileWorkExperience(workExperienceVO)
                workExperience1.socialProfile = socialProfile
                socialProfile.addToExperiences(workExperience1)
                if (!workExperience1.validate()) {
                    workExperience1.errors.allErrors.each {
                        println("----------------------Error work---------------" + it)
                    }
                } else {
                    workExperience1.save(flush: true)
                }
            }
        }
    }

    public String postCallForGoogleAccessToken(String code) {
        println("----------------getting access token-----------------")
        String googleApiKey = grailsApplication.config.google.key
        String googleApiSecret = grailsApplication.config.google.secret

        String serverUrl = grailsApplication.config.grails.serverURL
        String callbackurl = "${serverUrl}/signIn/googleCallback"
        StringBuilder sb = new StringBuilder("code=");
        sb.append(URLEncoder.encode(code, "UTF-8"));
        sb.append("&client_id=");
        sb.append(URLEncoder.encode("${googleApiKey}", "UTF-8"));
        sb.append("&client_secret=");
        sb.append(URLEncoder.encode("${googleApiSecret}", "UTF-8"));
        sb.append("&redirect_uri=");
        sb.append(URLEncoder.encode(callbackurl, "UTF-8"));
        sb.append("&grant_type=");
        sb.append(URLEncoder.encode('authorization_code', "UTF-8"));
        URL url = new URL('https://accounts.google.com/o/oauth2/token');
        HttpURLConnection connection = (HttpURLConnection) url.openConnection()
        String accessToken = ''
        try {
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", "" + sb.toString().length());
            connection.setRequestProperty("Host", "accounts.google.com");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
            outputStreamWriter.write(sb.toString());
            outputStreamWriter.flush();
            println("Response code ${connection.responseCode} , Message : ${connection.responseMessage}")
            String resultData = connection.content.text
            def responseJson = JSON.parse(resultData)
            accessToken = responseJson?.access_token
        }
        catch (Exception e) {
            println("Failed to get access token from google " + e)
        }
        return accessToken
    }

    public GoogleConnectVO getGoogleUserProfile(String accessToken) {
        String urlString = "https://www.googleapis.com/oauth2/v1/userinfo??output=json&access_token=${accessToken}"
        URL url = new URL(urlString)
        String responseText = url.text
        println'-----------responseText--------------'+responseText
        def jsonData = JSON.parse(responseText)
        GoogleConnectVO googleSignupVO = new GoogleConnectVO(jsonData)
        println'----------accessToken-----------'+accessToken
        googleSignupVO.accessToken = accessToken
        return googleSignupVO
    }

    public createGoogleSocialProfile(User user, GoogleConnectVO googleSignupVO) {
        SocialProfile socialProfile = new SocialProfile(user, googleSignupVO)
        user.addToSocialProfileList(socialProfile)
        if (!socialProfile.save(flush: true)) {
            socialProfile.errors.allErrors.each {
                println('--------------SocialProfile Google------------------------' + it)
            }
        }
    }

//  ****************************  Twitter Signup Code ****************************

    public RequestToken getRequestToken(String callbackUrl) {
        TwitterFactory factory = new TwitterFactory()
        Twitter twitter = factory.getInstance()
        twitter.setOAuthConsumer(twitterKey, twitterSecret)
        RequestToken requestToken = twitter.getOAuthRequestToken()
        return requestToken
    }

    public AccessToken getAccessTokenFromRequestToken(RequestToken requestToken, String verifier) {
        TwitterFactory factory = new TwitterFactory()
        Twitter twitter = factory.getInstance()
        twitter.setOAuthConsumer(twitterKey, twitterSecret)
        AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier)
        return accessToken
    }

    public twitter4j.User getTwitterUser(String oAuthToken, String oAuthTokenSecret) {
        String twitterURL
        TwitterFactory factory = new TwitterFactory()
        Twitter twitter = factory.getInstance()
        twitter.setOAuthConsumer(twitterKey, twitterSecret)
        AccessToken accessToken = new AccessToken(oAuthToken, oAuthTokenSecret)
        twitter.setOAuthAccessToken(accessToken)
        int userId = twitter.getId()
        twitter4j.User user = twitter.showUser(userId);
        return user
    }


    void updateTwitterSocialProfile(User user, twitter4j.User twitterUser, SocialProfile socialProfile) {
        socialProfile.user = user
        socialProfile.fullName = twitterUser?.name
        socialProfile.username = twitterUser?.screenName
        socialProfile.email = "${twitterUser?.name}@twitter.com"
        socialProfile.profileId = twitterUser?.id
        socialProfile.profileUrl = twitterUser?.profileImageURL
        socialProfile.type = SocialProfileType?.TWITTER
        if (socialProfile.validate()) {
            socialProfile.save(flush: true, failOnError: true)
        }
    }

    public createTwitterSocialProfile(User user, twitter4j.User twitterUser) {
        SocialProfile socialProfile = new SocialProfile(user, twitterUser)
        user.addToSocialProfileList(socialProfile)
        if (!socialProfile.save(flush: true)) {
            socialProfile.errors.allErrors.each {
                println('--------------SocialProfile Google------------------------' + it)
            }
        }
    }

    String getTwitterKey() {
        return grailsApplication.config.twitter.key
    }

    String getTwitterSecret() {
        return grailsApplication.config.twitter.secret
    }
}
