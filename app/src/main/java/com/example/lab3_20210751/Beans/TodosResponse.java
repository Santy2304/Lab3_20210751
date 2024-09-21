package com.example.lab3_20210751.Beans;

import java.io.Serializable;
import java.util.List;

public class TodosResponse implements Serializable {
    private List<ToDo> todos;

    public List<ToDo> getTodos() {
        return todos;
    }

    public void setTodos(List<ToDo> todos) {
        this.todos = todos;
    }
}
