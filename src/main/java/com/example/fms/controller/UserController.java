package com.example.fms.controller;

import com.example.fms.dto.UserDTO;
import com.example.fms.entity.User;
import com.example.fms.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public boolean createUser(@ApiParam(value = "a link is sent to the user's email and by this link the user can activate his account") @RequestBody UserDTO userDTO){
        return userService.createUser(userDTO);
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
    public List<User> getAllByParam(@RequestParam(required = false) String name,
                             @RequestParam(required = false) Boolean isActive,
                             @RequestParam(required = false) String surname,
                                    @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateAfter,
                                    @ApiParam(value="yyyy-MM-dd HH:mm") @RequestParam(required = false) String dateBefore,
                             @RequestParam(required = false) String position){

        Set<User> fooSet = new LinkedHashSet<>(userService.getAll());

        if (name != null)
            fooSet.retainAll(userService.getAllByName(name));
        if (isActive != null)
            fooSet.retainAll(userService.getAllByActive(isActive));
        if (surname != null)
            fooSet.retainAll(userService.getAllBySurname(surname));
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
