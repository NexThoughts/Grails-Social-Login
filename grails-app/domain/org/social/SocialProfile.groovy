package org.social

import com.social.FacebookConnectVO
import com.social.GoogleConnectVO
import com.social.LinkedInConnectVO
import com.social.SocialProfileType
import org.social.login.User

class SocialProfile {

    String profileId
    String fullName
    String username
    SocialProfileType type

    String accessToken
    String accessSecret
    String profileUrl
    String email
    String location
    String industry
    String summary
    String specialities
    String gender
    String hometown
    String birthday
    String languages
    Long followers
    Long followings
    List<SocialProfileWorkExperience> experiences = []
    List<SocialProfileEducation> educations = []

    static belongsTo = [user: User]

    static hasMany = [experiences: SocialProfileWorkExperience, educations: SocialProfileEducation]

    static mapping = {
        summary type: "text"
    }

    static constraints = {
        fullName(blank: true, nullable: true)
        username(blank: true, nullable: true)
        type(nullable: false)
        accessToken(blank: false, nullable: false)
        accessSecret(blank: true, nullable: true)
        profileId(blank: false, nullable: false)
        location(blank: true, nullable: true)
        industry(blank: true, nullable: true)
        summary(blank: true, nullable: true, maxSize: 2000, type: 'longtext')
        specialities(blank: true, nullable: true)
        gender(blank: true, nullable: true)
        hometown(blank: true, nullable: true)
        birthday(blank: true, nullable: true)
        profileUrl(blank: true, nullable: true)
        languages(blank: true, nullable: true)
        email(nullable: false, email: true)
        followers(nullable: true)
        followings(nullable: true)
    }

    SocialProfile(User user, FacebookConnectVO facebookConnectVO) {
        this.accessToken = facebookConnectVO?.accessToken
        this.fullName = facebookConnectVO?.firstName + " " + facebookConnectVO?.lastName
        this.profileId = facebookConnectVO?.profileId
        this.username = facebookConnectVO?.username
        this.type = SocialProfileType.FACEBOOK
        this.profileId = facebookConnectVO?.profileId
        this.location = facebookConnectVO?.location
        this.gender = facebookConnectVO?.gender
        this.hometown = facebookConnectVO?.hometown
        this.birthday = facebookConnectVO?.dateOfBirth
        this.languages = facebookConnectVO?.languages?.join(",")
        this.email = facebookConnectVO?.email
        this.user = user
    }

    SocialProfile(User user, LinkedInConnectVO linkedInConnectVO) {
        this.fullName = linkedInConnectVO?.firstName + " " + linkedInConnectVO?.lastName
        this.profileId = linkedInConnectVO?.profileId
        this.username = linkedInConnectVO?.username
        this.type = SocialProfileType.LINKED_IN
        this.accessToken = linkedInConnectVO?.accessToken
        this.accessSecret = linkedInConnectVO?.accessSecret
        this.profileUrl = linkedInConnectVO?.publicProfileUrl
        this.email = linkedInConnectVO?.email
        this.location = linkedInConnectVO?.location
        this.industry = linkedInConnectVO?.industry
        this.summary = linkedInConnectVO?.summary
        this.gender = linkedInConnectVO?.gender
        this.birthday = linkedInConnectVO.dateOfBirth
        this.languages = linkedInConnectVO?.languages?.join(",")
        this.followers = linkedInConnectVO?.followers
        this.followings = linkedInConnectVO?.followings
        this.user = user
    }

    SocialProfile(User user, GoogleConnectVO googleConnectVO ) {
        this.user = user
        this.fullName = googleConnectVO?.fullName
        this.username = googleConnectVO?.username
        this.email = googleConnectVO?.email
        this.profileId = googleConnectVO?.profileId
        this.gender = googleConnectVO?.gender
        this.profileUrl = googleConnectVO?.googleURL
        this.accessToken = googleConnectVO?.accessToken
        this.birthday = googleConnectVO?.dateOfBirth
        this.type = SocialProfileType?.GOOGLE
    }

    SocialProfile(User user, twitter4j.User twitterUser ) {
        this.user = user
        this.fullName = twitterUser?.name
        this.username = twitterUser?.screenName
        this.email = "${twitterUser?.name}@twitter.com"
        this.profileId = twitterUser?.id
        this.profileUrl = twitterUser?.profileImageURL
        this.type = SocialProfileType?.TWITTER
    }
}
