package server.utils;



import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CourseScraper {
    static String pathadoraUrl = "http://www.semanticweb.org/learning-path/pathadora-ontology#";
    static String pathadoraOWL = "pathadora-ontology:";

    public List<Map<String, String>> extractCourses(int index, String url, String faculty, int year, String obligatory) throws IOException {
        List<Map<String, String>> output = new ArrayList<>();

        FileWriter writer = new FileWriter(faculty);
        Document doc = Jsoup.connect(url).get();
        Element tableElement = doc.select("table").get(index);

        assert tableElement != null;
        Elements tableRowElements = tableElement.select(":not(thead) tr");

        for (int i = 0; i < tableRowElements.size(); i++) {
            Element row = tableRowElements.get(i);
            Elements rowItems = row.select("td");

            Map<String, String> courseData = new HashMap<>();
            for (int j = 1; j < rowItems.size(); j++) {
                courseData.put(getKeyByHeader(j, rowItems.size()), cleanString(rowItems.get(j).text()));

                if (j != rowItems.size() - 1) {
                  //  writer.append(',');
                }
            }
            output.add(courseData);
        }
        String coursesToOwl = parseToOWL(output,  faculty, year, obligatory);

        writer.append(coursesToOwl);
        writer.close();

        return output;
    }


    private static String parseToOWL(List<Map<String, String>> output, String faculty, int year,  String obligatory){
        System.out.println("output" + output.size() + "\n\n\n\n\n\n\n\n\"");

        String courseC = "";
        String courseSSDClass  = "";
        String updateOwl = "";

        for(Map<String, String> course : output) {
            for (Map.Entry e : course.entrySet()) {
                String courseName = course.get("course");
                String period = course.get("period");
                String type = course.get("type");
                String scientificArea = course.get("scientificArea");
                String cfu = course.get("cfu");

                System.out.println("course: " + courseName);
                System.out.println("period: " + period);
                System.out.println("type: " + type);
                System.out.println("scientificArea: " + scientificArea);
                System.out.println("");

                courseC += " <owl:Class rdf:about=\""+ pathadoraUrl + courseName + "\">\n" +
                        "           <rdfs:subClassOf rdf:resource=\""+pathadoraUrl + faculty + "\"/>\n" +
                        "</owl:Class>";

                courseSSDClass += " <owl:Class rdf:about=\""+pathadoraUrl + scientificArea+"\"> \n" +
                        "<rdfs:subClassOf rdf:resource=\""+pathadoraUrl+"CourseSSD\"/> </owl:Class>\n";

                updateOwl +=" \n\n" +
                          "<owl:NamedIndividual rdf:about=\"" + pathadoraUrl + "Course_" + courseName + "\">\n" +
                                "<rdf:type rdf:resource=\"" + pathadoraUrl + courseName + "\"/>\n" +
                                "<" + pathadoraOWL + "courseSSD rdf:resource=\"" + pathadoraUrl + "Course_SSD_" + scientificArea + "\"/>\n" +
                                "<" + pathadoraOWL + "courseType rdf:resource=\"" + pathadoraUrl + "Course_Type_"+type+"\"/>\n" +
                                "<" + pathadoraOWL + "isCourseObligatory rdf:resource=\"" + pathadoraUrl + obligatory+"\"/>\n" +
                                "<" + pathadoraOWL + "courseCFU rdf:datatype=\"http://www.w3.org/2001/XMLSchema#integer\">" + cfu + "</" + pathadoraOWL + "courseCFU>\n" +
                                "<" + pathadoraOWL + "coursePeriod rdf:datatype=\"http://www.w3.org/2001/XMLSchema#integer\">" + period + "</" + pathadoraOWL + "coursePeriod>\n" +
                                "<" + pathadoraOWL + "courseYear rdf:datatype=\"http://www.w3.org/2001/XMLSchema#integer\">" + year + "</" + pathadoraOWL + "courseYear>\n" +
                           "</owl:NamedIndividual>\n";

                System.out.println(courseC);
                System.out.println(courseSSDClass);
                System.out.println(updateOwl);
            }
        }
        return courseC + "\n " + courseSSDClass + "\n "  + updateOwl;
    }


    private String cleanString(String data){
        if(data.length()<2) return data; // do not ignore period (one char)

        String cleaned = data.replaceAll(" ", "_").replaceAll("[0-9, /]", "");

        if(cleaned.length()>1 && cleaned.substring(0,1).contains("_")) return cleaned.substring(1);
        else return cleaned;
    }


    private String getKeyByHeader(int index, int rowSize){
        if(rowSize>=5)
            if (index == 1) return "course";
        if (index == 2) return "period";
        if (index == 3) return "type";
        if (index == 4) return "scientificArea";
        if (index == 5) return "cfu";
        else return "Default";
    }


    public static void main(String[] args) throws IOException {
        new CourseScraper().extractCourses(
                0,
                "https://corsi.unibo.it/laurea/EconomiaMarketingAgroIndustriale/insegnamenti/piano/2021/5833/000/000/2021",
                "Marketing_and_Economics_of_the_Agro-Industrial_System",
                    1,
                "yes"
                );
    }

}
