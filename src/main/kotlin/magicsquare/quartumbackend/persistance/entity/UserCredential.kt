package magicsquare.quartumbackend.persistance.entity

import javax.persistence.*

/**
 * User credential - данные пользователя для входа
 *
 * @property id
 * @property user
 * @property email
 * @property password
 * @property username
 */
@Entity
@Table(name = "user_credentials")
open class UserCredential(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    open var id: Long? = null,

//    @MapsId
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
    @OneToOne(mappedBy = "userCredentials", cascade = [CascadeType.ALL])
    open var user: User? = null,

    @Column(name = "email", nullable = false, length = 60)
    open var email: String? = null,

    @Column(name = "password", nullable = false, length = 60)
    open var password: CharArray? = null,

    @Column(name = "username", length = 40)
    open var username: String? = null,
) {

    constructor(username: String, email: String, password: String) : this(){
        this.username = username
        this.email = email
        this.password = password.toCharArray()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserCredential

        if (id != other.id) return false
        if (user != other.user) return false
        if (email != other.email) return false
        if (!password.contentEquals(other.password)) return false
        if (username != other.username) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (password?.hashCode() ?: 0)
        result = 31 * result + (username?.hashCode() ?: 0)
        return result
    }


}
