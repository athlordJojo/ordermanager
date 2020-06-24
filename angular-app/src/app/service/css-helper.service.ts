import { Injectable } from '@angular/core';
import {OrderDto, OrderState} from "../model/order-dto";

@Injectable({
  providedIn: 'root'
})
export class CssHelperService {

  constructor() { }

  getClassForListItem(order: OrderDto, selectedOrder: OrderDto) {
    let classForRow: string

    if (order === selectedOrder) {
      classForRow = "list-group-item-primary active";
    } else {
      switch (order.state) {
        case OrderState.IN_PROGRESS: {
          classForRow = "list-group-item-secondary"
          break;
        }
        case OrderState.READY: {
          classForRow = "list-group-item-success"
          break;
        }
      }
    }
    return classForRow;
  }

}
