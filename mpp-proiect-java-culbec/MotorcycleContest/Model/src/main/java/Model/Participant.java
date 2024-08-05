package Model;

import java.util.Objects;
import java.util.UUID;

public class Participant extends Person<UUID> {
    private String team = null;
    private Integer engineCapacity = null;

    private Participant() {
        super();
    }

    public String getTeam() {
        return team;
    }

    public Integer getEngineCapacity() {
        return engineCapacity;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public void setEngineCapacity(Integer engineCapacity) {
        this.engineCapacity = engineCapacity;
    }

    public static class Builder {
        public Builder() {
        }

        private Participant member = new Participant();

        public Builder reset() {
            this.member = new Participant();
            return this;
        }

        public Builder setId(UUID id) {
            this.member.setId(id);
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.member.setFirstName(firstName);
            return this;
        }

        public Builder setLastName(String lastName) {
            this.member.setLastName(lastName);
            return this;
        }

        public Builder setTeam(String team) {
            this.member.team = team;
            return this;
        }

        public Builder setEngineCapacity(Integer engineCapacity) {
            this.member.engineCapacity = engineCapacity;
            return this;
        }

        public Participant build() {
            return member;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Participant that = (Participant) o;
        return Objects.equals(team, that.team) && Objects.equals(engineCapacity, that.engineCapacity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), team, engineCapacity);
    }

    @Override
    public String toString() {
        return "Model.Participant{" +
                "team='" + team + '\'' +
                ", engineCapacity=" + engineCapacity +
                "} " + super.toString();
    }
}
