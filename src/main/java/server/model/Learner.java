package server.model;

import server.utils.Parameters;

public class Learner {

    private final String name;
    private final String lastname;
    private final String gender;
    private final String birthdate;

    public Learner(String name, String lastname, String gender, String birthdate) {
        this.name = name;
        this.lastname = lastname;
        this.gender = gender;
        this.birthdate = birthdate;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    /* TODO: handle incorrect params list */
    public Learner buildLearner(final Parameters params){
        return new Learner(
                params.searchByParam("name").get(),
                params.searchByParam("lastname").get(),
                params.searchByParam("gender").get(),
                params.searchByParam("birthdate").get());

    }
}
