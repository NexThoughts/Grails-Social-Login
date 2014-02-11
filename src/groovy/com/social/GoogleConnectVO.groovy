package com.social

class GoogleConnectVO extends SocialConnectVO {

    String location
    String lang
    String about
    String fullName
    String gender
    String googleURL
    String address
    String dateOfBirth
    String phoneNumber

    GoogleConnectVO(def googleData) {
        println("---------------------------------google---------data0---------------" + googleData)
        this.firstName = googleData.given_name
        this.lastName = googleData.family_name
        this.fullName = googleData.name
        this.username = googleData.id
        this.profileImgUrl = googleData.picture
        this.profileId = googleData.id
        this.gender = googleData.gender
        this.googleURL = googleData.link
        this.email = googleData.email
        this.address = googleData?.address
        this.dateOfBirth = googleData?.dateOfBirth
        this.phoneNumber = googleData?.phoneNumber
    }
}
