import {Component, OnDestroy, OnInit} from '@angular/core';
import {OrderApi} from "../../service/order-api.service";
import {OrderDto} from "../../model/order-dto";
import {timer} from 'rxjs';

@Component({
  selector: 'app-order-list',
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.css']
})
export class OrderListComponent implements OnInit, OnDestroy {
  subscription;

  ngOnDestroy(): void {
    console.log("Cancel timer.")
    this.subscription.unsubscribe();
  }

  constructor(private orderService: OrderApi) {
    const t = timer(2000, 4000);
    this.subscription = t.subscribe(v => {
      this.loadData();
    });
  }

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

      this.inProgressOrders = data.filter(order => order.state == 'IN_PROGRESS').sort(sortOrderByModifiedDate);
      this.readyOrders = data.filter(order => order.state == 'READY').sort(sortOrderByModifiedDate);
      this.scoreBoardNumberOflastReadyOrder = this.readyOrders.length > 0 ? this.readyOrders[0].scoreBoardNumber.toString() : '-';
    });
  }
}
