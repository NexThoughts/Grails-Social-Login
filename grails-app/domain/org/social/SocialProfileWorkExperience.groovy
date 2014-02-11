package org.social

import com.social.FacebookUserWorkExperienceVO
import com.social.LinkedInWorkExperienceVO

class SocialProfileWorkExperience {

    String positionId
    String title
    String company
    String summary
    Date startDate
    Date endDate
    String startDateString
    String endDateString

    static belongsTo = [socialProfile: SocialProfile]

    static constraints = {
        positionId(nullable: true)
        title(nullable: true)
        summary(nullable: true, maxsize: 3000, type: 'longtext')
        startDate(nullable: true)
        endDate(nullable: true)
        company(nullable: true)
        startDateString(nullable: true)
        endDateString(nullable: true)
    }

    static mapping = {
        summary type: "text"
    }


    SocialProfileWorkExperience() {

    }

    SocialProfileWorkExperience(def positionData) {
        this.title = positionData.title
        this.summary = positionData.summary
        this.company = positionData?.company?.name
    }


    SocialProfileWorkExperience(FacebookUserWorkExperienceVO workExperienceVO) {
        this.positionId = workExperienceVO.positionId
        this.title = workExperienceVO.position
        this.summary = workExperienceVO.description
        this.company = workExperienceVO?.employer
    }

    SocialProfileWorkExperience(LinkedInWorkExperienceVO workExperienceVO) {
        this.positionId = workExperienceVO?.positionId
        this.company = workExperienceVO?.company
        this.title = workExperienceVO?.title
        this.summary = workExperienceVO?.summary
        this.startDateString = workExperienceVO?.startDate
        this.endDateString = workExperienceVO?.endDate
    }
}
