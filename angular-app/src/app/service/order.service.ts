import {Injectable} from '@angular/core';
import {OrderDto, OrderState} from "../model/order-dto";

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  constructor() {
  }

  existOrderWithScoreboardNumber(orders: OrderDto[], scoredboardNumber: number): boolean {
    return orders.find(order => order.scoreBoardNumber == scoredboardNumber) != null;
  }

  sortByScoreBoardNumber(orders: OrderDto[]) {
    return orders.sort((a, b) => {
      if (a.scoreBoardNumber < b.scoreBoardNumber) {
        return -1;
      } else if (a.scoreBoardNumber > b.scoreBoardNumber) {
        return 1;
      } else {
        return 0;
      }
    });
  }

  filterAndSortByModifiedDate(orders: OrderDto[], state: OrderState) {
    function sortOrderByModifiedDate(order1: OrderDto, order2: OrderDto): number {
      if (order1.lastModifiedDate < order2.lastModifiedDate) {
        // order1 is older
        return -1;
      } else if (order1.lastModifiedDate > order2.lastModifiedDate) {
        // order2 is older
        return 1;
      }
      // equal
      return 0;
    }

    return orders.filter(order => order.state == state).sort(sortOrderByModifiedDate);
  }

  sliceIntoSubArrays(orders: OrderDto[], chunk = 4): Array<Array<OrderDto>> {
    let i, j, temparray;
    let ordersOfOrders = Array<Array<OrderDto>>();
    for (i = 0, j = orders.length; i < j; i += chunk) {
      temparray = orders.slice(i, i + chunk);
      ordersOfOrders.push(temparray);
    }

    return ordersOfOrders;
  }
}
