package org.example;

import java.io.*;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ApplicantsProcessor {
    /**
     * @param csvStream input stream allowing to read the CSV input file
     * @return the processing output, in JSON format
     */
    int earliestday;
    int latestday;
    HashMap<String,ApplicantInfo> applicantID = new HashMap<>();
    HashSet<LocalDateTime> dates=new HashSet<>();
    PriorityQueue<Double> topOnes = new PriorityQueue<>();
    TreeMap<Double,String> nameScores = new TreeMap<>(Collections.reverseOrder());
    PriorityQueue<Double> topThreeOnes = new PriorityQueue<>(3);

    public String processApplicants(InputStream csvStream) throws IOException {

        StringBuilder results = new StringBuilder();

        Scanner myReader = new Scanner(csvStream);

        while (myReader.hasNextLine()) {
            String str = myReader.nextLine();
            int k = 0;
            if(str.isEmpty()) continue;
            for(int i=0;i<str.length();i++){
                if(str.charAt(i)==','){
                    k++;
                }
            }
            if (k == 0 || k!=3) {
                continue;
            }
            ArrayList<String> data = new ArrayList<>(Arrays.asList(str.split(",")));
            uniqueNumber(data);
        }
        myReader.close();
        results.append(applicantID.size());

        for(Map.Entry<String,ApplicantInfo> entry : applicantID.entrySet()){
            topOnes.offer(entry.getValue().getScore());
            dates.add(entry.getValue().getDate());
        }

        verifyDate();

        for(Map.Entry<String,ApplicantInfo> entry : applicantID.entrySet()){
            int day=(entry.getValue().getDate()).getDayOfMonth();
            int hour=(entry.getValue().getDate()).getHour();
            if(earliestday!=latestday && day == earliestday){
                entry.getValue().setScore((entry.getValue().getScore())+1);
            }
            if(earliestday!=latestday && day == latestday){
                if(hour>12){
                    entry.getValue().setScore((entry.getValue().getScore())-1);
                }
            }
        }

        for(Map.Entry<String,ApplicantInfo> entry : applicantID.entrySet()){
            topThree(entry.getValue().getScore(),entry.getValue().getName());
        }

        List<String> nameList = new ArrayList<>(nameScores.values());
        for(String name : nameList){
            results.append(" ").append(name);
        }

        results.append(" ").append(avgScores(applicantID,topOnes));
        return results.toString();
    }
    public void uniqueNumber(ArrayList<String> data) {
        int nr=0;
        int pp=-1;
        int pa=-1;
        String email=data.get(1);
        for(int i=0;i<email.length();i++){
            if(i==0&&!Character.isLetter(email.charAt(i))){
                break;
            }
            if(!Character.isLetterOrDigit(email.charAt(i))) {
                if (email.charAt(i) == '@' && nr > 1) {
                    break;
                } else if (email.charAt(i) == '@') {
                    nr++;
                    pa = i;
                } else if (email.charAt(i) == '.') {
                    pp = i;
                    if (pa == -1) {
                        break;
                    } else if (pp < pa) {
                        break;
                    }
                } else if (!(email.charAt(i) == '-') && !(email.charAt(i) == '_')) {
                    break;
                }
            }

            if (i == email.length() - 1) {
                if (nr > 1) {
                    break;
                }
                if (!Character.isLetter(email.charAt(i))) {
                    break;
                }

                try{
                    ApplicantInfo applicant = new ApplicantInfo(data.get(0),data.get(2),data.get(3));
                    applicantID.put(email,applicant);
                } catch (Exception e) {
                    break;
                }
            }

        }

    }

    public void topThree(double scr,String lastName) {

        nameScores.put(scr,lastName);

        if(topThreeOnes.size()<2){
            topThreeOnes.offer(scr);
        } else if (scr>topThreeOnes.peek()) {
            double last=topThreeOnes.poll();
            topThreeOnes.poll();
            topThreeOnes.offer(scr);
            nameScores.remove(last);
        }

    }

    public void verifyDate(){
        LocalDateTime earliest = null;
        LocalDateTime latest = null;
        for(LocalDateTime date : dates){
            if(earliest == null || date.isBefore(earliest)){
                earliest = date;
            }
            if(latest == null || date.isAfter(latest)){
                latest = date;
            }
        }
        earliestday=earliest.getDayOfMonth();
        latestday=latest.getDayOfMonth();
    }

    public double avgScores(HashMap<String,ApplicantInfo> applicantID, PriorityQueue<Double> topOnes){
        int k=applicantID.size();
        if(k%2==1){
            k=k/2+1;
        } else {
            k=k/2;
        }
        double sum=0;
        while (topOnes.size()>k){
            topOnes.poll();
        }
        while(!topOnes.isEmpty()){
            sum+=topOnes.poll();

        }
        return sum=sum/k;
    }

}

class ApplicantInfo{

    String name;
    LocalDateTime date;
    double score;

    public ApplicantInfo(String name,String date, String score) throws IllegalAccessException {

        int k = 0;
        for(int i=0;i<name.length();i++){
            if(name.charAt(i)==' '){
                k++;
            }
        }
        if (k == 0) {
            throw new IllegalAccessException();
        }

        String[] ns=name.split(" ");
        String lastName = ns[ns.length-1];
        this.name = lastName;
        try{
            this.date=LocalDateTime.parse(date,DateTimeFormatter.ISO_DATE_TIME);
        } catch (DateTimeException e){
            throw new IllegalAccessException();
        }
        this.score = Double.parseDouble(score);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
