package ro.mpp2024.lab2.model;

public interface Identifiable<Tid> {
    Tid getID();
    void setID(Tid id);
}