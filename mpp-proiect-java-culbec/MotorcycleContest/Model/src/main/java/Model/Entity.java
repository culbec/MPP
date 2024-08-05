package Model;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

import java.util.Objects;

@MappedSuperclass
public class Entity<ID> {
    @Id
    private ID id = null;

    Entity() {
    }

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public static class Builder<ID> {
        public Builder() {
        }

        private Entity<ID> member = new Entity<>();

        public Builder<ID> reset() {
            this.member = new Entity<>();
            return this;
        }
        public Builder<ID> setId(ID id) {
            member.id = id;
            return this;
        }

        public Entity<ID> build() {
            return member;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Model.Entity{" +
                "id=" + id +
                '}';
    }
}
