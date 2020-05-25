import {Component, OnInit} from '@angular/core';
import {OrderService} from "../../service/order.service";
import {OrderDto} from "../../model/order-dto";
import {timer} from 'rxjs';

@Component({
  selector: 'app-order-list',
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.css']
})
export class OrderListComponent implements OnInit {

  constructor(private orderService: OrderService) {
    const t = timer(2000, 4000);
    t.subscribe(v => {
      this.loadData();
    })
  }

  orders: OrderDto[];
  scoreBoardNumberOflastReadyOrder: string;
  readyOrders: OrderDto[];
  inProgressOrders: OrderDto[];

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.orderService.findAll().subscribe(data => {
      function sortOrderByModifiedDate(order1: OrderDto, order2: OrderDto): number {
        if (order1.lastModifiedDate < order2.lastModifiedDate) {
          // order1 is older
          return 1;
        } else if (order1.lastModifiedDate > order2.lastModifiedDate) {
          // order2 is older
          return -1;
        }
        // equal
        return 0;
      }

      this.orders = data;
      this.inProgressOrders = this.orders.filter(order => order.state == 'IN_PROGRESS').sort(sortOrderByModifiedDate);
      this.readyOrders = this.orders.filter(order => order.state == 'READY').sort(sortOrderByModifiedDate);
      this.scoreBoardNumberOflastReadyOrder = this.readyOrders.length > 0 ? this.readyOrders[0].scoreBoardNumber.toString() : '-';
    });
  }
}
