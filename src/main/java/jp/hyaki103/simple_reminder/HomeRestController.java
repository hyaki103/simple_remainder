package jp.hyaki103.simple_reminder;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class HomeRestController {

    // task管理に必要な情報をレコードとして保持
    record TaskItem(String id, String task, String deadline, boolean done)  {}
    // taskは複数にまたがるため、格納するフィールドを用意する
    private List<TaskItem> taskItems = new ArrayList<>();

    @RequestMapping(value = "/resthello")
    //@RequestMapping("/resthello")
    String hello() {
        return """
                Hello
                It Works!
                現在時刻は%sです
                """.formatted(LocalDateTime.now());
    }

    /**
     *
     * @param task 追加したいタスク
     * @param deadline 追加したい締め切り
     * @return 追加完了
     */
    @GetMapping("/restadd")
    //@RequestMapping(value="/restadd", method=RequestMethod.GET)
    String addItem(@RequestParam("task") String task,
                   @RequestParam("deadline") String deadline) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        TaskItem item = new TaskItem(id, task, deadline, false);
        taskItems.add(item);

        return "タスクを追加しました";
    }

    @GetMapping("/restlist")
    String listItems() {
        String result = taskItems.stream()
                .map(TaskItem::toString)
                .collect(Collectors.joining(", "));
        return result;
    }


}