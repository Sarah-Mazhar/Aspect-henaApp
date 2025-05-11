//package com.example.hena.web;
//
//import com.example.hena.event.entity.Event;
//import com.example.hena.user.entity.User;
//import com.example.hena.user.service.UserService;
//import com.example.hena.event.service.EventService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.ui.Model;
//
//@Controller
//public class FrontendController {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private EventService eventService;
//
//    @GetMapping("/")
//    public String home() {
//        return "home";
//    }
//
//    @GetMapping("/event/form")
//    public String eventForm() {
//        return "eventForm";
//    }
//
//    @PostMapping("/event/create")
//    public String createEvent(Event event) {
//        // You’ll need to assign host manually or by session
//        eventService.createEvent(event, new User()); // replace with real user
//        return "redirect:/";
//    }
//
//    @GetMapping("/user/register")
//    public String registerUserForm() {
//        return "registerUser"; // This serves the HTML form
//    }
//
//    @PostMapping("/user/register")
//    public String registerUser(User user) {
//        userService.createUser(user);
//        return "redirect:/";
//    }
//}
