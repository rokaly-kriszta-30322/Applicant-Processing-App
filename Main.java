package org.example;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;

public class Main {
    public static void main(String[] args) throws Exception {

        InputStream inputstream = new FileInputStream("C:\\Users\\app\\src\\main\\java\\org\\example\\data.csv");

        ApplicantsProcessor processor = new ApplicantsProcessor();
        String response = processor.processApplicants(inputstream);

        String[] responses=response.split(" ");
        int score = Integer.parseInt(responses[0]);
        double avg = Double.parseDouble(responses[responses.length-1]);
        List<String> names=Arrays.asList(responses).subList(1,responses.length-1);
        JsonData jsonData = new JsonData(score,names,avg);

        Gson gson = new Gson();
        String json = gson.toJson(jsonData);
        System.out.println(json);
    }
}

class JsonData {
    int uniqueApplicants;
    List<String> topApplicants;
    double averageScore;
    public JsonData(int number, List<String> topThree, double avgScore){
        uniqueApplicants = number;
        topApplicants = topThree;
        averageScore = avgScore;
    }
}