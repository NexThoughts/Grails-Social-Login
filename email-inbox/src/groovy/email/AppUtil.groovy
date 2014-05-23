package email

import com.credio.admin.News
import grails.util.GrailsUtil
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class AppUtil {

    public static String getStaticResourcesDirPath() {
        String path = '';
        if (GrailsUtil.developmentEnv) {
            path = ServletContextHolder.getServletContext().getRealPath("/")
//            path = '/home/nexthoughts/credio_document/'
        } else {
            path = '/mnt/credio_static/document/'
        }
        return path
    }

}
