package generated.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

import javax.persistence.*;

@Entity
@Table(name = "revinfo")
@RevisionEntity
@AttributeOverrides({
        @AttributeOverride(name = "timestamp", column = @Column(name = "revtstmp")),
        @AttributeOverride(name = "id", column = @Column(name = "rev"))
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AuditRevisionEntity extends DefaultRevisionEntity {

}