package com.example.fms.controller;

import com.example.fms.entity.User;
import com.example.fms.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("/position/{position}")
    public User setPosition(@PathVariable String position, Principal principal){
        return userService.setPosition(position, principal.getName());
    }

    @DeleteMapping("/{id}")
    public boolean deleteByd(@PathVariable Long id){
        return userService.deleteUserById(id);
    }

    @GetMapping("/get")
    public List<User> getAllByParam(HttpServletRequest request){
        String name = request.getParameter("name");
        String surname = request.getParameter("surname");
        String isActive = request.getParameter("isActive");
        String dateAfter = request.getParameter("dateAfter");
        String dateBefore = request.getParameter("dateBefore");
        String position = request.getParameter("position");

        Set<User> fooSet = new LinkedHashSet<>(userService.getAll());

        if (name != null)
            fooSet.retainAll(userService.getAllByName(name));
        if (surname != null)
            fooSet.retainAll(userService.getAllBySurname(surname));
        if (isActive != null)
            fooSet.retainAll(userService.getAllByActive(Boolean.parseBoolean(isActive)));
        if (dateAfter != null)
            fooSet.retainAll(userService.getAllByDateCreatedAfter(dateAfter));
        if (dateBefore != null)
            fooSet.retainAll(userService.getAllByDateCreatedBefore(dateBefore));
        if (position != null)
            fooSet.retainAll(userService.getAllByPosition(position));

        return new ArrayList<>(fooSet);
    }

    @GetMapping("/email/{email}")
    public User getByEmail(@PathVariable String email){ //email нужно написать точно и правильно
        return userService.getByEmail(email);
    }

}
