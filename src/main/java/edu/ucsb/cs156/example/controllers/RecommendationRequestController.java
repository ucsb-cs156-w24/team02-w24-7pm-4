package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.RecommendationRequest;
import edu.ucsb.cs156.example.entities.UCSBDate;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.RecommendationRequestRepository;
import edu.ucsb.cs156.example.repositories.UCSBDateRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import java.time.LocalDateTime;
@Tag(name = "Recommendation")
@RequestMapping("/api/Recommendation")
@RestController
@Slf4j
public class RecommendationRequestController extends ApiController{
    
    @Autowired
    RecommendationRequestRepository recommendationRepository;

    @Operation(summary = "List all Recommendations")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<RecommendationRequest> allRecommendations() {
        Iterable<RecommendationRequest> recommendations = recommendationRepository.findAll();
        return recommendations;
    }

    @Operation(summary = "Create a new recommendation")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public RecommendationRequest postRecommendation(
        @Parameter(name="requesterEmail") @RequestParam String requesterEmail,
        @Parameter(name= "professorEmail") @RequestParam String professorEmail,
        @Parameter(name= "explanation") @RequestParam String explanation,
        @Parameter(name="dateRequested") @RequestParam LocalDateTime dateRequested,
        @Parameter(name="dateNeeded") @RequestParam LocalDateTime dateNeeded,
        @Parameter(name="done") @RequestParam boolean done
        )
        {

        RecommendationRequest recommendation = new RecommendationRequest();
        recommendation.setRequesterEmail(requesterEmail);
        recommendation.setProfessorEmail(professorEmail);
        recommendation.setExplanation(explanation);
        recommendation.setDateRequested(dateRequested);
        recommendation.setDateNeeded(dateNeeded);
        recommendation.setDone(done);

        RecommendationRequest savedRecommendations = recommendationRepository.save(recommendation);

        return savedRecommendations;
    }
    @Operation(summary = "Get a single recommendation")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public RecommendationRequest getById(
            @Parameter(name="id") @RequestParam Long id) {
        RecommendationRequest recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        return recommendation;
    }

    @Operation(summary = "Delete a Recommendation")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deleteRecommendation(
            @Parameter(name="id") @RequestParam Long id) {
        RecommendationRequest recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        recommendationRepository.delete(recommendation);
        return genericMessage("Recommendation with id %s deleted".formatted(id));
    }
    
    @Operation(summary = "Update a single recommendation")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public RecommendationRequestController updateRecommendation(
            @Parameter(name="id") @RequestParam Long id,
            @RequestBody @Valid RecommendationRequest incoming) {

        RecommendationRequest recommendation = recommendationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(RecommendationRequest.class, id));

        recommendation.setRequesterEmail(incoming.getRequesterEmail());
        recommendation.setProfessorEmail(incoming.getProfessorEmail());
        recommendation.setExplanation(incoming.getExplanation());
        recommendation.setDateRequested(incoming.getDateRequested());
        recommendation.setDateNeeded(incoming.getDateNeeded());
        recommendation.setDone(incoming.getDone());
        recommendationRepository.save(recommendation);

        return recommendation;
    }
}