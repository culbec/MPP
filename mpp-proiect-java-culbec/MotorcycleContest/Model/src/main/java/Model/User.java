package Model;

import java.util.Objects;

public class User extends Person<Integer> {
    private String username = null;

    private User() {
        super();
    }

    public static class Builder {
        public Builder() {

        }

        private User member = new User();

        public Builder reset() {
            this.member = new User();
            return this;
        }

        public Builder setId(Integer id) {
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

        public Builder setUsername(String username) {
            this.member.username = username;
            return this;
        }

        public User build() {
            return member;
        }
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        User user = (User) o;
        return Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username);
    }

    @Override
    public String toString() {
        return "Model.User{" +
                "username='" + username + '\'' +
                "} " + super.toString();
    }
}
