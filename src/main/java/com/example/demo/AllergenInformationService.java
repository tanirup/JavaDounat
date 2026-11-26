package com.example.demo;

import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AllergenInformationService {
    private final JdbcTemplate jdbc;

    public AllergenInformationService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public AllergenInformation forItem(Item item) {
        if (item.isAllergenInfoVerified()) {
            var names = jdbc.queryForList("SELECT a.name FROM allergens a JOIN item_allergens ia "
                    + "ON ia.allergen_id = a.id WHERE ia.item_id = ? ORDER BY a.id", String.class, item.getId());
            return new AllergenInformation(names, false);
        }
        // 公式資料のイーストドーナツ類を参考にした例。自店商品の原材料とは区別する。
        return new AllergenInformation(List.of("小麦", "卵", "乳成分", "大豆"), true);
    }

    public record AllergenInformation(List<String> names, boolean reference) {}
}
