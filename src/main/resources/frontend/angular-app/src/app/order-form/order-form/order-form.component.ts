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
  orders: OrderDto[];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService) {
    this.newOrder = new OrderDto();
    this.newOrder.state = 'IN_PROGRESS';
  }

  onSubmit() {
    this.orderService.save(this.newOrder).subscribe(result => this.gotoUserList());
    this.newOrder.scoreBoardNumber = null;
    this.loadData();
  }

  gotoUserList() {
    // this.router.navigate(['/orders']);
  }

  loadData() {
    this.orderService.findAll().subscribe(data => {
      this.orders = data
      this.orders.sort((o1, o2) => {
        return o1.state.localeCompare(o2.state.toString())
      });
    })
  }

  selectOrder(selectedOrder: OrderDto) {
    this.selectedOrder = selectedOrder;
    console.log("Clicked:", selectedOrder);
  }

  changeStateOfSelectedOrder(state: string) {
    this.selectedOrder.state = state;
    this.orderService.update(this.selectedOrder).subscribe(data => this.loadData());
  }

}
