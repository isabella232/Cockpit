package com.cockpit.api.controller;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicProject;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import io.atlassian.util.concurrent.Promise;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class JiraController {
//    @Value("${jira.username}")
    private String username="";
//    @Value("${jira.password}")
    private String password="";
//    @Value("${jira.jiraUrl}")
    private String jiraUrl="https://tdf.atlassian.net/";
    private JiraRestClient restClient;


    // READ
    @GetMapping(
            value = "/api/v1/jira/project/{jiraKey}"
    )
    public @ResponseBody
    ResponseEntity<JiraRestClient> getUSer(@PathVariable Long jiraKey) {
        URI theUri = getJiraUri();
//        Promise<Iterable<BasicProject>> getAllProjects();
        return ResponseEntity.ok(getJiraRestClient());
    }

    private JiraRestClient getJiraRestClient() {
        return new AsynchronousJiraRestClientFactory()
                .createWithBasicHttpAuthentication(getJiraUri(), username, password);
    }

    private URI getJiraUri() {
        return URI.create(jiraUrl);
    }

}
