package Model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.Formula;

import java.util.Objects;

@Entity(name = "RaceORM")
@Table(name = "races")
@AttributeOverride(name = "id", column = @Column(name = "rid"))
public class RaceORM extends Model.EntityORM<Integer> {
    @Column(name = "engine_capacity")
    private Integer engineCapacity = null;

    @Formula("(SELECT COUNT(p.pid) FROM participants p WHERE p.engine_capacity = engine_capacity)")
    private int noParticipants;

    public RaceORM() {
        super();
    }

    public RaceORM(Integer engineCapacity) {
        super();
        this.engineCapacity = engineCapacity;
    }

    // Getters and setters

    public Integer getEngineCapacity() {
        return engineCapacity;
    }

    public void setEngineCapacity(Integer engineCapacity) {
        this.engineCapacity = engineCapacity;
    }

    public Integer getNoParticipants() {
        return noParticipants;
    }

    public void setNoParticipants(Integer noParticipants) {
        this.noParticipants = noParticipants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RaceORM raceORM = (RaceORM) o;
        return Objects.equals(id, raceORM.id) &&
                Objects.equals(noParticipants, raceORM.noParticipants) &&
                Objects.equals(engineCapacity, raceORM.engineCapacity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, noParticipants, engineCapacity);
    }

    @Override
    public String toString() {
        return "RaceORM{" +
                "id=" + id +
                ", noParticipants=" + noParticipants +
                ", engineCapacity=" + engineCapacity +
                '}';
    }
}
