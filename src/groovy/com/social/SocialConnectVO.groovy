package com.social

public abstract class SocialConnectVO {

    String firstName
    String lastName
    String defaultPassword = UUID.randomUUID().toString()
    String email
    String profileImgUrl
    String username
    String link
    String accessToken
    String profileId

}
