import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {OrderDto, OrderState} from "../model/order-dto";

@Component({
  selector: 'app-manage-list',
  templateUrl: './manage-list.component.html',
  styleUrls: ['./manage-list.component.css']
})
export class ManageListComponent implements OnInit {
  @Input() orders: OrderDto[]
  @Input() selectedOrder: OrderDto
  @Input() description: string
  @Output() clickedItem = new EventEmitter<OrderDto>();
  constructor() { }

  ngOnInit(): void {
  }

  getClassForListItem(order: OrderDto) {
    let classForRow: string

    if (order === this.selectedOrder) {
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

  handleClick(order: OrderDto) {
    this.clickedItem.emit(order)
  }
}
