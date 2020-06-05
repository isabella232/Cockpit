------------------------------------------------------------
--        Script Postgre
------------------------------------------------------------



------------------------------------------------------------
-- Table: Jira
------------------------------------------------------------
CREATE TABLE public.Jira(
                            id                 SERIAL NOT NULL ,
                            jira_project_key   VARCHAR (4) NOT NULL ,
                            current_sprint     INT  NOT NULL ,
                            jira_project_id    INT  NOT NULL ,
                            mvp_start_date     DATE  NOT NULL ,
                            mvp_end_date       DATE  NOT NULL  ,
                            CONSTRAINT Jira_PK PRIMARY KEY (id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: Mvp
------------------------------------------------------------
CREATE TABLE public.Mvp(
                           id                SERIAL NOT NULL ,
                           name              VARCHAR (50) NOT NULL ,
                           entity            VARCHAR (50) NOT NULL ,
                           url_mvp_avatar    VARCHAR (50) NOT NULL ,
                           cycle             INT  NOT NULL ,
                           mvp_description   VARCHAR (255) NOT NULL ,
                           status            VARCHAR (50) NOT NULL ,
                           id_Jira           INT  NOT NULL  ,
                           CONSTRAINT Mvp_PK PRIMARY KEY (id)

    ,CONSTRAINT Mvp_Jira_FK FOREIGN KEY (id_Jira) REFERENCES public.Jira(id)
    ,CONSTRAINT Mvp_Jira_AK UNIQUE (id_Jira)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: TeamMember
------------------------------------------------------------
CREATE TABLE public.TeamMember(
                                  id           SERIAL NOT NULL ,
                                  first_name   VARCHAR (50) NOT NULL ,
                                  last_name    VARCHAR (50) NOT NULL ,
                                  email        VARCHAR (50) NOT NULL  ,
                                  CONSTRAINT TeamMember_PK PRIMARY KEY (id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: Team
------------------------------------------------------------
CREATE TABLE public.Team(
                            id       SERIAL NOT NULL ,
                            name     VARCHAR (4) NOT NULL ,
                            id_Mvp   INT    ,
                            CONSTRAINT Team_PK PRIMARY KEY (id)

    ,CONSTRAINT Team_Mvp_FK FOREIGN KEY (id_Mvp) REFERENCES public.Mvp(id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: Technology
------------------------------------------------------------
CREATE TABLE public.Technology(
                                  id     SERIAL NOT NULL ,
                                  name   VARCHAR (50) NOT NULL ,
                                  url    VARCHAR (5) NOT NULL  ,
                                  CONSTRAINT Technology_PK PRIMARY KEY (id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: Sprint
------------------------------------------------------------
CREATE TABLE public.Sprint(
                              id                  SERIAL NOT NULL ,
                              sprint_start_date   DATE  NOT NULL ,
                              sprint_end_date     DATE  NOT NULL ,
                              team_motivation     INT  NOT NULL ,
                              team_mood           INT  NOT NULL ,
                              team_confidence     INT  NOT NULL ,
                              total_nb_us         INTEGER  NOT NULL ,
                              sprint_number       INT  NOT NULL ,
                              id_Jira             INT  NOT NULL  ,
                              CONSTRAINT Sprint_PK PRIMARY KEY (id)

    ,CONSTRAINT Sprint_Jira_FK FOREIGN KEY (id_Jira) REFERENCES public.Jira(id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: UserStory
------------------------------------------------------------
CREATE TABLE public.UserStory(
                                 id              SERIAL NOT NULL ,
                                 creation_date   DATE  NOT NULL ,
                                 end_date        DATE  NOT NULL ,
                                 story_point     INT  NOT NULL ,
                                 description     VARCHAR (255) NOT NULL ,
                                 issue_key       VARCHAR (20) NOT NULL ,
                                 jira_issue_id   INT  NOT NULL ,
                                 priority        VARCHAR (20) NOT NULL ,
                                 start_date      DATE  NOT NULL ,
                                 status          VARCHAR (20) NOT NULL ,
                                 summary         VARCHAR (255) NOT NULL ,
                                 id_Sprint       INT   ,
                                 id_Jira         INT  NOT NULL  ,
                                 CONSTRAINT UserStory_PK PRIMARY KEY (id)

    ,CONSTRAINT UserStory_Sprint_FK FOREIGN KEY (id_Sprint) REFERENCES public.Sprint(id)
    ,CONSTRAINT UserStory_Jira0_FK FOREIGN KEY (id_Jira) REFERENCES public.Jira(id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: mvp_technologies
------------------------------------------------------------
CREATE TABLE public.mvp_technologies(
                                        id              INT  NOT NULL ,
                                        id_Technology   INT  NOT NULL  ,
                                        CONSTRAINT mvp_technologies_PK PRIMARY KEY (id,id_Technology)

    ,CONSTRAINT mvp_technologies_Mvp_FK FOREIGN KEY (id) REFERENCES public.Mvp(id)
    ,CONSTRAINT mvp_technologies_Technology0_FK FOREIGN KEY (id_Technology) REFERENCES public.Technology(id)
)WITHOUT OIDS;


------------------------------------------------------------
-- Table: team_team_members
------------------------------------------------------------
CREATE TABLE public.team_team_members(
                                         id              INT  NOT NULL ,
                                         id_TeamMember   INT  NOT NULL  ,
                                         CONSTRAINT team_team_members_PK PRIMARY KEY (id,id_TeamMember)

    ,CONSTRAINT team_team_members_Team_FK FOREIGN KEY (id) REFERENCES public.Team(id)
    ,CONSTRAINT team_team_members_TeamMember0_FK FOREIGN KEY (id_TeamMember) REFERENCES public.TeamMember(id)
)WITHOUT OIDS;


