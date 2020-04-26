package com.testTask.widgetsService;

import com.testTask.domain.Widget;
import com.testTask.widgetLogic.WLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class WidgetController {

    public static class WidgetDTOInput {
        public Integer x;
        public Integer y;
        public Integer z_index;
        public Integer width;
        public Integer height;
    }

    public static class WidgetDTOOutput {
        public Integer id;
        public Integer x;
        public Integer y;
        public Integer z_index;
        public Integer width;
        public Integer height;
        public LocalDateTime lastModificationDateTime;

        public WidgetDTOOutput(Widget entity) {
            this.id = entity.getId();
            this.x = entity.getX();
            this.y = entity.getY();
            this.z_index = entity.getZ_index();
            this.width = entity.getWidth();
            this.height = entity.getHeight();
            this.lastModificationDateTime = entity.getLastModificationDateTime();
        }
    }


    @Autowired
    private WLogic logic = new WLogic();


    @PutMapping("/widget")
    public WidgetDTOOutput CreateNew(@RequestBody WidgetDTOInput dto) {
        return new WidgetDTOOutput(logic.createWidget(dto.x, dto.y, dto.z_index, dto.width, dto.height));
    }

    @GetMapping("/widget/{id}")
    public WidgetDTOOutput get(@PathVariable int id) {
        return new WidgetDTOOutput(logic.getWidget(id));
    }

    @GetMapping("/widget")
    public List<WidgetDTOOutput> getAll(@RequestParam(name = "limit", defaultValue = "10") int limit,
                               @RequestParam(name = "page", defaultValue = "1") int page) {
        return logic.getPage(limit, page).stream().map((w) -> new WidgetDTOOutput(w)).collect(Collectors.toList());
    }

    @PostMapping("/widget/{id}")
    public WidgetDTOOutput Update(@PathVariable int id, @RequestBody WidgetDTOInput dto) {
        return new WidgetDTOOutput(logic.updateWidget(id, dto.x, dto.y, dto.z_index, dto.width, dto.height));
    }

    @DeleteMapping("/widget/{id}")
    public boolean delete(@PathVariable int id) {
        return logic.delete(id);
    }
}



