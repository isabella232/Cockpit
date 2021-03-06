package com.cockpit.api.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cockpit.api.exception.ResourceNotFoundException;
import com.cockpit.api.model.dao.Mvp;
import com.cockpit.api.model.dao.Team;
import com.cockpit.api.model.dao.TeamMember;
import com.cockpit.api.model.dto.TeamDTO;
import com.cockpit.api.model.dto.TeamMemberDTO;
import com.cockpit.api.repository.MvpRepository;
import com.cockpit.api.repository.TeamMemberRepository;
import com.cockpit.api.repository.TeamRepository;

@Service
public class TeamService {
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final MvpRepository mvpRepository;

    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public TeamService(TeamRepository teamRepository, MvpRepository mvpRepository,
                       TeamMemberRepository teamMemberRepository) {
        this.teamRepository = teamRepository;
        this.mvpRepository = mvpRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    public TeamDTO createNewTeam(TeamDTO teamDTO, Long mvpId) throws ResourceNotFoundException {
        if (verifyUniqueName(teamDTO)) {
            Team teamCreated = teamRepository.save(modelMapper.map(teamDTO, Team.class));
            Optional<Mvp> mvp = mvpRepository.findById(mvpId);
            if (!mvp.isPresent()) {
                throw new ResourceNotFoundException("mvp not found");
            }
            mvp.get().setTeam(teamCreated);
            mvpRepository.save(mvp.get());
            return modelMapper.map(teamCreated, TeamDTO.class);
        }
        return null;

    }

    public TeamDTO findTeamById(Long id) throws ResourceNotFoundException {
        Optional<Team> teamRes = teamRepository.findById(id);
        if (!teamRes.isPresent()) {
            throw new ResourceNotFoundException("Team can not be found");
        }
        return modelMapper.map(teamRes.get(), TeamDTO.class);
    }

    public void deleteTeam(Long id) throws ResourceNotFoundException {
        Optional<Team> teamToDelete = teamRepository.findById(id);
        if (!teamToDelete.isPresent()) {
            throw new ResourceNotFoundException("The team to be deleted does not exist in database");
        }
        for(Mvp mvp : teamToDelete.get().getMvps())
        {
            mvp.setTeam(null);
            mvpRepository.save(mvp);
        }
        teamRepository.delete(teamToDelete.get());
    }

    public List<TeamDTO> findAll() {
        List<Team> teamList = teamRepository.findAllByOrderByName();
        return teamList.stream().map(team -> modelMapper.map(team, TeamDTO.class)).collect(Collectors.toList());

    }

    public List<TeamMemberDTO> findAllMembers() {
        List<TeamMember> teamMemberList = teamMemberRepository.findAllByOrderById();
        return teamMemberList.stream().map(member -> modelMapper.map(member, TeamMemberDTO.class)).collect(Collectors.toList());

    }

    public TeamDTO createTeamMember(Long idTeam, TeamMemberDTO member) throws ResourceNotFoundException {

        Optional<Team> teamToUpdate = teamRepository.findById(idTeam);
        if (!teamToUpdate.isPresent()) {
            throw new ResourceNotFoundException("Team not found");
        }

        teamToUpdate.get().getTeamMembers().add(modelMapper.map(member, TeamMember.class));
        Team team = teamRepository.save(teamToUpdate.get());

        return modelMapper.map(team, TeamDTO.class);
    }

    public TeamDTO deleteTeamMember(Long idTeam, Long teamMeberId) throws ResourceNotFoundException {

        Optional<Team> teamToUpdate = teamRepository.findById(idTeam);
        if (!teamToUpdate.isPresent()) {
            throw new ResourceNotFoundException("Team not found");
        }
        Optional<TeamMember> existingMember = teamMemberRepository.findById(teamMeberId);
        if (!existingMember.isPresent()) {
            throw new ResourceNotFoundException("Team member not found");
        }

        teamToUpdate.get().getTeamMembers().remove(existingMember.get());
        existingMember.get().getTeams().remove(teamToUpdate.get());
        teamMemberRepository.delete(existingMember.get());
        Team team = teamRepository.save(teamToUpdate.get());

        return modelMapper.map(team, TeamDTO.class);
    }

    public TeamDTO unassignTeamMember(Long idTeam, Long teamMeberId) throws ResourceNotFoundException {

        Optional<Team> teamToUpdate = teamRepository.findById(idTeam);
        if (!teamToUpdate.isPresent()) {
            throw new ResourceNotFoundException("Team to update not found");
        }
        Optional<TeamMember> existingMember = teamMemberRepository.findById(teamMeberId);
        if (!existingMember.isPresent()) {
            throw new ResourceNotFoundException("Team Member not found");
        }

        teamToUpdate.get().getTeamMembers().remove(existingMember.get());
        existingMember.get().getTeams().remove(teamToUpdate.get());
        Team team = teamRepository.save(teamToUpdate.get());

        return modelMapper.map(team, TeamDTO.class);
    }

    public TeamDTO assignTeamMember(Long idTeam, Long teamMeberId) throws ResourceNotFoundException {

        Optional<Team> teamToUpdate = teamRepository.findById(idTeam);
        if (!teamToUpdate.isPresent()) {
            throw new ResourceNotFoundException("Team to update not found");
        }
        Optional<TeamMember> existingMember = teamMemberRepository.findById(teamMeberId);
        if (!existingMember.isPresent()) {
            throw new ResourceNotFoundException("Team member not found");
        }

        teamToUpdate.get().getTeamMembers().add(existingMember.get());
        existingMember.get().getTeams().add(teamToUpdate.get());
        Team team = teamRepository.save(teamToUpdate.get());

        return modelMapper.map(team, TeamDTO.class);
    }

    boolean verifyUniqueName(TeamDTO teamDTO) {
        List<Team> teams = teamRepository.findAllByOrderByName();
        for (Team team : teams) {
            if (team.getName().equalsIgnoreCase(teamDTO.getName())) {
                return false;
            }
        }
        return true;

    }
}
