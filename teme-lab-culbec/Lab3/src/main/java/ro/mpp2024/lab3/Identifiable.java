package ro.mpp2024.lab3;

public interface Identifiable<ID> {
    void setId(ID id);
    ID getId();
}
