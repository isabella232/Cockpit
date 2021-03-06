package com.cockpit.api.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cockpit.api.exception.ResourceNotFoundException;
import com.cockpit.api.model.dao.Jira;
import com.cockpit.api.model.dao.Mvp;
import com.cockpit.api.model.dto.BurnUpChartDTO;
import com.cockpit.api.model.dto.MvpDTO;

import com.cockpit.api.model.dao.Sprint;

@Service
public class BurnUpChartService {

    private JiraService jiraService;

    private UserStoryService userStoryService;

    private SprintService sprintService;

    private MvpService mvpService;

    private ModelMapper modelMapper = new ModelMapper();


    @Autowired
    public BurnUpChartService(
            JiraService jiraService,
            UserStoryService userStoryService,
            SprintService sprintService,
            MvpService mvpService

    ) {
        this.jiraService = jiraService;
        this.userStoryService = userStoryService;
        this.sprintService = sprintService;
        this.mvpService = mvpService;
    }

    public List<BurnUpChartDTO> getChartData(Long id) throws ResourceNotFoundException {
        List<BurnUpChartDTO> chartDataList = new ArrayList<>();
        MvpDTO mvpDto = mvpService.findMvpById(id);
        Mvp mvp = modelMapper.map(mvpDto, Mvp.class);
        Jira jira = jiraService.findByMvp(mvp);
        double rate = 0;
        long lastNbUsClosed = 0;
        int iteration = 1;
        double projection = 0;
        int totalUSNumber = 0;
        int nbOfSprintsInChart = 8;
        Integer sumUsClosedInLastSprints = 0;
        if (mvp.getSprintNumber() != null) {
            nbOfSprintsInChart = mvp.getSprintNumber();
        }
        for (int sprintNumber = 1; sprintNumber <= nbOfSprintsInChart; sprintNumber++) {
            BurnUpChartDTO chart = new BurnUpChartDTO();

            chart.setSprintId(sprintNumber);
            Sprint thisSprint = sprintService.findByJiraAndSprintNumber(jira, sprintNumber);
            int actualSprintStories = userStoryService.getNumberOfStoriesInOneSprint(thisSprint, jira);
            totalUSNumber = totalUSNumber + actualSprintStories;

            if (thisSprint!=null  && thisSprint.getCompletedUsNumber() != null) {
                sumUsClosedInLastSprints = sumUsClosedInLastSprints + thisSprint.getCompletedUsNumber();
                setExpected(chart, totalUSNumber, sprintNumber, sumUsClosedInLastSprints,jira, thisSprint);
            }

            setTotalStories(chart, sprintNumber, jira);
            setUsClosed(chart, sprintNumber, jira, sumUsClosedInLastSprints);

            // Set projection
            if (chart.getUsClosed() != null) {
                rate = ((double) sumUsClosedInLastSprints / (sprintNumber + 1));
                lastNbUsClosed = sumUsClosedInLastSprints;
            } else {
                projection = lastNbUsClosed + iteration * rate;
                chart.setProjectionUsClosed(projection);
                iteration++;
            }
            if (sprintNumber == sprintService.findSprintNumberForADate(jira, Calendar.getInstance().getTime())) {
                chart.setProjectionUsClosed((double) lastNbUsClosed);
            }

            chartDataList.add(chart);
        }
        return chartDataList;

    }

    private void setUsClosed(BurnUpChartDTO chart, int sprintNumber, Jira jira, int numberOfUsClosed) {
        int sprintN = sprintService.findSprintNumberForADate(jira, Calendar.getInstance().getTime());
        if (sprintNumber <= sprintN) {
            chart.setUsClosed(numberOfUsClosed);

        }
    }

    private void setTotalStories(BurnUpChartDTO chart, int sprintNumber, Jira jira) {

        Sprint currentSprint = sprintService.findByJiraAndSprintNumber(jira, sprintNumber);
        if (currentSprint != null) {
            chart.setTotalStories(currentSprint.getTotalNbUs());
        }
    }

    private void setExpected(BurnUpChartDTO chart, int totalUSNumber, int sprintNumber, int sumUsClosedInLastSprints,
                             Jira jira, Sprint sprint) {

        if (jira.getCurrentSprint() != null && sprintNumber <= jira.getCurrentSprint()
                && sprint.getNotCompletedUsNumber() != null
                && sprint.getPuntedUsNumber() != null
        ) {
            chart.setExpectedUsClosed(sumUsClosedInLastSprints + sprint.getNotCompletedUsNumber() + sprint.getPuntedUsNumber());
        } else if (sprint != null) {
            chart.setExpectedUsClosed(totalUSNumber);
        } else {
            chart.setExpectedUsClosed(null);
        }
    }
}
