package de.raychouni.ordernotifier.services;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class OrderUpdate{
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
