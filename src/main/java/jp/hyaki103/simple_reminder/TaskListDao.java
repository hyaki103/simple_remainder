package jp.hyaki103.simple_reminder;

import jp.hyaki103.simple_reminder.HomeController.TaskItems;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TaskListDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    TaskListDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void add(TaskItems taskItem) {
        SqlParameterSource param = new BeanPropertySqlParameterSource(taskItem);
        SimpleJdbcInsert insert =
                new SimpleJdbcInsert(jdbcTemplate)
                        .withTableName("tasklist");
        insert.execute(param);
    }

    public List<TaskItems> findAll() {
        String query = "SELECT * FROM tasklist";
        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<Map<String, Object>> result = jdbcTemplate.queryForList(query);
        List<TaskItems> taskItems = result.stream()
                .map((Map<String, Object> row) -> {
                    try {
                        return new TaskItems(
                                row.get("id").toString(),
                                row.get("task").toString(),
                                sdFormat.parse(row.get("deadline").toString()),
                                (Boolean)row.get("done"));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();
        return taskItems;
    }

    public int delete(String id) {
        int number = jdbcTemplate.update("DELETE FROM tasklist WHERE id = ?", id);
        return number;
    }

    public int update(TaskItems taskItem) {
        int number = jdbcTemplate.update(
                "UPDATE tasklist SET task = ?, deadline = ?, done = ? WHERE id = ?",
                taskItem.task(),
                taskItem.deadline(),
                taskItem.done(),
                taskItem.id());
        return number;
    }
}
