package jp.hyaki103.simple_reminder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {
    record TaskItems(String id, String task, String deadline, boolean done){}
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
                   @RequestParam("deadline") String deadline) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        TaskItems item = new TaskItems(id, task, deadline, false);
        // taskItems.add(item);
        dao.add(item);

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
                      @RequestParam("deadline") String deadline,
                      @RequestParam("done") boolean done) {
        TaskItems taskItem = new TaskItems(id, task, deadline, done);
        dao.update(taskItem);
        return "redirect:/list";
    }
}
