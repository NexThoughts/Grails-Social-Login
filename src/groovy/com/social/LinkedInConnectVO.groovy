package com.social

import java.text.SimpleDateFormat

class LinkedInConnectVO extends SocialConnectVO {

    String location
    String about
    String address
    String dateOfBirth
    String phoneNumber
    String gender
    String accessSecret
    String industry
    String summary
    String publicProfileUrl
    Long followers
    Long followings
    List<LinkedInEducationVO> userEducations = []
    List<LinkedInWorkExperienceVO> userWorkExperiences = []
    List<String> languages = []

    LinkedInConnectVO(def linkedInData) {
        this.firstName = linkedInData?.firstName
        this.lastName = linkedInData?.lastName
        this.about = linkedInData?.summary
        this.profileImgUrl = linkedInData?.pictureUrl
        this.profileId = linkedInData?.id
        this.email = linkedInData?.emailAddress
        this.address = linkedInData?.mainAddress
        this.phoneNumber = linkedInData?.dateOfBirth
        this.phoneNumber = linkedInData?.phoneNumbers?.values?.phoneNumber?.get(0)
        this.location = linkedInData?.location?.country?.code?.toUpperCase()
        this.gender = linkedInData?.gender
        this.industry = linkedInData?.industry
        this.summary = linkedInData?.summary
        this.publicProfileUrl = linkedInData?.publicProfileUrl
        this.followers = linkedInData?.connections?._total

//        if (linkedInData?.dateOfBirth?.day && linkedInData?.dateOfBirth?.month && linkedInData?.dateOfBirth?.year) {
//            String dateStr = linkedInData.dateOfBirth.month + "/" + linkedInData.dateOfBirth.day + "/" + linkedInData.dateOfBirth.year
//            this.dateOfBirth = new LocalDate(new Date(dateStr).time)
//        }
        linkedInData?.languages?.values?.each {
            String language = it.language.name
            this.languages.add(language)
        }

        linkedInData?.educations?.values?.each {
            LinkedInEducationVO userEducation = new LinkedInEducationVO(it)
            this.userEducations.add(userEducation)
        }


        linkedInData?.positions?.values?.each {
            LinkedInWorkExperienceVO userWorkExperience = new LinkedInWorkExperienceVO(it)
            this.userWorkExperiences.add(userWorkExperience)
        }
    }

}


class LinkedInEducationVO {

    String educationId
    String schoolName

    String startDate
    String endDate
    String fieldOfStudy
    String degree
    String activities
    String notes

    LinkedInEducationVO() {

    }

    LinkedInEducationVO(def educationData) {
        this.educationId = educationData?.id
        this.schoolName = educationData?.schoolName
        this.startDate = educationData?.startDate?.year
        this.endDate = educationData?.endDate?.year
        this.fieldOfStudy = educationData?.fieldOfStudy
        this.degree = educationData?.degree
        this.activities = educationData?.activities
        this.notes = educationData?.notes
    }
}

class LinkedInWorkExperienceVO {
    String positionId
    String title
    String summary
    String startDate
    String endDate
    String company
    String isCurrent

    LinkedInWorkExperienceVO() {

    }

    LinkedInWorkExperienceVO(def workData) {
        this.positionId = workData?.id
        this.title = workData?.title
        this.summary = workData?.summary
        this.startDate = workData?.endDate
        this.company = workData?.company?.name
        this.isCurrent = workData?.isCurrent
        this.startDate = workData?.startDate?.month + "/" + workData?.startDate?.year
        this.endDate = workData?.endDate?.month + "/" + workData?.endDate?.year
    }
}







