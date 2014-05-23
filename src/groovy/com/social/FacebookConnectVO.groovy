package com.social

class FacebookConnectVO extends SocialConnectVO{

    String about
    String location
    String gender
    String address
    String dateOfBirth
    String hometown
    String phoneNumber

    List<String> languages = []

    Long followers
    Long followings

    List<FacebookUserWorkExperienceVO> workExperiences = []
    List<FacebookUserEducationVO> userEducations = []

    FacebookConnectVO(def facebookData) {
        println("----------- Facebook Data --------------------- " + facebookData)
        this.firstName = facebookData?.first_name
        this.lastName = facebookData?.last_name
        this.username = facebookData?.username
        this.link = facebookData?.link
        this.location = facebookData?.location?.name
        this.about = facebookData?.description
        this.profileImgUrl = facebookData?.profile_image_url
        this.profileId = facebookData?.id
        this.email = facebookData?.email
        this.hometown = facebookData?.hometown?.name
        this.gender = facebookData?.gender
        this.address = facebookData?.address
        this.phoneNumber = facebookData?.phoneNumber
        this.followers = facebookData?.friends?.data?.size()
        this.location = facebookData?.location?.name

        facebookData?.work?.each {
            FacebookUserWorkExperienceVO workExperience = new FacebookUserWorkExperienceVO(it)
            this.workExperiences.add(workExperience)
        }

        facebookData?.education?.each {
            FacebookUserEducationVO userEducation = new FacebookUserEducationVO(it)
            this.userEducations.add(userEducation)
        }
        facebookData?.languages?.each {
            String language = it.name
            this.languages.add(language)
        }
    }
}

class FacebookUserWorkExperienceVO {
    String positionId
    String position
    String description
    String employer
    String startDate
    String endDate

    FacebookUserWorkExperienceVO() {

    }

    FacebookUserWorkExperienceVO(def workData) {
        this.positionId = workData?.employer?.id
        this.position = workData?.position?.name
        this.description = workData?.description
        this.employer = workData?.employer?.name
        this.startDate = workData?.start_date
        this.endDate = workData?.end_date
    }
}

class FacebookUserEducationVO {
    String educationId
    String school
    String year
    String type
    String degree

    FacebookUserEducationVO() {

    }

    FacebookUserEducationVO(def educationData) {
        this.educationId = educationData?.school?.id
        this.school = educationData?.school?.name
        this.year = educationData?.year?.name
        this.type = educationData?.type
        this.degree = educationData?.degree?.name
    }
}

