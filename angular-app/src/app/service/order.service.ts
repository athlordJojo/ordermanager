import {Injectable} from '@angular/core';
import {OrderDto} from "../model/order-dto";

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  constructor() {
  }

  existOrderWithScoreboardNumber(orders:OrderDto[], scoredboardNumber:number){
    return orders.find(order => order.scoreBoardNumber == scoredboardNumber) != null;
  }

  filterAndSort(orders: OrderDto[], state: string) {
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

    return orders.filter(order => order.state == state).sort(sortOrderByModifiedDate);
  }
}
