package com.chowscott.todo.TodoTask;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.chowscott.todo.User.User;
import com.chowscott.todo.User.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TodoTaskController {

  @Autowired
  private UserRepository userRepo;

  @Autowired
  private TodoTaskRepository repository;

  private Optional<User> getUser(Principal principal) {
    return userRepo.findById(this.getUserId(principal));
  }

  private Long getUserId(Principal principal) {
    return Long.valueOf(principal.getName());
  }

  @GetMapping(value = "/")
  public String landing(Principal user, Model model) {
    Long userId = getUserId(user);
    if (user instanceof OAuth2AuthenticationToken) {
      Map<String, Object> attributes = ((OAuth2AuthenticationToken) user).getPrincipal().getAttributes();
      String name = (String) attributes.get("name");
      User userObj = new User(userId, name);
      userRepo.save(userObj);
    }
    ArrayList<TodoTask> todoList = (ArrayList<TodoTask>) repository.findAll();
    todoList.removeIf(todo -> !todo.getUserId().equals(userId));
    model.addAttribute("items", new TodoTaskResource(todoList));
    model.addAttribute("newItem", new TodoTask());
    return "landing";
  }

  @PostMapping(value = "/add")
  public String addTodo(Principal principal, @ModelAttribute TodoTask todoTask) {
    Optional<User> user = getUser(principal);
    if (user.isPresent()) {
      TodoTask newTask = new TodoTask(todoTask.getName(), user.get().getId());
      repository.save(newTask);
    }
    return "redirect:/";
  }

  @PostMapping(value = "/update", params = { "updateId", "!removeId" })
  public String updateTodo(Principal principal, final HttpServletRequest req) {
    Optional<User> user = getUser(principal);
    if (user.isPresent()) {
      final Long id = Long.valueOf(req.getParameter("updateId"));
      TodoTask task = repository.findById(id).get();
      if (task != null) {
        task.setComplete(!task.isComplete());
        repository.save(task);
      }
    }
    return "redirect:/";
  }

  @PostMapping(value = "/update", params = { "removeId" })
  public String removeTodo(Principal principal, final HttpServletRequest req) {
    Optional<User> user = getUser(principal);
    if (user.isPresent()) {
      final Long id = Long.valueOf(req.getParameter("removeId"));
      repository.deleteById(id);
    }
    return "redirect:/";
  }

}
