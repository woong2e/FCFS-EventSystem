package main.kotlin.com.woong2e.couponsystem.global.jpa

import com.github.f4b6a3.ulid.UlidCreator
import jakarta.persistence.*
import org.hibernate.proxy.HibernateProxy
import org.springframework.data.domain.Persistable
import java.util.*

@MappedSuperclass
abstract class PrimaryKeyEntity : Persistable<UUID> {
    @Id
    @Column(columnDefinition = "BINARY(16)")
    private val id: UUID = UlidCreator.getMonotonicUlid().toUuid()

    @Transient
    private var _isNew = true

    override fun getId(): UUID = id

    override fun isNew(): Boolean = _isNew

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is HibernateProxy && this::class != other::class) return false
        return id == getIdentifier(other)
    }

    private fun getIdentifier(obj: Any): UUID {
        return if (obj is HibernateProxy) {
            obj.hibernateLazyInitializer.identifier as UUID
        } else {
            (obj as PrimaryKeyEntity).id
        }
    }

    override fun hashCode() = Objects.hashCode(id)

    @PostPersist
    @PostLoad
    protected fun load() {
        _isNew = false
    }
}
