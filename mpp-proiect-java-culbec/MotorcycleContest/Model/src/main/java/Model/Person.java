package Model;

import java.util.Objects;

public class Person<ID> extends Entity<ID> {
    private String firstName = null;
    private String lastName = null;

    Person() {
        super();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public static class Builder<ID> {
        public Builder() {
        }

        private Person<ID> member = new Person<>();

        public Builder<ID> reset() {
            this.member = new Person<>();
            return this;
        }

        public Builder<ID> setId(ID id) {
            this.member.setId(id);
            return this;
        }

        public Builder<ID> setFirstName(String firstName) {
            this.member.firstName = firstName;
            return this;
        }

        public Builder<ID> setLastName(String lastName) {
            this.member.lastName = lastName;
            return this;
        }

        public Person<ID> build() {
            return member;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Person person = (Person) o;
        return Objects.equals(firstName, person.firstName) && Objects.equals(lastName, person.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), firstName, lastName);
    }

    @Override
    public String toString() {
        return "Model.Person{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                "} " + super.toString();
    }
}
