package com.testTask.widgetsService;

import com.testTask.domain.Widget;
import com.testTask.storage.InMemoryStorage;
import com.testTask.widgetLogic.WLogic;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
public class WidgetController {

    public static class WidgetDTO {
        public Integer x;
        public Integer y;
        public Integer z_index;
        public Integer width;
        public Integer height;
    }

    private final WLogic logic = new WLogic(new InMemoryStorage<Widget>()); // TODO: DI

    @PutMapping("/widget")
    public int CreateNew(@RequestBody WidgetDTO dto) {
        return logic.createWidget(dto.x, dto.y, dto.z_index, dto.width, dto.height);
    }

    @GetMapping("/widget/{id}")
    public Widget get(@PathVariable int id) {
        return logic.getWidget(id);
    }

    @GetMapping("/widget")
    public List<Widget> getAll() {
        return logic.getAll();
    }

    @PostMapping("widget/{id}")
    public int Update(@PathVariable int id, @RequestBody WidgetDTO dto) {
        return logic.updateWidget(id, dto.x, dto.y, dto.z_index, dto.width, dto.height);
    }

    @DeleteMapping("/widget/{id}")
    public boolean delete(@PathVariable int id) {
        return logic.delete(id);
    }
}



