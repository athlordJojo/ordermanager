import {Component, OnInit} from '@angular/core';
import {OrderApi} from '../api/order-api.service';
import {OrderDto} from "../model/order-dto";

@Component({
  selector: 'app-order-form',
  templateUrl: './order-form.component.html',
  styleUrls: ['./order-form.component.css']
})
export class OrderFormComponent implements OnInit {

  ngOnInit(): void {
    this.loadData();
  }

  newOrder: OrderDto;
  selectedOrder: OrderDto;
  orders: OrderDto[] = [];
  newScoreBoardNumberOfNewOrder:number;
  constructor(
    private orderApi: OrderApi) {
  }

  onSubmit() {
    this.orderApi.save(this.newScoreBoardNumberOfNewOrder).subscribe(result => {
      this.newScoreBoardNumberOfNewOrder = null;
      this.loadData();
    });
  }

  loadData() {
    this.orderApi.findAll().subscribe(data => {
      this.orders = data;
      this.orders.sort((o1, o2) => {
        return o1.state.localeCompare(o2.state.toString())
      });
    })
  }

  selectOrder(selectedOrder: OrderDto) {
    // this toggles the selected item
    if (this.selectedOrder && this.selectedOrder.uuid == selectedOrder.uuid) {
      this.selectedOrder = null;
    } else {
      this.selectedOrder = selectedOrder;
    }
  }

  changeStateOfSelectedOrder(state: string) {
    this.selectedOrder.state = state;
    this.orderApi.update(this.selectedOrder).subscribe(data => this.loadData());
  }

  deleteSelectedOrder(order: OrderDto) {
    this.orderApi.delete(order).subscribe(
      data => {
        this.loadData();
      });
  }

  getClassForListItem(order: OrderDto) {
    let classForRow: string

    if (order === this.selectedOrder) {
      classForRow = "list-group-item-primary";
    } else {
      switch (order.state) {
        case "IN_PROGRESS": {
          classForRow = "list-group-item-danger"
          break;
        }
        case "READY": {
          classForRow = "list-group-item-success"
          break;
        }
      }
    }
    return classForRow;
  }
}
