package api;

public interface API {

    /* TODO: extract state from learner and parte to OWL */
    void persistLearner(String learner);

    /* TODO: extract state from professor and parte to OWL */
    void persistProfessor(String professor);

    /* TODO: to be refactored, mentioned for schematize purpose */
    void generateNextStep();

    /* TODO: send to the client the path generated */
    void shareGeneratedPath();

}
