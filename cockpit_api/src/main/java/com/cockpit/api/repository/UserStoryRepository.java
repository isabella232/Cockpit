package com.cockpit.api.repository;

import java.util.List;
import com.cockpit.api.model.dao.Jira;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.cockpit.api.model.dao.UserStory;

@Repository
public interface UserStoryRepository extends CrudRepository<UserStory, Long> {
	  @Query("select COUNT(us) FROM UserStory us, Sprint sp WHERE us.sprint = sp.id AND sp.jira = (:jira) AND us.status='Done' and sp.sprintNumber<=(:sprintNumber)")
	  Integer countNumberOfClosedUsPerSprint(@Param("jira") Jira jira, @Param("sprintNumber") int SprintNumber);
	  
	   @Query("SELECT us FROM UserStory us, Sprint sp WHERE us.sprint = sp.id AND sp.jira = (:jira) AND sp.sprintNumber" +
	            " = (:sprintNumber)")
	    List<UserStory> findMyUserStories(@Param("jira") Jira jira, @Param("sprintNumber") int SprintNumber);

}
