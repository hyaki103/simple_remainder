package jp.hyaki103.simple_reminder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {
    record TaskItems(
            @NotBlank String id,
            @NotNull @Size(min = 2) String task,
            @NotNull @Past Date deadline,
            boolean done) {}
    private List<TaskItems> taskItems = new ArrayList<>();
    private final TaskListDao dao;

    @Autowired
    HomeController(TaskListDao dao) {
        this.dao = dao;
    }

    @GetMapping("/list")
    String listItems(Model model) {
        List<TaskItems> taskItems = dao.findAll();
        model.addAttribute("taskList", taskItems);
        return "home";
    }

    @GetMapping("/add")
    String addItem(@RequestParam("task") String task,
                   @RequestParam("deadline") String strDeadline) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        // String型でHTMLから受け取ったdeadlineの型変換を行う
        try {
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date deadline = sdFormat.parse(strDeadline);
            TaskItems item = new TaskItems(id, task, deadline, false);
            // taskItems.add(item);
            dao.add(item);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


        return "redirect:/list";
    }

    @RequestMapping(value = "/hello")
    String Hello(Model model) {
        model.addAttribute("time", LocalDateTime.now());
        return "hello";
    }

    @GetMapping(value = "/delete")
    String deleteItem(@RequestParam("id") String id) {
        dao.delete(id);
        return "redirect:/list";
    }

    @GetMapping(value = "/update")
    String updateItem(@RequestParam("id") String id,
                      @RequestParam("task") String task,
                      @RequestParam("deadline") String strDeadline,
                      @RequestParam("done") boolean done) {
        try {
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date deadline = sdFormat.parse(strDeadline);
            TaskItems taskItem = new TaskItems(id, task, deadline, done);
            dao.update(taskItem);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/list";
    }
}
