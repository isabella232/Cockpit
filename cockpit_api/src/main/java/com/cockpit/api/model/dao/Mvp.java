package com.cockpit.api.model.dao;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.Set;

@Entity
@Table(name = "mvp")
public class Mvp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull(message="Name is mandatory")
    private String name;

    @NotNull(message="Entity is mandatory")
    private String entity;

    @NotNull(message="Avatar url is mandatory")
    private String urlMvpAvatar;

    @NotNull(message="Cycle is mandatory")
    private int cycle;

    private int scopeCommitment;

    private Integer sprintNumber;
    
    private String mvpDescription;

    private String status;

    private Integer technicalDebt;

    @ManyToOne(cascade=CascadeType.MERGE)
    @JoinColumn(name = "id_team")
    private Team team;

    @ManyToMany(cascade=CascadeType.MERGE)
    @JoinTable(name = "mvps_technologies",
            joinColumns = @JoinColumn(name = "id_mvp"),
            inverseJoinColumns = @JoinColumn(name = "id_technology")
    )
    private Set<Technology> technologies;

    @OneToOne(mappedBy = "mvp")
    private Jira jira;

    public Mvp() {
        // Empty constructor
    }

    public int getScopeCommitment() {
        return scopeCommitment;
    }

    public void setScopeCommitment(int scopeCommitment) {
        this.scopeCommitment = scopeCommitment;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public void setUrlMvpAvatar(String urlMvpAvatar) {
        this.urlMvpAvatar = urlMvpAvatar;
    }

    public void setCycle(int cycle) {
        this.cycle = cycle;
    }

    public void setMvpDescription(String mvpDescription) {
        this.mvpDescription = mvpDescription;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTechnicalDebt(Integer technicalDebt) {
        this.technicalDebt = technicalDebt;
    }

    public void setTeam(Team team) { this.team = team; }

    public void setTechnologies(Set<Technology> technologies) {
        this.technologies = technologies;
    }

    public void setJira(Jira jira) {
        this.jira = jira;
    }

    public String getName() {
        return name;
    }

    public String getEntity() {
        return entity;
    }

    public String getUrlMvpAvatar() {
        return urlMvpAvatar;
    }

    public int getCycle() {
        return cycle;
    }

    public String getMvpDescription() {
        return mvpDescription;
    }

    public String getStatus() {
        return status;
    }

    public Integer getTechnicalDebt() {
        return technicalDebt;
    }

    public Team getTeam() {
        return team;
    }

    public Set<Technology> getTechnologies() {
        return technologies;
    }

    public Jira getJira() {
        return jira;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

	public Integer getSprintNumber() {
		return sprintNumber;
	}

	public void setSprintNumber(Integer sprintNumber) {
		this.sprintNumber = sprintNumber;
	}

}
