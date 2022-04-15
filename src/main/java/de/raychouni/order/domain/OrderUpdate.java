package de.raychouni.order.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderUpdate {
    CHANGE_TYPE change;

    public OrderUpdate(CHANGE_TYPE change) {
        this.change = change;
    }

    public enum CHANGE_TYPE {
        INSERTED("inserted"),
        DELETED("deleted"),
        UPDATED("updated");

        String value;

        @Override
        public String toString() {
            return value;
        }

        CHANGE_TYPE(String value) {
            this.value = value;
        }
    }
}
