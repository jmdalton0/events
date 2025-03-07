package com.jmd0.events.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.jmd0.events.models.EventModel;
import com.jmd0.events.models.EventSearch;
import com.jmd0.events.models.UserModel;
import com.jmd0.events.service.EventService;
import com.jmd0.events.service.UserService;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String getAllEvents(Model model) {
        List<EventModel> events = eventService.findAll();
        model.addAttribute("events", events);
        model.addAttribute("message", "Showing all events");
        model.addAttribute("pageTitle", "Events");
        return "events";
    }

    @GetMapping("/create")
    public String showCreateEventForm(Model model) {
        model.addAttribute("event", new EventModel());
        model.addAttribute("pageTitle", "Create Event");

        List<UserModel> users = userService.findAll();  
        model.addAttribute("users", users);

        return "create-event";
    }

    @PostMapping("/create")
    public String createEvent(@ModelAttribute @Valid EventModel event, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Create Event");

            List<UserModel> users = userService.findAll();  
            model.addAttribute("users", users);

            return "create-event";
        }
        eventService.save(event);
        return "redirect:/events";
    }

    @GetMapping("/edit/{id}")
    public String showEditEventForm(@PathVariable String id, Model model) {
        EventModel event = eventService.findById(id);
        model.addAttribute("event", event);

        List<UserModel> users = userService.findAll();
        model.addAttribute("users", users);

        return "edit-event";
    }

    @PostMapping("/edit/{id}")
    public String updateEvent(@PathVariable String id, @ModelAttribute EventModel event, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("event", event);

            List<UserModel> users = userService.findAll();
            model.addAttribute("users", users);

            return "edit-event";
        }
        eventService.updateEvent(id, event);
        return "redirect:/events";
    }

    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable String id) {
        eventService.delete(id);
        return "redirect:/events";
    }

    @GetMapping("/search")
    public String searchForm(Model model) {
        model.addAttribute("eventSearch", new EventSearch());
        return "searchForm";
    }

    @PostMapping("/search")
    public String search(@ModelAttribute @Valid EventSearch eventSearch, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "searchForm";
        }
        List<EventModel> events = eventService.findByDescription(eventSearch.getSearchString());
        model.addAttribute("message", "Search results for " + eventSearch.getSearchString());
        model.addAttribute("events", events);
        return "events";
    }
}
