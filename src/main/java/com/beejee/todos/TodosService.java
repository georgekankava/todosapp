package com.beejee.todos;

import com.beejee.todos.domain.Todo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by georgekankava on 30.06.17.
 */
@Service
@Transactional
public class TodosService {

    @Autowired
    private TodosJpa todosJpa;

    private static final Integer scaledWidth = 320;
    private static final Integer scaledHeight = 240;

    public List<Todo> retrieveTodos(Integer page, Integer size, String column) {
        return todosJpa.findAll(new PageRequest(page, size, new Sort(Sort.Direction.ASC, column))).getContent();
    }


    public void addTodo(String username, String email, String description, byte [] image) {
        Todo todo = new Todo();
        todo.setUsername(username);
        todo.setEmail(email);
        todo.setDescription(description);
        todo.setImage(image);
        todosJpa.save(todo);
    }

    public byte[] createResizedCopy(byte[] imageInBytes, String imageType) throws IOException {
        InputStream in = new ByteArrayInputStream(imageInBytes);
        BufferedImage bImageFromConvert = ImageIO.read(in);
        if (imageInBytes.length != 0 && bImageFromConvert.getWidth() > scaledWidth && bImageFromConvert.getHeight() > scaledHeight) {
            BufferedImage resizedImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(bImageFromConvert, 0, 0, scaledWidth, scaledHeight, null);
            g.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( resizedImage, "jpg", baos );
            baos.flush();
            return baos.toByteArray();
        }
        return imageInBytes;
    }

    public Long getTotalCount() {
        return todosJpa.count();
    }

    public void updateTodo(Todo todo) {
        todosJpa.save(todo);
    }
}
