package greencity.entity;

import greencity.entity.localization.ShoppingListItemTranslation;
import lombok.*;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "languages")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(
    exclude = {"adviceTranslations", "shoppingListItemTranslations", "habitTranslations", "factOfTheDayTranslations"})
@ToString(
    exclude = {"adviceTranslations", "shoppingListItemTranslations", "habitTranslations", "factOfTheDayTranslations"})
@Builder
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 35)
    private String code;

    @OneToMany(mappedBy = "language", fetch = FetchType.LAZY)
    private List<HabitTranslation> habitTranslations;

    @OneToMany(mappedBy = "language", fetch = FetchType.LAZY)
    private List<ShoppingListItemTranslation> shoppingListItemTranslations;
}
