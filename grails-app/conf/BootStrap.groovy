import org.social.login.Role
import org.social.login.User
import org.social.login.UserRole

class BootStrap {

    def init = { servletContext ->

        def adminRole = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN').save(failOnError: true)
        def userRole = Role.findByAuthority('ROLE_USER') ?: new Role(authority: 'ROLE_USER').save(failOnError: true)

        if (!User.count()) {
            def adminUser = User.findByUsername('admin@email.com') ?: new User(
                    name: 'Admin User',
                    username: 'admin@email.com',
                    password: '1234',
                    enabled: true).save(failOnError: true)

            if (!adminUser.authorities.contains(adminRole)) {
                UserRole.create(adminUser, adminRole)
            }

            (1..15).each {
                String username = "user${it}@email.com"
                def user = User.findByUsername(username) ?: new User(
                        name: 'User '+it,
                        username: username,
                        password: '1234',
                        enabled: true).save(failOnError: true)

                if (!user.authorities.contains(userRole)) {
                    UserRole.create(user, userRole)
                }
            }


        }
    }
    def destroy = {


    }
}
