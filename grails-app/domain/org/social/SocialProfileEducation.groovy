package org.social

import com.social.FacebookUserEducationVO
import com.social.LinkedInEducationVO

class SocialProfileEducation {

    String educationId
    String schoolName
    String degree
    String fieldOfStudy
    String activities
    Date startDate
    Date endDate
    String startDateString
    String endDateString
    String year

    static belongsTo = [socialProfile: SocialProfile]

    static constraints = {
        educationId(nullable: true)
        schoolName(nullable: true)
        degree(nullable: true)
        fieldOfStudy(nullable: true)
        activities(nullable: true)
        startDate(nullable: true)
        endDate(nullable: true)
        year(nullable: true)
        startDateString(nullable: true)
        endDateString(nullable: true)
    }

    static mapping = {

    }

    SocialProfileEducation() {

    }

    SocialProfileEducation(def educationData) {
        this.educationId = educationData.id
        this.schoolName = educationData.schoolName
        this.degree = educationData.degree
        this.fieldOfStudy = educationData.fieldOfStudy
        this.activities = educationData.activities
    }

    SocialProfileEducation(LinkedInEducationVO userEducation) {
        this.educationId = userEducation?.educationId
        this.schoolName = userEducation?.schoolName
        this.degree = userEducation?.degree
        this.fieldOfStudy = userEducation?.fieldOfStudy
        this.activities = userEducation?.activities
        this.startDateString = userEducation?.startDate
        this.endDateString = userEducation?.endDate
    }

    SocialProfileEducation(FacebookUserEducationVO userEducation) {
        this.educationId = userEducation?.educationId
        this.schoolName = userEducation.school
        this.degree = userEducation.degree
        this.fieldOfStudy = userEducation.type
        this.year = userEducation.year
    }
}
