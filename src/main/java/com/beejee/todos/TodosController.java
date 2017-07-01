package com.beejee.todos;

import com.beejee.todos.domain.Todo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by georgekankava on 30.06.17.
 */

@Controller
@ComponentScan
@EnableWebSecurity
@EnableAutoConfiguration
@EnableJpaRepositories
public class TodosController extends WebSecurityConfigurerAdapter {

    private static final int TODO_AMOUNT_PER_PAGE = 3;

    @Autowired
    private TodosService todosService;

    @RequestMapping(value = "/todo", method = RequestMethod.GET)
    public String createTodo() {
        return "todo";
    }

    @ResponseBody
    @RequestMapping(value = "/todo", method = RequestMethod.POST)
    public TodoResponse createTodo(@Valid @ModelAttribute TodoForm todoForm,
                           BindingResult result,
                           @RequestParam(value="image", required = false) MultipartFile image) throws IOException, NoSuchAlgorithmException {
        if(!result.hasErrors()) {
            if (image != null) {
                byte[] imageBytes = todosService.createResizedCopy(image.getBytes(), image.getContentType());
                todosService.addTodo(todoForm.getUsername(), todoForm.getEmail(), todoForm.getDescription(), imageBytes);
            } else {
                todosService.addTodo(todoForm.getUsername(), todoForm.getEmail(), todoForm.getDescription(), null);
            }
            return new TodoResponse("Todo saved successfully.", false);
        }
        return new TodoResponse("Todo couldn't be saved.", true);
    }

    @ResponseBody
    @RequestMapping(value = "/totalPages", produces = MediaType.APPLICATION_JSON_VALUE)
    public Long getTotalPages() {
        if(todosService.getTotalCount() % TODO_AMOUNT_PER_PAGE == 0) {
            return todosService.getTotalCount() / TODO_AMOUNT_PER_PAGE;
        }
        return todosService.getTotalCount() / TODO_AMOUNT_PER_PAGE + 1;
    }

    @RequestMapping(value = "/updateTodo", method = RequestMethod.POST)
    public void updateTodo(@RequestBody Todo todo) {
        todosService.updateTodo(todo);
    }

    private void validateImage(MultipartFile image) {
    }

    @ResponseBody
    @RequestMapping(value = "/todoes/{page}/column/{column}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Todo> retrieveTodoes(@PathVariable Integer page, @PathVariable String column) {
        return todosService.retrieveTodos(page, TODO_AMOUNT_PER_PAGE, column);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(TodosController.class, args);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/todoes/*", "/totalPages", "/todo", "/todos.html", "/resources/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                .logout()
                .permitAll();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .inMemoryAuthentication()
                .withUser("admin").password("123").roles("ADMIN");
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class TodoResponse {
        private String responseMessage;
        private boolean error;
    }

    @Getter
    @Setter
    public static class TodoForm {
        private Integer id;
        private String username;
        private String email;
        private String description;
    }
}
