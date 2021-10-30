package server.utils;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CourseScraper {
    static String pathadoraUrl = "http://www.semanticweb.org/learning-path/pathadora-ontology#";
    static String pathadoraOWL = "pathadora-ontology:";

    public List<Map<String, String>> extractCourses(int index, String url, String faculty, int year, String obligatory, List<String> languages) throws IOException {
        List<Map<String, String>> output = new ArrayList<>();

        FileWriter writer = new FileWriter(index + "_" + faculty);
        Document doc = Jsoup.connect(url).get();
        Element tableElement = doc.select("table").get(index);

        assert tableElement != null;
        Elements tableRowElements = tableElement.select(":not(thead) tr");

        for (int i = 0; i < tableRowElements.size(); i++) {
            Element row = tableRowElements.get(i);
            Elements rowItems = row.select("td");

            Map<String, String> courseData = new HashMap<>();
            for (int j = 1; j < rowItems.size(); j++) {
                String key = getKeyByHeader(j, rowItems.size());
                String value = cleanString(key, rowItems.get(j).text());
                if (!value.isEmpty()) {
                    courseData.put(key, value);
                }
            }
            output.add(courseData);
        }

        String coursesToOwl = parseToOWL(output, faculty, year, obligatory, languages);

        writer.append(coursesToOwl);
        writer.close();

        return output;
    }


    private static String parseToOWL(List<Map<String, String>> output, String faculty, int year, String obligatory, List<String> languages) {
        String courseC = "";
        String courseSSDClass = "";
        String updateOwl = "";
        String courseSSDIndividual = "";

        for (Map<String, String> course : output) {
            if (course.get("course") != null || course.get("scientificArea") != null) {
                String courseName = course.get("course");
                String period = course.get("period");
                String type = course.get("type");
                String cfu = course.get("cfu");

                if (type == null) {
                    type = "DEFAULT";
                }

                String scientificArea = course.get("scientificArea");

                courseC += " <owl:Class rdf:about=\"" + pathadoraUrl + courseName + "\">\n" +
                        " <rdfs:subClassOf rdf:resource=\"" + pathadoraUrl + faculty + "\"/>\n" +
                        "</owl:Class>\n";

                courseSSDClass += " <owl:Class rdf:about=\"" + pathadoraUrl + scientificArea + "\"> \n" +
                        "<rdfs:subClassOf rdf:resource=\"" + pathadoraUrl + "CourseSSD\"/> </owl:Class>\n";

                courseSSDIndividual += "<owl:NamedIndividual rdf:about=\"" + pathadoraUrl + "Course_SSD_" + scientificArea + "\">\n" +
                        "<rdf:type rdf:resource=\"" + pathadoraUrl + scientificArea + "\"/>\n" +
                        "</owl:NamedIndividual>";

                String languagesOWL = "";
                for (String lang : languages) {
                    languagesOWL += "<" + pathadoraOWL + "courseLanguage rdf:resource=\"" + pathadoraUrl + "Language_" + lang + "\"/>\n";

                }

                updateOwl += " \n\n" +
                        "<owl:NamedIndividual rdf:about=\"" + pathadoraUrl + "Course_" + courseName + "\">\n" +
                        "<rdf:type rdf:resource=\"" + pathadoraUrl + courseName + "\"/>\n" +
                        "<" + pathadoraOWL + "courseSSD rdf:resource=\"" + pathadoraUrl + "Course_SSD_" + scientificArea + "\"/>\n" +
                        "<" + pathadoraOWL + "courseType rdf:resource=\"" + pathadoraUrl + "Course_Type_" + type + "\"/>\n" +
                        "<" + pathadoraOWL + "isCourseObligatory rdf:resource=\"" + pathadoraUrl + obligatory + "\"/>\n" +
                        "<" + pathadoraOWL + "courseCFU rdf:datatype=\"http://www.w3.org/2001/XMLSchema#integer\">" + cfu + "</" + pathadoraOWL + "courseCFU>\n" +
                        "<" + pathadoraOWL + "coursePeriod rdf:datatype=\"http://www.w3.org/2001/XMLSchema#integer\">" + period + "</" + pathadoraOWL + "coursePeriod>\n" +
                        "<" + pathadoraOWL + "courseYear rdf:datatype=\"http://www.w3.org/2001/XMLSchema#integer\">" + year + "</" + pathadoraOWL + "courseYear>\n" +
                        languagesOWL +
                        "</owl:NamedIndividual>\n";
            }
        }

        return " \n " + courseC + "\n " + courseSSDClass + "\n " + updateOwl + "\n " + courseSSDIndividual;
    }


    private String cleanString(String key, String data) {
        if (key.equals("period") || key.equals("cfu") || key.equals("year"))
            return data; // do not ignore period (one char)
        String cleaned = data.replaceAll(" ", "_").replaceAll("[0-9, /']", "");
        if (cleaned.length() > 1 && cleaned.substring(0, 1).contains("_")) return cleaned.substring(1);
        else return cleaned;
    }


    private String getKeyByHeader(int index, int rowSize) {
        if (rowSize >= 6) {
            if (index == 1) return "course";
            if (index == 2) return "period";
            if (index == 3) return "type";
            if (index == 4) return "scientificArea";
            if (index == 5) return "cfu";
        } else {
            if (index == 1) return "course";
            if (index == 2) return "period";
            if (index == 3) return "scientificArea";
            if (index == 4) return "cfu";
        }
        return "Default";
    }


    public static void main(String[] args) throws IOException {
        String url = "https://corsi.unibo.it/magistralecu/MedicinaVeterinaria/insegnamenti/piano/2021/8617/000/000/2020";
        String faculty = "Veterinary_medicine";

        final String yes = "yes";
        final String no = "no";
        final String italian = "Italian";
        final String english = "English";

        List<String> languages = Arrays.asList(italian);

        new CourseScraper().extractCourses(0, url, faculty, 1, yes, languages);


    }

}
