package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.entities.UCSBDiningCommonsMenuItems;
import edu.ucsb.cs156.example.errors.EntityNotFoundException;
import edu.ucsb.cs156.example.repositories.UCSBDiningCommonsMenuItemRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
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


@Tag(name="UCSBDiningCommonsMenuItem")
@RequestMapping("/api/ucsbdiningcommonsmenuitems")
@RestController
@Slf4j
public class UCSBDiningCommonsMenuItemsController extends ApiController
{
    @Autowired
    UCSBDiningCommonsMenuItemRepository ucsbDiningCommonsMenuItemRepository;

    @Operation(summary= "List all ucsb dining commons menu items")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<UCSBDiningCommonsMenuItems> allmenuitems() {
        Iterable<UCSBDiningCommonsMenuItems> menuItems = ucsbDiningCommonsMenuItemRepository.findAll();
        return menuItems;
    }

    @Operation(summary= "Create a new dining commons menu item")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/post")
    public UCSBDiningCommonsMenuItems postUcsbDiningCommonsMenuItem(
            @Parameter(name="diningCommonsCode") @RequestParam String diningCommonsCode,
            @Parameter(name="name") @RequestParam String name,
            @Parameter(name="station") @RequestParam String station)
            throws JsonProcessingException {

        log.info("diningCommonsCode={}, name={}, station={}", diningCommonsCode, name, station);

        UCSBDiningCommonsMenuItems menuitem = new UCSBDiningCommonsMenuItems();
        menuitem.setDiningCommonsCode(diningCommonsCode);
        menuitem.setName(name);;
        menuitem.setStation(station);

        UCSBDiningCommonsMenuItems savedUcsbDiningCommonsMenuItem = ucsbDiningCommonsMenuItemRepository.save(menuitem);

        return savedUcsbDiningCommonsMenuItem;
    }

    @Operation(summary= "Get a menu item")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public UCSBDiningCommonsMenuItems getById(
            @Parameter(name="id") @RequestParam Long id) {
        UCSBDiningCommonsMenuItems menuitem = ucsbDiningCommonsMenuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException( UCSBDiningCommonsMenuItems.class, id));

        return menuitem;
    }

    @Operation(summary= "Delete a menu item")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("")
    public Object deletemenuitem(
            @Parameter(name="id") @RequestParam Long id) {
                UCSBDiningCommonsMenuItems menuItem = ucsbDiningCommonsMenuItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonsMenuItems.class, id));

                ucsbDiningCommonsMenuItemRepository.delete(menuItem);
        return genericMessage("UCSBDiningCommonsMenuItem with id %s deleted".formatted(id));
    }

    @Operation(summary= "Update  menu item")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("")
    public UCSBDiningCommonsMenuItems updatemenuitem(
            @Parameter(name="id") @RequestParam Long id,
            @RequestBody @Valid UCSBDiningCommonsMenuItems incoming) {

        UCSBDiningCommonsMenuItems menuitem = ucsbDiningCommonsMenuItemRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(UCSBDiningCommonsMenuItems.class, id));

        menuitem.setDiningCommonsCode(incoming.getDiningCommonsCode());
        menuitem.setName(incoming.getName());
        menuitem.setStation(incoming.getStation());

        ucsbDiningCommonsMenuItemRepository.save(menuitem);

        return menuitem;
    }

}