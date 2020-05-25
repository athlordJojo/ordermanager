import { Component, OnInit } from '@angular/core';
import {OrderService} from "../../service/order.service";
import {OrderDto} from "../../model/order-dto";

@Component({
  selector: 'app-order-list',
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.css']
})
export class OrderListComponent implements OnInit {

  constructor(private orderService: OrderService) {
  }

  orders: OrderDto[];

  ngOnInit() {
    this.orderService.findAll().subscribe(data => {
      this.orders = data;
    });
  }

}
