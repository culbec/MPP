package Model;

import java.util.Objects;

public class Race extends Entity<Integer> {
    private Integer engineCapacity = null;
    private Integer noParticipants = null;

    private Race() {
        super();
    }

    public static class Builder {
        public Builder() {

        }

        private Race member = new Race();

        public Builder reset() {
            this.member = new Race();
            return this;
        }

        public Builder setId(Integer id) {
            this.member.setId(id);
            return this;
        }

        public Builder setNoParticipants(Integer noParticipants) {
            this.member.noParticipants = noParticipants;
            return this;
        }

        public Builder setEngineCapacity(Integer engineCapacity) {
            this.member.engineCapacity = engineCapacity;
            return this;
        }

        public Race build() {
            return member;
        }
    }

    public Integer getEngineCapacity() {
        return engineCapacity;
    }

    public Integer getNoParticipants() {
        return noParticipants;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Race race = (Race) o;
        return Objects.equals(noParticipants, race.noParticipants) && Objects.equals(engineCapacity, race.engineCapacity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(noParticipants, engineCapacity);
    }

    @Override
    public String toString() {
        return "Model.Race{" +
                "noParticipants=" + noParticipants +
                ", engineCapacity=" + engineCapacity +
                '}';
    }
}
