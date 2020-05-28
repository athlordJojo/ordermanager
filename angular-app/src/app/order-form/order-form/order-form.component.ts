import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {OrderService} from '../../service/order.service';
import {OrderDto} from "../../model/order-dto";

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

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService) {
    this.newOrder = new OrderDto();
    this.newOrder.state = 'IN_PROGRESS';
  }

  onSubmit() {
    this.orderService.save(this.newOrder).subscribe(result => {
      this.newOrder.scoreBoardNumber = null;
      this.loadData();
    });
  }

  loadData() {
    this.orderService.findAll().subscribe(data => {
      this.orders = data.filter(o => o.state !== "DONE");
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
    this.orderService.update(this.selectedOrder).subscribe(data => this.loadData());
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
        case "DONE": {
          console.log("DARK")
          console.log(order)
          classForRow = "list-group-item-dark"
          break;
        }
      }
    }
    return classForRow;
  }
}
