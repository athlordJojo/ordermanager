package de.raychouni.ordernotifier.services;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OrderUpdate extends ApplicationEvent {
    CHANGE_TYPE change;

    public OrderUpdate(Object source, CHANGE_TYPE change) {
        super(source);
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
