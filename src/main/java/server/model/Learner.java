package server.model;

import server.utils.Parameters;

import java.util.Map;

public class Learner {

    private String name;
    private String lastname;
    private String gender;
    private String birthdate;


    public Learner(String name, String lastname, String gender, String birthdate) {
        this.name = name;
        this.lastname = lastname;
        this.gender = gender;
        this.birthdate = birthdate;
    }

    public Learner(Parameters p){
         buildLearner(p);
    }
    public Learner(Map<String, String > p){
        buildLearner(p);
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
                params.searchByParam("name"),
                params.searchByParam("lastname"),
                params.searchByParam("gender"),
                params.searchByParam("birthdate"));

    }

    public Learner buildLearner(final Map<String, String> params){
        return new Learner(
                params.get("name"),
                params.get("lastname"),
                params.get("gender"),
                params.get("birthdate"));

    }
}
