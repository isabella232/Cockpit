package com.cockpit.api.service.jiragateway;

import com.cockpit.api.model.dao.Sprint;
import com.cockpit.api.model.dao.UserStory;
import com.cockpit.api.model.dto.jira.*;
import com.cockpit.api.repository.JiraRepository;
import com.cockpit.api.repository.SprintRepository;
import com.cockpit.api.repository.UserStoryRepository;
import com.cockpit.api.service.UserStoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static javax.management.timer.Timer.*;

@Configuration
@EnableScheduling
@Service
@Transactional
public class UpdateUserStoryService {
    Logger log = LoggerFactory.getLogger(UpdateUserStoryService.class);
    private List<UserStory> stories = new ArrayList<>();
    private final SprintRepository sprintRepository;
    private final JiraRepository jiraRepository;
    private final UserStoryRepository userStoryRepository;
    final JiraApiService jiraApiService;
    final UserStoryService userStoryService;

    @Autowired
    public UpdateUserStoryService(JiraRepository jiraRepository, SprintRepository sprintRepository,
                                  UserStoryRepository userStoryRepository, UserStoryService userStoryService,
                                  JiraApiService jiraApiService) {
        this.jiraRepository = jiraRepository;
        this.sprintRepository = sprintRepository;
        this.userStoryRepository = userStoryRepository;
        this.userStoryService = userStoryService;
        this.jiraApiService = jiraApiService;
    }

    @Value("${spring.jira.urlIssues}")
    private String urlIssues;
    @Value("${spring.jira.urlAllUserStories}")
    private String urlAllUserStories;

    @Scheduled(initialDelay = 15 * ONE_SECOND, fixedDelay = ONE_HOUR)
    public void updateUserStoryInDBFromJira() {
        log.info("UserStory - Start update user stories- Thread : {}", Thread.currentThread().getName());
        List<Sprint> sprintList;
        try {
            sprintList = sprintRepository.findAll();
            for (Sprint sprint : Optional.ofNullable(sprintList).orElse(Collections.emptyList())) {
                updateUserStoryInDBForASprintFromJira(sprint, urlIssues);
            }
            if (!sprintList.isEmpty()) {
                updateUserStoryInDBForBakclogFromJira(sprintList.get(0).getJira().getJiraProjectKey(), urlIssues);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.info("UserStory - End update user stories- Thread : {}", Thread.currentThread().getName());
    }

    @Scheduled(initialDelay = 60 * ONE_SECOND, fixedDelay = ONE_HOUR)
    public void cleaningUselessUSFromDB() {
        log.info("UserStory - Start cleaning user stories - Thread : {}", Thread.currentThread().getName());
        try {
            cleanUserStoriesNotLongerExists(urlAllUserStories);
        } catch (Exception e) {
            log.error("Exception thrown when trying to delete user stories in DB not present in JIRA");
            log.debug(e.getMessage());
        }
        log.info("UserStory - End cleaning user stories - Thread : {}", Thread.currentThread().getName());
    }

    public void cleanUserStoriesNotLongerExists(String urlIssues) throws Exception {
        List<Issue> issueList = new ArrayList<>();
        int maxResults = 100;
        int startAt = 0;
        int totalValues = 0;
        int i = 1;
        while (totalValues >= startAt) {
            String url = String.format(urlIssues, maxResults, startAt);
            ResponseEntity<Issues> result = (ResponseEntity<Issues>) jiraApiService.callJira(url, Issues.class.getName());
            if (result.getStatusCode().is2xxSuccessful()) {
                totalValues = result.getBody().getTotal();
                startAt = (maxResults * i);
                issueList.addAll(result.getBody().getIssues());
                i++;
            } else {
                throw new Exception("Unable to get Issues From Jira");
            }
        }
        try {
            clearDBFromDifferenceInUserStories(issueList);
        } catch (Exception e) {
            log.error("UserStory - Unable to clean user stories that no longer exists in JIRA");
            log.error(e.getMessage());
        }
    }


    public void clearDBFromDifferenceInUserStories(List<Issue> issueList) {

        if (!issueList.isEmpty()) {
            List<String> newIssues = issueList.stream().map(Issue::getKey).collect(Collectors.toList());
            try {
                userStoryRepository.deleteAllByIssueKeyNotIn(newIssues);
            } catch (Exception e) {
                log.error(e.getMessage());

            }
        }

    }

    public List<UserStory> updateUserStoryInDBForBakclogFromJira(String jiraProjectKey, String urlIssues) throws Exception {
        String jqlBacklogUS = "project=" + jiraProjectKey + " AND Sprint=null AND issuetype=Story&expand=changelog";

        String urlBacklogUS = urlIssues + jqlBacklogUS;
        ResponseEntity<Issues> resultBacklogUS = (ResponseEntity<Issues>) jiraApiService.callJira(urlBacklogUS, Issues.class.getName());

        List<Issue> issueListBacklogUS = (resultBacklogUS.getBody().getIssues());
        if (resultBacklogUS.getStatusCode().is2xxSuccessful()) {
            return getUserStories(null, issueListBacklogUS);
        }
        return stories;
    }

    public List<UserStory> updateUserStoryInDBForASprintFromJira(Sprint sprint, String urlIssues) throws Exception {

        String sprintId = String.valueOf(sprint.getJiraSprintId());
        String jqlSprintUS = "Sprint=" + sprintId + " AND issuetype=Story&expand=changelog";
        String urlSprintUS = urlIssues + jqlSprintUS;

        ResponseEntity<Issues> resultSprintUS = (ResponseEntity<Issues>) jiraApiService.callJira(urlSprintUS, Issues.class.getName());
        List<Issue> issueListSprintUS = (resultSprintUS.getBody().getIssues());
        if (resultSprintUS.getStatusCode().is2xxSuccessful()) {
            return getUserStories(sprint, issueListSprintUS);
        }
        return stories;
    }

    private List<UserStory> getUserStories(Sprint sprint, List<Issue> issueList) {
        for (Issue issue : Optional.ofNullable(issueList).orElse(Collections.emptyList())) {
            UserStory userStory;
            Optional<UserStory> optionalUserStory = userStoryRepository.findByJiraIssueId(Integer.parseInt(issue.getId()));
            userStory = optionalUserStory.orElseGet(UserStory::new);
            stories.add(setUserStory(userStory, sprint, issue));
            userStoryRepository.save(userStory);
        }
        return stories;
    }

    private UserStory setUserStory(UserStory userStory, Sprint sprint, Issue issue) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        userStory.setJiraIssueId(Integer.parseInt(issue.getId()));
        userStory.setIssueKey(issue.getKey());
        if (sprint != null) {
            userStory.setSprint(sprint);
        }

        if (issue.getFields() != null) {
            userStory.setSummary(issue.getFields().getSummary());
            userStory.setDescription("");
            userStory.setStatus(issue.getFields().getStatus().getName());
            userStory.setJira(jiraRepository.findByJiraProjectId(Integer.parseInt(issue.getFields().getProject().getId())));
            userStory.setPriority((issue.getFields().getPriority() == null) ? "N/A" : issue.getFields().getPriority().getName());
            // Custome field could be different for each Jira server
            userStory.setStoryPoint((issue.getFields().getCustomfield10026() == null) ? 0 : (double) issue.getFields().getCustomfield10026());
            try {
                userStory.setCreationDate((issue.getFields().getCreated() == null) ? null : dateFormat.parse(issue.getFields().getCreated()));
            } catch (ParseException e) {
                log.error("Exception thrown when parsing updateDate for UserStory Status");
            }
        }
        return userStory;
    }
}