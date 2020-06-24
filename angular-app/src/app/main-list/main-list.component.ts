import {Component, Input, OnInit} from '@angular/core';
import {OrderDto, OrderState} from "../model/order-dto";

@Component({
  selector: 'app-main-list',
  templateUrl: './main-list.component.html',
  styleUrls: ['./main-list.component.css']
})
export class MainListComponent implements OnInit {
  private _ordersOfOrders: Array<Array<OrderDto>>
  visualizedState: OrderState;
  @Input() description: string

  constructor() {
  }

  ngOnInit(): void {
  }

  @Input()
  set ordersOfOrders(ordersOfOrders: Array<Array<OrderDto>>) {
    this._ordersOfOrders = ordersOfOrders
    if (ordersOfOrders.length > 0 && ordersOfOrders[0].length > 0) {
      this.visualizedState = ordersOfOrders[0][0].state;
    }
  }

  get ordersOfOrders(): Array<Array<OrderDto>> {
    return this._ordersOfOrders
  }
}
