package model.model;

public class Candidate {
    private String candidateID;
    private String name;
    private String email;
    private String skills;
    private String source;
    private String resumePath;
    private String status;

    public Candidate(String candidateID, String name, String email, String skills, String source, String resumePath, String status) {
        this.candidateID = candidateID;
        this.name = name;
        this.email = email;
        this.skills = skills;
        this.source = source;
        this.resumePath = resumePath;
        this.status = status;
    }

    public String getCandidateID() { return candidateID; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getSkills() { return skills; }
    public String getSource() { return source; }
    public String getResumePath() { return resumePath; }
    public String getStatus() { return status; }
    
    public void setStatus(String status) { this.status = status; }
}
