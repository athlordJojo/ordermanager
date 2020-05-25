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
  }

  order: OrderDto;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService) {
    this.order = new OrderDto();
    this.order.state = 'IN_PROGRESS';
  }

  onSubmit() {
    this.orderService.save(this.order).subscribe(result => this.gotoUserList());
  }

  gotoUserList() {
    this.router.navigate(['/orders']);
  }

}
