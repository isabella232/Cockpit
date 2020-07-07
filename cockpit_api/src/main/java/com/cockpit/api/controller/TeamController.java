package com.cockpit.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.cockpit.api.exception.ResourceNotFoundException;
import com.cockpit.api.model.dto.TeamDTO;
import com.cockpit.api.model.dto.TeamMemberDTO;
import com.cockpit.api.service.TeamService;

@RestController
@CrossOrigin
public class TeamController {
	private final TeamService teamService;

	@Autowired
	public TeamController(TeamService teamService) {
		this.teamService = teamService;
	}

	// CREATE a new Team and assign it to an MVP
	@PostMapping(value = "/api/v1/team/create/{mvpId}")
	public ResponseEntity<TeamDTO> createTeam(@PathVariable Long mvpId, @RequestBody TeamDTO teamDTO)
			throws ResourceNotFoundException {
		TeamDTO newTeam = teamService.createNewTeam(teamDTO, mvpId);
		return ResponseEntity.ok(newTeam);
	}

	// GET ALL
	@GetMapping(value = "/api/v1/team/all")
	public ResponseEntity<List<TeamDTO>> getTeams() {
		List<TeamDTO> mvpList = teamService.findAll();
		return ResponseEntity.ok(mvpList);
	}

	// GET Team BY ID
	@GetMapping(value = "/api/v1/team/{id}")
	public ResponseEntity getTeamById(@PathVariable Long id) {
		try {
			TeamDTO teamFound = teamService.findTeamById(id);
			return ResponseEntity.ok().body(teamFound);
		} catch (com.cockpit.api.exception.ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	// UPDATE a Team
	@PutMapping(value = "/api/v1/team/update/{id}")
	public ResponseEntity updateTeam(@RequestBody TeamDTO teamDTO, @PathVariable Long id) {
		try {
			TeamDTO teamUpdated = teamService.updateTeam(teamDTO, id);
			return ResponseEntity.ok().body(teamUpdated);
		} catch (com.cockpit.api.exception.ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	// DELETE a Team
	@DeleteMapping(value = "/api/v1/team/delete/{id}")
	public ResponseEntity<String> deleteTeam(@PathVariable Long id) {
		try {
			teamService.deleteTeam(id);
			return ResponseEntity.ok("One Team has been deleted");
		} catch (ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	// ADD a Team member
	@PutMapping(value = "/api/v1/team/addTeamMember/{id}")
	public ResponseEntity addTeamMember(@RequestBody TeamMemberDTO teamMemberDTO, @PathVariable Long id) {
		try {
			TeamDTO teamUpdated = teamService.createTeamMember(id, teamMemberDTO);
			return ResponseEntity.ok().body(teamUpdated);
		} catch (com.cockpit.api.exception.ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	// DELETE a Team member
	@PutMapping(value = "/api/v1/team/{id}/deleteTeamMember/{teamMeberId}")
	public ResponseEntity deleteTeamMember(@PathVariable("id") Long id, @PathVariable("teamMeberId") Long teamMeberId) {
		try {
			TeamDTO teamUpdated = teamService.deleteTeamMember(id, teamMeberId);
			return ResponseEntity.ok().body(teamUpdated);
		} catch (com.cockpit.api.exception.ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
}
