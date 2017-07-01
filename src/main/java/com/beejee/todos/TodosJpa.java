package com.beejee.todos;

import com.beejee.todos.domain.Todo;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by georgekankava on 30.06.17.
 */
@Repository
public interface TodosJpa extends PagingAndSortingRepository<Todo, Integer> {}
