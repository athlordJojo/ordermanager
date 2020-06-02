package de.raychouni.ordernotifier.services;

import lombok.Getter;

@Getter
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
